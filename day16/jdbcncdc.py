#!/usr/bin/python3

from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from py4j.java_gateway import java_import
  
spark = SparkSession.builder\
	.appName("jdbcncdc")\
	.config("spark.sql.shuffle.partitions", "2")\
	.getOrCreate()

gw = spark.sparkContext._gateway
java_import(gw.jvm, "com.sunbeam.HiveQuoteDialect")
dialect = gw.jvm.com.sunbeam.HiveQuoteDialect()
gw.jvm.org.apache.spark.sql.jdbc.JdbcDialects.registerDialect(dialect)

db_url = "jdbc:hive2://localhost:10000/edbda"
db_user = "sunbeam"
db_password = " "
db_table="ncdc_orc"
db_driver="org.apache.hive.jdbc.HiveDriver"

temp = spark.read\
	.option("fetchsize", "10")\
	.option("driver", db_driver)\
	.option("user", db_user)\
	.option("password", db_password)\
	.jdbc(url=db_url, table=db_table)
temp.printSchema()

result = temp\
	.withColumnRenamed("ncdc_orc.year", "yr")\
	.withColumnRenamed("ncdc_orc.temperature", "temp")\
	.withColumnRenamed("ncdc_orc.quality", "quality")\
	.where("quality IN (0, 1, 4, 5, 9) AND temp != 9999")\
	.groupBy("yr").agg(avg("temp").alias("avgtemp"))\
	.orderBy("avgtemp", ascending=False)

result.show(truncate=False)

result.explain(extended=True)

spark.stop()

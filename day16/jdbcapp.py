#!/usr/bin/python3

from pyspark.sql import SparkSession
from py4j.java_gateway import java_import
  
spark = SparkSession.builder\
	.appName("jdbcapp")\
	.enableHiveSupport()\
	.getOrCreate()

gw = spark.sparkContext._gateway
java_import(gw.jvm, "com.sunbeam.HiveQuoteDialect")
dialect = gw.jvm.com.sunbeam.HiveQuoteDialect()
gw.jvm.org.apache.spark.sql.jdbc.JdbcDialects.registerDialect(dialect)

db_url = "jdbc:hive2://localhost:10000/edbda"
db_user = "sunbeam"
db_password = " "
db_table="dept"
db_driver="org.apache.hive.jdbc.HiveDriver"

temp = spark.read\
	.option("fetchsize", "10")\
	.option("driver", db_driver)\
	.option("user", db_user)\
	.option("password", db_password)\
	.jdbc(url=db_url, table=db_table)
temp.printSchema()

result = temp\
	.limit(10)
result.show(truncate=False)

result.explain(extended=True)

spark.stop()



#!/usr/bin/python3
from pyspark.sql import SparkSession
from pyspark.sql.functions import *

# create SparkSession object
spark = SparkSession.builder\
            .appName("Ncdc")\
            .config("spark.sql.shuffle.partitions", "2")\
            .getOrCreate()

ncdc = spark.read\
    .text("file:///D:/may21/dbda/bigdata/data/ncdc")
ncdc.printSchema()

regex = "^.{15}([0-9]{4}).{68}([-\\+][0-9]{4})([0-9]).*$"

temps = ncdc\
    .withColumn("yr", regexp_extract("value", regex, 1).cast("INT"))\
    .withColumn("temp", regexp_extract("value", regex, 2).cast("INT"))\
    .withColumn("quality", regexp_extract("value", regex, 3).cast("INT"))\
    .drop("value")

temps.printSchema()

db_url = "jdbc:mysql://localhost:3306/sales"
db_user = "dbda"
db_password = "dbda"
db_table = "ncdc"
# mysql> USE sales;
# mysql> CREATE TABLE ncdc(year INT, temp INT, quality INT);

temps.write\
    .option("user", db_user)\
    .option("password", db_password)\
    .mode("OVERWRITE")\
    .jdbc(db_url, db_table)

spark.stop()


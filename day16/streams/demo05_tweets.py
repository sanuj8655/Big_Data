#!/usr/bin/python3

from pyspark.sql import SparkSession
from pyspark.sql.functions import *

spark = SparkSession.builder\
    .config("spark.sql.shuffle.partitions", "2")\
    .master("local[2]")\
    .appName("socket_stream")\
    .getOrCreate()

tweetsPath = "file:///D:/tmp/tweets_new"
tweetsSchema = "id STRING, time STRING, text STRING"

tweets = spark.readStream\
    .schema(tweetsSchema)\
    .json(tweetsPath)\
    .withColumn("ttime", to_timestamp(col("time").cast("BIGINT") / 1000))\
    .drop("time")

result = tweets\
    .selectExpr("explode(split(lower(text), '[ \\.!\\?,\t\r\n]')) as word")\
    .where("word LIKE '#%'")\
    .groupby("word").count()\
    .orderBy(desc("count"))\
    .limit(20)

query = result.writeStream\
    .format("console")\
    .outputMode("complete")\
    .start()

query.awaitTermination()

spark.stop()


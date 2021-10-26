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
trendingPath = "file:///D:/tmp/trending"

tweets = spark.readStream\
    .schema(tweetsSchema)\
    .json(tweetsPath)\
    .withColumn("ttime", to_timestamp(col("time").cast("BIGINT") / 1000))\
    .drop("time")

# sentiment analysis -- generate score for each tweet.
result = tweets\
    .withWatermark("ttime", "20 seconds")

query = result.writeStream\
    .trigger(processingTime="10 seconds")\
    .format("csv")\
    .option("path", trendingPath)\
    .option("checkpointLocation", "file:///D:/tmp/checks")\
    .outputMode("append")\
    .start()

print("ready ...")
query.awaitTermination()

spark.stop()


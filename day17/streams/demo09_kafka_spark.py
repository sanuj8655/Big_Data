#!/usr/bin/python3

from pyspark.sql import SparkSession
from pyspark.sql.functions import *

spark = SparkSession.builder\
            .config("spark.sql.shuffle.partitions", "2")\
            .appName("kafka_spark")\
            .master("local[4]")\
            .getOrCreate()

topic = "iot"
# source = kafka
data = spark.readStream\
            .format("kafka")\
            .option("kafka.bootstrap.servers", "localhost:9092")\
            .option("subscribe", topic)\
            .option("failOnDataLoss", "false")\
            .load()

data.printSchema()

# processing
readingSchema = "time STRING, sensor STRING, reading STRING"
result = data\
    .selectExpr("CAST(value AS string) AS val")\
    .select(from_json("val", readingSchema).alias("v"))\
    .select(expr("CAST(v.time AS timestamp)"), expr("v.sensor"), expr("CAST(v.reading AS double)"))\
    .withWatermark("time", "10 seconds")\
    .groupby("sensor", window("time", windowDuration="20 seconds", slideDuration="5 seconds"))\
    .agg(avg("reading").alias("avg_reading"))\
    .selectExpr("(sensor, window.start AS start, window.end AS end, avg_reading) AS result")\
    .select(to_json("result").alias("value"))

result.printSchema()

# (job 1) sink = console
query1 = result.writeStream\
    .format("console")\
    .option("truncate", "false")\
    .outputMode("append")\
    .start()

# (job 2) sink = kafka
query2 = result.writeStream\
    .format("kafka")\
    .option("topic", "avgiot")\
    .option("kafka.bootstrap.servers", "localhost:9092")\
    .option("checkpointLocation", "/home/anuj/Documents/iotchecks")\
    .start()

spark.streams.awaitAnyTermination()

spark.stop()

from pyspark.sql import SparkSession
from pyspark.sql.functions import *

# create SparkSession object
spark = SparkSession.builder\
            .appName("Ncdc")\
            .config("spark.sql.shuffle.partitions", "2")\
            .getOrCreate()

tweetsSchema = "id STRING, time STRING, text STRING"

tweets = spark.read\
    .schema(tweetsSchema)\
    .json("file:///D:/tmp/tweets/")\
    .withColumn("tweetid", col("id").cast("BIGINT"))\
    .withColumn("tweetdate", to_date(to_timestamp(col("time").cast("BIGINT"))))\
    .withColumn("tweetyear", year("tweetdate"))\
    .withColumn("tweetmonth", month("tweetdate"))\
    .withColumn("tweetdate", dayofmonth("tweetdate"))\
    .drop("id", "time")

tweets.printSchema()

tweets.show(truncate=True)

spark.stop()
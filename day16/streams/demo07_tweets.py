# data-bricks cloud

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
    .orderBy("count", ascending=False)\
    .limit(20)

query = result.writeStream\
    .trigger(processingTime='10 seconds')\
    .format("memory") \
    .queryName("count_result")\
    .outputMode("complete")\
    .start()

%sql
SELECT * FROM count_result;

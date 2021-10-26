#!/usr/bin/python3

from pyspark.sql import SparkSession

spark = SparkSession.builder\
    .config("spark.sql.shuffle.partitions", "2")\
    .master("local[2]")\
    .appName("socket_stream")\
    .getOrCreate()

# stream source
data = spark.readStream\
            .format("socket")\
            .option("host", "192.168.1.4")\
            .option("port", "4444")\
            .load()
data.printSchema()

# stream processing
result = data\
        .selectExpr("explode(split(lower(value), '[^a-z]')) as word")\
        .filter("word not in ('', 'in', 'a', 'the', 'shall', 'this', 'of', 'any', 'in')")

# stream sink
query = result.writeStream\
            .trigger(processingTime="10 seconds")\
            .format("console")\
            .outputMode("append")\
            .start()

print("waiting for termination ...")
query.awaitTermination()
spark.stop()



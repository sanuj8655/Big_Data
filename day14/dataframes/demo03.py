#!/usr/bin/python3

from pyspark.sql import SparkSession

# create SparkSession object
spark = SparkSession.builder\
            .appName("BookSummary")\
            .config("spark.sql.shuffle.partitions", "2")\
            .getOrCreate()

books = spark.read\
            .option("header", "true")\
            .option("inferSchema", "true")\
            .csv("file:///D:/may21/dbda/bigdata/data/books_hdr.csv")

result = books\
        .groupby("subject").sum("price")\
        .where("subject IN ('C Programming', 'C++ Programming')")

result.show(truncate=False)

result.explain(extended=True)

spark.stop()



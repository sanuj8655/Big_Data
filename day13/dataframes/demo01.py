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
books.printSchema()

result = books\
        .select("subject", "price")\
        .groupby("subject").sum("price")\
        .orderBy("sum(price)", ascending=False)\
        .limit(2)

result.show(n=10, truncate=False) # action

input("press enter to exit ...")

# stop spark session
spark.stop()




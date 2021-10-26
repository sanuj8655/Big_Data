from pyspark.sql import SparkSession
from pyspark.sql.functions import *

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

# SELECT subject, SUM(price) FROM books GROUP BY subject ORDER BY SUM(price) DESC LIMIT 2
result1 = books\
        .groupby("subject").sum("price")\
        .orderBy("sum(price)", ascending=False)\
        .limit(2)
result1.show(n=10, truncate=False) # action
result1.explain(extended=True)

books.createOrReplaceTempView("v_books")
result2 = spark.sql("SELECT subject, SUM(price) FROM v_books "
                    "GROUP BY subject "
                    "ORDER BY SUM(price) DESC "
                    "LIMIT 2")
result2.show(n=10, truncate=False) # action
result2.explain(extended=True)

spark.stop()

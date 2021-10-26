#!/usr/bin/python3

from pyspark.sql import SparkSession

# create SparkSession object
spark = SparkSession.builder\
            .appName("TopMovies")\
            .config("spark.sql.shuffle.partitions", "2")\
            .getOrCreate()

movies = spark.read\
            .option("header", "true")\
            .option("inferSchema", "true")\
            .option("delimiter", ",")\
            .csv("file:///D:/may21/dbda/vm_share/movies/movies.csv")
movies.printSchema()

ratings = spark.read\
            .option("header", "true")\
            .option("inferSchema", "true")\
            .option("delimiter", ",")\
            .csv("file:///D:/may21/dbda/vm_share/movies/ratings.csv")
ratings.printSchema()

result = ratings\
            .groupby("movieId").count()\
            .join(movies, [ratings.movieId == movies.movieId]) \
            .orderBy("count", ascending=False) \
            .limit(10)

result.explain(True)

result.show(truncate=False)


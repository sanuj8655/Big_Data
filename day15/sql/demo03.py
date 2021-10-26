from pyspark.sql import SparkSession
from pyspark.sql.functions import *

# create SparkSession object
spark = SparkSession.builder\
            .appName("Movies")\
            .config("spark.sql.shuffle.partitions", "2")\
            .getOrCreate()

movies = spark.read\
            .option("header", "true")\
            .option("inferSchema", "true")\
            .csv("file:///D:/may21/dbda/vm_share/movies/movies.csv")

movies.createOrReplaceTempView("v_movies")

movie_genres = spark.sql("SELECT movieId, title, EXPLODE(SPLIT(genres, '[|]')) genre FROM v_movies LIMIT 20")
# movie_genres.show(truncate=False)
# movie_genres.cache()

movie_genres.createOrReplaceTempView("v_movie_genres")

result = spark.sql("SELECT genre, COUNT(movieId) cnt, CONCAT_WS(',', COLLECT_LIST(title)) titles FROM v_movie_genres GROUP BY genre")

result.show(truncate=False)

spark.stop()

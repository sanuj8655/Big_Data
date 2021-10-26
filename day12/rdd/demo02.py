#!/usr/bin/python3

## display top 10 movies (movies with max number of ratings).

from pyspark import SparkConf, SparkContext

config = SparkConf()\
    .setAppName("TopMovies")

sc = SparkContext(conf=config)
ratingsFilePath = 'file:///D:/may21/dbda/vm_share/movies/ratings.csv'
moviesFilePath = 'file:///D:/may21/dbda/vm_share/movies/movies_caret.csv'


def parseRating(line):
    try:
        parts = line.split(',')
        return (int(parts[0]), int(parts[1]), float(parts[2]), int(parts[3]))
    except:
        return ()


def parseMovie(line):
    try:
        parts = line.split('^')
        return (int(parts[0]), parts[1], parts[2])
    except:
        return ()


ratings = sc.textFile(ratingsFilePath)\
            .map(lambda line: parseRating(line))\
            .filter(lambda tup: len(tup) > 0)\
            .map(lambda tup: (tup[1], 1))\
            .reduceByKey(lambda a,b: a + b)
# ratings RDD -- key value pair rdd -- (movieId, count)

movies = sc.textFile(moviesFilePath)\
            .map(lambda line: parseMovie(line))\
            .filter(lambda tup: len(tup) > 0)\
            .map(lambda tup: (tup[0], tup[1]))
# movies RDD -- key value pair rdd -- (movieId, title)

result = ratings.join(movies)\
            .sortBy(lambda tup: tup[1][0], ascending=False)\
            .map(lambda tup: (tup[0], tup[1][0], tup[1][1]))\
            .take(20)

# ratings.join(movies) return RDD -- key-value pair RDD -- (movieId, (count,title) )
#      key: movieId (key from both RDDs)
#      value: tuple -- (count - value from ratings RDD, title - value from movies RDD)


for item in result:
    print(item)

input('press enter to exit ...')

sc.stop()

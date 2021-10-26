#!/usr/bin/python3

## display top-most movies (movies with max number of ratings).

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


topRating = sc.textFile(ratingsFilePath)\
            .map(lambda line: parseRating(line))\
            .filter(lambda tup: len(tup) > 0)\
            .map(lambda tup: (tup[1], 1))\
            .reduceByKey(lambda a,b: a + b)\
            .sortBy(lambda tup: tup[1], ascending=False)\
            .first()
# topRating tuple -- (topmost-movieId, count)
print(topRating)

movies = sc.textFile(moviesFilePath)\
            .map(lambda line: parseMovie(line))\
            .filter(lambda tup: len(tup) > 0)\
            .map(lambda tup: (tup[0], tup[1]))
# movies RDD -- key value pair rdd -- (movieId, title)

result = movies.lookup(topRating[0])
print(result)

input('press enter to exit ...')

sc.stop()

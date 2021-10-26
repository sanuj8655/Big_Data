#!/usr/bin/python3

from pyspark import SparkConf, SparkContext

config = SparkConf()\
    .setAppName("WordCount")

# may read the stop words from some text file on "client" machine.
stopWordList = ["", "is", "are", "the", "in", "by", "of"]

sc = SparkContext(conf=config)
filePath = 'file:///D:/setup/spark-3.0.1-bin-hadoop3.2/LICENSE'

# broadcast (copy) stooWordList on all the workers -- similar to Map side joins
stopWords = sc.broadcast(stopWordList)

# accumulator to count number of articles a, an, the.
articleCount = sc.accumulator(0)


def countArticle(word):
    if word in ['a', 'an', 'the']:
        articleCount.add(1)
    return word


result = sc.textFile("file:///D:/setup/spark-3.0.1-bin-hadoop3.2/LICENSE")\
            .map(lambda line: line.lower())\
            .flatMap(lambda line: line.split())\
            .map(lambda word: countArticle(word))\
            .filter(lambda word: word not in stopWords.value)\
            .map(lambda word: (word, 1))\
            .reduceByKey(lambda a,b: a + b)\
            .sortBy(lambda tup: tup[1], ascending=False)\
            .take(20)

# print result
print(result)

# print accumulator value
print("Article Count: ", articleCount.value)

sc.stop()

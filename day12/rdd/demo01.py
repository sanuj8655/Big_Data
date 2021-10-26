#!/usr/bin/python3

# SELECT subject, SUM(price) FROM books GROUP BY subject;

from pyspark import SparkConf, SparkContext

config = SparkConf()\
    .setAppName("BookSummary")

sc = SparkContext(conf=config)
filePath = 'file:///D:/may21/dbda/bigdata/data/books.csv'


def parseBook(line):
    try:
        parts = line.split(',')
        subject = parts[3]
        price = float(parts[4])
        return (subject, price)
    except:
        return ()


books = sc.textFile(filePath)

result = books.map(lambda line: parseBook(line))\
            .filter(lambda tup: len(tup) > 0)\
            .reduceByKey(lambda a,b: a + b)\
            .collect()

for item in result:
    print(item)

input('press enter to exit.')
# Browser: http://localhost:4040

sc.stop()














#!/usr/bin/python3

## display top-most movies (movies with max number of ratings).

from pyspark import SparkConf, SparkContext

config = SparkConf()\
    .setMaster("local[3]")\
    .setAppName("RDD Demo")
# default master: local[*] -- use all available CPUs

sc = SparkContext(conf=config)

rdd1 = sc.parallelize(range(1,100))
print("rdd1 partitions: ", rdd1.getNumPartitions())

rdd2 = sc.parallelize(range(1,100), numSlices=4)
print("rdd2 partitions: ", rdd2.getNumPartitions())

# rdd2.saveAsTextFile("file:///D:/may21/dbda/bigdata/temp/rdd2")
# print('rdd2 is saved into text file.')

rdd3 = rdd2.repartition(6)
# rdd2 (4 parts) --> rdd3 (6 parts)
# rdd3 = 15 + 15 + 15 + 15 + 20 + 20 (approx)

# rdd3.saveAsTextFile("file:///D:/may21/dbda/bigdata/temp/rdd3")
# print('rdd3 is saved into text file.')

rdd4 = rdd3.coalesce(3)
rdd4.saveAsTextFile("file:///D:/may21/dbda/bigdata/temp/rdd4")
print('rdd4 is saved into text file.')

sc.stop()

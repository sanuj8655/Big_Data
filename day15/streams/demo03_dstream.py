#!/usr/bin/python3

# streaming word count using spark dstreams

from pyspark import SparkConf, SparkContext
from pyspark.streaming import StreamingContext

conf = SparkConf()\
    .setMaster("local[2]")\
    .setAppName("stream_wordcount")

sc = SparkContext(conf=conf)

# stream processing with micro-batch duration = 10 seconds
ssc = StreamingContext(sc, 10)

# define stream source
# client socket getting data from server/listening socket on port 4444.
stream = ssc.socketTextStream("localhost", 4444)

# stream processing/transformation
result = stream.map(lambda line: line.lower())\
    .flatMap(lambda line: line.split())\
    .map(lambda word: (word, 1))\
    .reduceByKey(lambda a,b: a + b)
# print on console
result.pprint()

# stream action
ssc.start()

print("waiting for termination...")
ssc.awaitTermination()

# stop streaming context and spark context
ssc.stop()

### steps for execution
# step 1: run a server socket on port 4444
# terminal> netcat -k -l 4444
# step 2: start this application.
# step 3: start writing contents on listening/server socket.

# the application will start processing data
# and produce output continuously after every 10 seconds



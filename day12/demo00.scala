
# read the file and load as rdd
val rdd1 = sc.textFile("file:///D:/setup/spark-3.0.1-bin-hadoop3.2/LICENSE")
# rdd1: org.apache.spark.rdd.RDD[String] = file:///D:/setup/spark-3.0.1-bin-hadoop3.2/LICENSE MapPartitionsRDD[1]

# convert each line in lower case
val rdd2 = rdd1.map(line => line.toLowerCase())
# rdd2: org.apache.spark.rdd.RDD[String] = MapPartitionsRDD[2]

# split each line into multiple words
val rdd3 = rdd2.flatMap(line => line.split("[^a-z]"))
# rdd3: org.apache.spark.rdd.RDD[String] = MapPartitionsRDD[3]

# filter out stop words
val rdd4 = rdd3.filter(word => ! List("", "is", "are", "the", "in", "by", "of").contains(word))
# rdd4: org.apache.spark.rdd.RDD[String] = MapPartitionsRDD[4]

# add one into each word
val rdd5 = rdd4.map(word => (word, 1))
# rdd5: org.apache.spark.rdd.RDD[(String, Int)] = MapPartitionsRDD[5]

# group by word and sum all occurrences of count
val rdd6 = rdd5.reduceByKey((a,b) => a + b)
# rdd6: org.apache.spark.rdd.RDD[(String, Int)] = ShuffledRDD[6]

# swap tuple fields
val rdd7 = rdd6.map(tup => (tup._2, tup._1))
# rdd7: org.apache.spark.rdd.RDD[(Int, String)] = MapPartitionsRDD[7]

# get all words occurred more than 20 times
val rdd8 = rdd7.filter(tup => tup._1 > 20)
# rdd8: org.apache.spark.rdd.RDD[(Int, String)] = MapPartitionsRDD[8]

# collect 15 words from the output
val result = rdd8.take(15)
# result: Array[(Int, String)] = Array(...)

# print result
print(result.mkString("|"))
# (30,any)|(30,hive)|(190,org)|(25,api)|(32,jetty)|(21,j)|(112,apache)|(41,commons)|(28,spark)|(44,license)|(54,com)|(69,or)|(27,for)|(23,that)|(35,hadoop)

# Big Data

## Agenda
* Spark RDD & DAG
* Spark Architecture

## Word Count Program
* Input -- rdd1
	```
	Red green blue
	Red red green
	green Blue black
	green Green blue
	```
* Lower Case -- rdd2
	```
	red green blue
	red red green
	green blue black
	green green blue
	```
* Split -- flatMap() -- rdd3
	```
	red
	green
	blue
	red
	red
	green
	green
	blue
	black
	green
	green
	blue
	```
* Filter Stop Words -- rdd4
	```
	red
	green
	blue
	red
	red
	green
	green
	blue
	black
	green
	green
	blue
	```
* Add one -- rdd5 -- Key-Value pair RDD (RDD of Tuple2)
	```
	(red,1)
	(green,1)
	(blue,1)
	(red,1)
	(red,1)
	(green,1)
	(green,1)
	(blue,1)
	(black,1)
	(green,1)
	(green,1)
	(blue,1)
	```
* Group by word and Sum ones -- reduceByKey -- rdd6
	* Grouping by Key
		```
		(red, [1,1,1])
		(green, [1,1,1,1,1])
		(blue, [1,1,1])
		(black, [1])
		```
	* Reduce on each group --> (a,b) => a + b
		* "1, 1," 1, 1, 1
			* 1 + 1 = 2
		* 1, 1, "1," 1, 1
			* 2 + 1 = 3
		* 1, 1, 1, "1," 1
			* 3 + 1 = 4
		* 1, 1, 1, 1, "1"
			* 4 + 1 = 5
	* Final Result
		```
		(red, 3)
		(green, 5)
		(blue, 3)
		(black, 1)
		```
* Swap tuple fields -- rdd7
	```
	(3, red)
	(5, green)
	(3, blue)
	(1, black)
	```
* Filter words with count > 2 -- rdd8
	```
	(3, red)
	(5, green)
	(3, blue)
	```
* Get the words --> collect()
	```
	[
		(3, red),
		(5, green),
		(3, blue)
	]
	```

* Browser: http://localhost:4040

* spark-shell>

```
rdd2.partitions
# Array[org.apache.spark.Partition] = Array(org.apache.spark.rdd.HadoopPartition@3c1, org.apache.spark.rdd.HadoopPartition@3c2)

rdd2.partitions.length
# Int = 2

rdd7.partitions
# Array[org.apache.spark.Partition] = Array(org.apache.spark.rdd.ShuffledRDDPartition@0, org.apache.spark.rdd.ShuffledRDDPartition@1)
```

## Spark Terminologies
* RDD
	* Resilient Distributed Dataset.
	* Typically loaded and processed in memory (RAM).
	* Divided into multiple partitions.
* Spark application
	* Set of Spark Jobs (one or more jobs).
	* Each application have single SparkContext.
	* UI is available on port 4040.
	* e.g. spark-shell -- is a spark application.
	* Application is submitted using spark-submit.
		* e.g. spark-submit ... demo01.py
		* e.g. spark-submit ... app.jar
* Spark job
	* Terminated with action operation.
	* Divided into one or more stages.
	* Stages are separated by shuffle/wide operations.
* Stage
	* Set of narrow RDD operations.
	* Executed in single JVM process (in machine).
	* Divided into multiple tasks.
* Tasks
	* The tasks are created for each partition in the RDD.
	* Each task is a thread of execution (in JVM process).

## RDD
* RDD is Resilient.
	* If any RDD (or partitions in RDD) is failed (due to network, machine or operation failure), it can be recreated --> Resilient.
	* It is recreated from the source (textFile(), cached RDD, etc) by applying set of operations again.
	* Each RDD set of operations are maintained in its metadata -- RDD Lineage.
	* spark-shell> rdd5.toDebugString
		```
		res5: String =
		(2) MapPartitionsRDD[5] at map
			|  MapPartitionsRDD[4] at filter
			|  MapPartitionsRDD[3] at flatMap
			|  MapPartitionsRDD[2] at map
			|  file:///D:/setup/spark-3.0.1-bin-hadoop3.2/LICENSE MapPartitionsRDD[1] at textFile
			|  file:///D:/setup/spark-3.0.1-bin-hadoop3.2/LICENSE HadoopRDD[0] at textFile
		```
* RDD is Distributed.
	* RDD is logical entity -- no space occupied by RDD "directly".
	* RDD is divided into partitions. Partitions are stored in memory RAM of one or more nodes/machines.
	* Partition size depends on how RDD is created and source of RDD.
	* rdd = sc.textFile("hdfs://localhost:9000/user/sunbeam/ncdc/input")
		* When RDD is created from data in the HDFS, the number of Partitions = Number of Input splits = Number of HDFS blocks.
		* The partitioning is done by Hadoop TextInputFormat and RecordReader.
		* Usually one Partition = HDFS block size (128 MB).
	* val rdd = sc.parallelize(1 to 100) // scala
	* rdd = sc.parallelize(range(1, 100)) // python
		* sc.parallelize() convert scala/python collection into Spark RDD.
		* Number of Partitions depends on Cluster resources.
		* By default, "local" mode it depends on number of CPUs.
			* rdd.partitions.length
			* rdd.partitions
				* Array[org.apache.spark.Partition] = Array(org.apache.spark.rdd.ParallelCollectionPartition@802, ...()
	* val rdd = sc.parallelize(1 to 100, 2)
		* Programmer may decide numnber of partitions while reading from source (in few cases) and during shuffle operations.
		* rdd.partitions.length
* RDD is immutable.
	* RDD cannot be modified and operation will result into new RDD (similar to Java Strings).
* RDD Programming Guide
	* https://spark.apache.org/docs/latest/rdd-programming-guide.html


## Assign
1. Deptwise total sal (emp.csv and dept.csv).
2. Jobwise max sal (emp.csv)

file:///home/...

hdfs://localhost:9000/user/...

http://

s3://


# Big Data

## Agenda
* Revision
* Hadoop MR Logs
* Job Counters
* MR Job config
* Input splits & Number of mappers
* Combiner
* Partitioners & Number of reducers
* Custom Writables
* MR Job chaining
* MR Execution flow
* MR Execution on YARN

## Map Reduce

### NCDC Temperature Analysis (Avg Temp per year).

#### Mapper and Reducer
* AvgTempMapper
	* Input: `<offset, line>` --> `<LongWritable, Text>`
	* Output: `<year, temperature>` --> `<IntWritable, IntWritable>`
* AvgTempReducer
	* Input: `<year, temperatures>` --> `<IntWritable, IntWritables>`
	* Output: `<year, avg_temperature>` --> `<IntWritable, DoubleWritable>`

#### Execute MR program
* terminal> hadoop fs -mkdir -p /user/sunbeam/ncdc/partial_input
* terminal> hadoop fs -put 1901 /user/sunbeam/ncdc/partial_input
* terminal> hadoop fs -put 1902 /user/sunbeam/ncdc/partial_input
* terminal> hadoop fs -put 1903 /user/sunbeam/ncdc/partial_input
* terminal> hadoop jar ncdc-0.0.1-SNAPSHOT.jar com.sunbeam.ncdc.NcdcMain /user/sunbeam/ncdc/partial_input /user/sunbeam/ncdc/output2
* terminal> hadoop fs -cat /user/sunbeam/ncdc/output2/part-r-00000

#### Hadoop MR logs
* terminal> cd $HADOOP_HOME/logs
* terminal> ls
* terminal> cd userlogs
* terminal> cd application_1628912267551_0001
* The application executed by multiple YarnChild (Containers).
	* _000001 --> MRAppMaster
	* _000002, _000003, _000004 --> YarnChild for Mappers
	* _000005 --> YarnChild for Reducer
* terminal> cd container_1628912267551_0001_01_000002
* terminal> ls -l
* terminal> cd ../container_1628912267551_0001_01_000003
* terminal> ls -l
* terminal> cat stdout
* terminal> cd ../container_1628912267551_0001_01_000004
* terminal> ls -l
* terminal> cat stdout

#### Job History Server
* terminal> mapred --daemon start historyserver
* Browser: http://localhost:19888
* Job History Server reads all logs from HDFS and NodeManagers.

#### Job Counters
* Hadoop MR execution keep track of many predefined job counters.
	```
	     File System Counters
	         FILE: Number of bytes read=0
	         FILE: Number of bytes written=288445
	         FILE: Number of read operations=0
	         FILE: Number of large read operations=0
	         FILE: Number of write operations=0
	         HDFS: Number of bytes read=889099
	         HDFS: Number of bytes written=0
	         HDFS: Number of read operations=3
	         HDFS: Number of large read operations=0
	         HDFS: Number of write operations=0
	         HDFS: Number of bytes read erasure-coded=0
	     Map-Reduce Framework
	         Map input records=6565
	         Map output records=6565
	         Map output bytes=52520
	         Map output materialized bytes=65656
	         Input split bytes=121
	         Combine input records=0
	         Spilled Records=6565
	         Failed Shuffles=0
	         Merged Map outputs=0
	         GC time elapsed (ms)=938
	         CPU time spent (ms)=6400
	         Physical memory (bytes) snapshot=305827840
	         Virtual memory (bytes) snapshot=2532712448
	         Total committed heap usage (bytes)=315097088
	         Peak Map Physical memory (bytes)=305827840
	         Peak Map Virtual memory (bytes)=2532712448
	```
* MR programmer may also implement custom job counters to get application specific stats/summary.
* step 1: Implement an "enum" for job counter with desired fields (counters).
* step 2: To increment the counter (in mapper or reducer)
	* Counter counter = context.getCounter(enum.field);
	* counter.increment(1);
* terminal> hadoop jar ncdc-0.0.1-SNAPSHOT.jar com.sunbeam.ncdc.NcdcMain /user/sunbeam/ncdc/partial_input /user/sunbeam/ncdc/output3
* Observe the Job Counters output on console.
	```
	com.sunbeam.ncdc.NcdcJobCounter
         INVALID_RECORDS=44
         VALID_RECORDS=19640
	```
* Observe the Job Counters in Job HistoryServer.

#### MR Job config
* MR Job config can be hard-coded in MR job application using conf.set("config", "value");
	* e.g. conf.set("dfs.replication", "2");
* However this poor practice. This restricts user to run on specific cluster with specific settings.
* MR Job config can be given using generic options -fs, -jt, -D, etc. However specifying multiple config with -D is tedious and non maintainable.
* MR Job config is commonly given using generic option -conf in some xml file. Different config files can be created for different clusters or different requirements.
* terminal> hadoop jar ncdc-0.0.1-SNAPSHOT.jar com.sunbeam.ncdc.NcdcMain -conf mr-config/local-config.xml /home/sunbeam/may21/bigdata/data/ncdc/ /tmp/output1
	* In local mode, only local filesystem can be accessed.
* terminal> hadoop jar ncdc-0.0.1-SNAPSHOT.jar com.sunbeam.ncdc.NcdcMain -conf mr-config/pseudo-config.xml /user/sunbeam/ncdc/partial_input /user/sunbeam/ncdc/output4
	* In pseudo-config mode, only HDFS is accessed.
* Hadoop MR job config (predefined config values or custom-application specific config values) can be accessed in mapper and reducer class using context.getConfiguration().

#### Input splits & Number of mappers
* To process huge data faster it is scattered over multiple nodes (in HDFS) in the cluster and advised to process (on YARN) there (not on single system) -- Distributed Computing.
* In HDFS by default data is divided into data blocks of size 128 MB. Each block of data can be processed individually in Mapper task. Mostly number of mapper tasks = number of HDFS blocks.
* Input splits --> Input to a mapper process.
	* Decided by the InputFormat and RecordReader.
	* e.g. In TextInputFormat, one record is one line.
	* InputSplit approx equal to HDFS block.
* Number of mapper tasks = number of input splits.



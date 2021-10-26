# Big Data

## Agenda
* Revision
* Input and Output Formats
* Execution with multiple mappers
* Combiner
* Partitioners & Number of reducers
* Execution with multiple reducers
* MR Job chaining
* Custom Writables
* MR Execution flow
* Hadoop performance
* MR Execution on YARN
* Hadoop streaming

## Map Reduce

### NCDC Temperature Analysis

#### Mapper and Reducer
* AvgTempMapper
	* Input: `<offset, line>` --> `<LongWritable, Text>`
	* Output: `<year, temperature>` --> `<IntWritable, IntWritable>`
* AvgTempReducer
	* Input: `<year, temperatures>` --> `<IntWritable, IntWritables>`
	* Output: `<year, avg_temperature>` --> `<IntWritable, DoubleWritable>`


#### Input splits & Number of mappers
* To process huge data faster it is scattered over multiple nodes (in HDFS) in the cluster and advised to process (on YARN) there (not on single system) -- Distributed Computing.
* In HDFS by default data is divided into data blocks of size 128 MB. Each block of data can be processed individually in Mapper task. Mostly number of mapper tasks = number of HDFS blocks.
* Input splits --> Input to a mapper process.
	* Decided by the InputFormat and RecordReader.
	* e.g. In TextInputFormat, one record is one line.
	* InputSplit approx equal to HDFS block.
* Number of mapper tasks = number of input splits.

### Number of Mappers
* Number of Mappers = Number of Input splits = Number of HDFS blocks
* To reduce number of input splits for smaller files
	* Upload files as compressed (archive) file.
	* Use CombinedInputFormat

### Compression
* Hadoop supports few compression algorithms.
	* .gz -- GNU Zip -- org.apache.hadoop.io.compress.GzipCodec
	* .bz2 -- Binary Zip File -- org.apache.hadoop.io.compress.BZip2Codec
	* .lzo -- LZO -- com.hadoop.compression.lzo.LzopCodec
	* .lz4 -- LZ4 -- org.apache.hadoop.io.compress.Lz4Codec
	* .snappy -- Snappy -- org.apache.hadoop.io.compress.SnappyCodec
* If input files in any of the above compressed form, they will be processed by Hadoop directly (no coding/config needed).
* To produce compressed output (mapper or final reducer output), then the compression format must be set in MR job config.
	```Java
	FileOutputFormat.setCompressOutput(job, true);
	FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
	```
* hadoop jar ncdc-0.0.1-SNAPSHOT.jar com.sunbeam.ncdc.NcdcMain -conf mr-config/pseudo-config.xml /user/sunbeam/ncdc/partial_input /user/sunbeam/ncdc/output5

### Input and Output Formats
* Multiple small files can be treated as single input split using CombinedInputFormat.
	```Java
	job.setInputFormatClass(CombineTextInputFormat.class);
	```

### Combiner
* Find max for each month
* hadoop jar ncdc_maxtemp-0.0.1-SNAPSHOT.jar com.sunbeam.ncdc.NcdcMain -conf mr-config/pseudo-config.xml /user/sunbeam/ncdc/partial_input /user/sunbeam/ncdc/output7

### Reducer
* Find avg temp for each month
	* hadoop jar ncdc_avgtemp-0.0.1-SNAPSHOT.jar com.sunbeam.ncdc.NcdcMain -conf mr-config/pseudo-config.xml /user/sunbeam/ncdc/partial_input /user/sunbeam/ncdc/output9
* When using multiple reducers
	* 1. The data corresponding to a key, must go to the same reducer task. One key data should be not be distributed to multiple reducers.
	* 2. The input data should be evenly distributed across multiple reducers.
* To divide data across the reducers, Hadoop by default use HashPartitioner.
	* It divides the mapper output into partitions so that each partition can be delivered to respective reducers.
	* The number of partitions = The number of reducers.
	* Equal division across multiple reducers is ensured by the hash function.
		* partition = key.hashCode() % numberOfPartitions;

### MR Execution Flow
* MR progam data flow is in following order.
	* Map Task (Java process)
		* InputFormat --> <offset,line>
		* Mapper --> <mapkey,mapvalue>
		* Partitioner --> <partition|mapkey,mapvalue>
		* Combiner --> <partition|mapkey,mapaggregateresult>
	* Reduce Task (Java process)
		* Reducer Input --> a partition from all mappers --> <mapkey,mapaggregateresult>
		* Reducer --> a partition from all mappers --> <key,aggregateresult>
		* OutputFormat --> HDFS

### Hadoop performance

#### Mapper output --> Ring Buffer
* Partition number is attached to each map output key-value pair and then the output is stored in a ring buffer (circular queue).
* Ring buffer size = 100 MB (default)
	* HDFS 1.x -- Block size = 64 MB
	* HDFS 2.x -- Block size = 128 MB
* If mapper output exceed ring buffer size threshold, the output is written on hard-disk (on NodeManager on which Mapper task is running). This is called as "spill" (Map-side).
* If data spills on disk, reading from disk will take more time (disk is slower).
* This spill can be avoided by increasing size of ring buffer in job config.
	* io.sort.mb = 200 mb (change as per requirements).
* Example:
	* Map output records = 19640
	* Map output bytes = 157120
	* Avg size of record = 157120 / 19640 = 9 bytes
	* The partition index (4 bytes) is added for each Map key-value pair.
	* Partitioner output size of each record = 9 + 4 = 13 bytes.
	* If dfs.blocksize = 1024 MB, approx output of mapper+partitioner = 90MB+.
	* Ring buffer default size = 100 MB with spill percentage (threshold) = 80%, will cause the spill.
	* To avoid spill increase size of Ring buffer = 90.257 MB / 0.8 = 112.82 MB = 120 MB (approx).
* The mapper task (JVM process) is by default given memory of 1024 MB.
	* Considering ring buffer size = 100 MB.
	* If buffer size is increased to 120 MB, map task size should be increased in the proportion.

#### Refer hadoop-performance.pdf

### Recommendation Systems
* https://towardsdatascience.com/introduction-to-recommender-systems-6c66cf15ada

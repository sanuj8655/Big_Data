# Big Data

## Agenda
* MR Execution on YARN
* MR Job chaining
* Custom Writables
* Hadoop streaming
* Hive introduction

## MR Execution on YARN
* MRAppMaster
* YarnChild -- MapTask / ReduceTask

## Execute Job in uber mode
* terminal> hadoop jar ncdc_maxtemp-0.0.1-SNAPSHOT.jar com.sunbeam.ncdc.NcdcMain -conf mr-config/uber-config.xml /user/sunbeam/ncdc/partial_input /user/sunbeam/ncdc/output10
* ls $HADOOP_HOME/logs/userlogs/application_xyz_000p/
* Single container is created for MRAppMaster and execute multiple mappers and reducer in it.

## MR Job chaining
* Friends Recommendation
	```
	A	B,C,D,G
	B	A,E
	C	A,E
	D	A,E
	E	B,C,D
	```
* hadoop fs -mkdir -p /user/sunbeam/friends/input
* hadoop fs -put /home/sunbeam/may21/bigdata/data/friends.txt /user/sunbeam/friends/input
* hadoop jar mr_friends-0.0.1-SNAPSHOT.jar com.sunbeam.edbda.MRDriver -conf mr-config/pseudo-config.xml /user/sunbeam/friends/input /user/sunbeam/friends/aux1 /user/sunbeam/friends/output1

## Custom Writables
* User defined class inherited from Hadoop's Writable class --> Value.
* User defined class inherited from Hadoop's WritableComparable class --> Key or Value.

## Map Reduce

### Map only job
* Input (HDFS) --> Mapper --> Output (HDFS)
* In main class
	* Configure job with Mapper info only.
		* job.setMapperClass(...);
		* job.setMapOutputKeyClass(...);
		* job.setMapOutputValueClass(...);
	* Configure job for zero reducers.
		* job1.setNumReduceTasks(0);
* No reducer --> No aggregation operation.
* Applications of Map-only jobs
	* Data ingestion (loading data into Hadoop from RDBMS, ...)
	* Data export (loading data into RDBMS from Hadoop, ...)
	* Data cleansing/transformation
	* Data filtering

### Reduce only job
* No. Reduce only job is not possible.
* However a job can have a mapper which is not doing any processing --> IdentityMapper.
* Input (HDFS) --> Identity Mapper --> Sort-Shuffle-Merge --> Reducer --> Output (HDFS)

### Shuffle stage
* The output of mapper is sorted (in map task) and the shuffled to multiple reducers (as per partitioner) and then the partitions are merged at reducer side.

## Hadoop streaming
* Hadoop allows implementing MR job in any programming language which facilitate input/output and redirection.
* Thus MR program can be written in Python, Ruby, C/C++, etc.
* Linux -- IO redirection:
	* ls -l > out.txt
	* wc < in.txt
	* ls -l | wc
	* Internally the output (stdout -- System.out) and input (stdin -- System.in) is redirected to the files (out.txt, in.txt) instead of terminal.
* Java -- IO redirection:
	```Java
	Runtime rt = Runtime.getRuntime();
	Process p = rt.exec("wc");
	OutputStream out = p.getOututStream();
	// ... write OutputStream ...
	InputStream in = p.getInputStream();
	// ... read InputStream ...
	int exitCode = p.waitFor();
	```
* Hadoop Streaming job launch mapper task and reducer task (implemented in other programming languages) and supply input via stdin and collect result via stdout.
* hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-3.2.0.jar \
-files mapper.py,reducer.py \
-input /user/sunbeam/wordcount/input \
-output /user/sunbeam/wordcount/output5 \
-mapper mapper.py \
-reducer reducer.py

## Sorting
```Java
class Emp {
	private int empno;
	private String ename;
	private double sal;
	private int deptno;
	private String job;
	// ctors
	// getters/setters
}
class Main {
	public static void main(String[] args) {
		List<Emp> list = new ArrayList<>();
		// load emp data from emp.csv

		// ORDER BY deptno ASC, job ASC, sal DESC.
		list.sort((e1, e2) -> {
			int diff = e1.getDeptno() - e2.getDeptno();
			if(diff == 0)
				diff = e1.getJob().compareTo(e2.getJob());
			if(diff == 0)
				diff = (int)Math.signnum(e2.getSal() - e1.getSal());
			return diff;
		});
		list.forEach(System.out::println);
	}
}
```

## Inter-process communication
* OS have several mechanisms for IPC.
	* Signals, Shared memory, Message Queue, Pipe, Sockets
* Socket -- Communication end-point -- Developed by BSD UNIX.
	* UNIX socket
	* INET socket
* INET socket
	* Used for Communication between processes running on different machines.
	* INET socket = IP address + Port number
		* IP address = Identify computer in the network (LAN, MAN, WAN)
			* IPv4 and IPv6.
		* Port number = Identify socket in that computer.
			* 0 to 65535

## Hive introduction



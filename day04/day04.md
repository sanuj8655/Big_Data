# Big Data

## Agenda
* Use multi-node cluster
* Replication
* HDFS write & read operation
* Safe mode
* Commissioning and Decommissioning
* Secondary NameNode
* Standby NameNode
* NameNode metadata (fsimage)
* HDFS 1.x vs HDFS 2.x

## Using multi-node cluster

```sh
master> start-dfs.sh

all> jps

master> hadoop fs -mkdir -p /user/sunbeam 

master> echo "welcome to hadoop 3.x cluster..." > welcome.txt

master> hadoop fs -put welcome.txt /user/sunbeam
# welcome.txt --> slave1 and slave2

master> echo "file one" > one.txt
master> echo "file two" > two.txt
master> echo "file three" > three.txt

master> hadoop fs -put one.txt /user/sunbeam
# one.txt --> slave1 and slave3
master> hadoop fs -put two.txt /user/sunbeam
# two.txt --> slave2 and slave3
master> hadoop fs -put three.txt /user/sunbeam
# three.txt --> slave1 and slave2

slave3> hadoop-daemon.sh stop datanode
slave3> jps

master> echo "slave3 datanode is off" > four.txt

master> hadoop fs -put four.txt /user/sunbeam
# four.txt --> slave1 and slave2

master> hadoop fs -cat /user/sunbeam/one.txt
# exception from slave3. then read from slave1.

master> hadoop fs -cat /user/sunbeam/two.txt
# read from slave2

slave2> hadoop-daemon.sh stop datanode
slave2> jps

master> hadoop fs -cat /user/sunbeam/two.txt

master> echo "slave2 and slave3 datanode is off" > five.txt

master> hadoop fs -put five.txt /user/sunbeam
# five.txt --> slave1 (under-replica)
# exception

slave2> hadoop-daemon.sh start datanode
slave2> jps

# one.txt --> slave1 & slave3 --> slave1 & slave2
# two.txt --> slave2 & slave3 --> slave1 & slave2

# five.txt --> slave1 & slave2 (auto create replica on slave2 to replication=2)

slave3> hadoop-daemon.sh start datanode
slave3> jps

# one.txt --> slave1 & slave3 --> slave1 & slave2 --> slave1 & slave2 & slave3 (over-replica)
# one.txt --> slave1 & slave3 (auto delete slave2 replica to replication=2)

master> hdfs fsck /

master> hadoop fs -setrep 3 /user/sunbeam/welcome.txt
# welcome.txt --> slave1 & slave2 & slave3
```

### Heartbeat
* DataNodes keep sending small packets of data to NameNode continuously (by default after every 3 seconds).
* If Heartbeat signal is not received for a certain duration, NameNode declare DataNode as dead.

## Safe mode
* The HDFS metadata is stored on NameNode (disk). The metadata is served from the NameNode RAM (for sake of better speed).
* When HDFS is started, the metadata is loaded from NameNode disk to NameNode RAM. It also checks the metadata. This is referred as "safe mode".
* During this process no operations can be performed on HDFS.
* In typical Hadoop cluster (TBs of data), this process may take upto 25-30 mins.
* Even though not recommended, safe mode can be turned off manually.
	* hadoop dfsadmin –safemode leave
* http://master:9870/ --> Startup

## Commissioning and Decommissioning
* Hadoop cluster have multiple data nodes (e.g. 4 to 1000).
* Adding nodes into hadoop cluster on the fly (i.e. without stopping the cluster), is called as "Commissioning".
* Removing nodes from hadoop cluster on the fly (i.e. without stopping the cluster), is called as "Decommissioning".
* During the process, Hadoop automatically does load balancing of data blocks across the available nodes.
* Commissioning and Decommissioning process is done by Hadoop administrator (as per requirement). The process can be simplified by Hadoop cluster management -- Apache Ambari.
* http://master:9870/ --> Overview

## Secondary NameNode

### HDFS 1.x Secondary NameNode
* In Hadoop 1.x, NameNode is SPOF (Sinle Point of Failure).
* Secondary NameNode periodically takes backup of NameNode (primary).
* If NameNode fails some time later the last backup, all new changes in the metadata (after the backup) will be lost. Hence NameNode is SPOF.
* If NameNode fails, then Secondary NameNode is made Primary NameNode manually. During this process "HDFS is down" (Non-HA i.e. No High Availability) and thus no operations can be performed.

### HDFS 2.x Secondary NameNode
* Secondary NameNode fetch each metadata change from primary NameNode along with las fsimage.
* It apply/merge all new changes (edit logs) into that fsimage and build new fsimage checkpoint.
* This checkpoint transferred on NameNode and renamed as fsimage.
* This process ensures that Secondary NameNode have all metadata changes as of primary NameNode.
* If NameNode fails, then Secondary NameNode is made Primary NameNode manually. During this process "HDFS is down" (Non-HA i.e. No High Availability) and thus no operations can be performed.

### HDFS 2.x Standby NameNode
* Standby NameNode is working similar to Secondary NameNode.
* However if NameNode fails, Standby NameNode automatically become Primary NameNode. This is also referred as HDFS HA (High Availability) config.
* Hadoop administrator will inspect reason on NameNode failure and repair it. The NameNode can be again added into the cluster as "Standby NameNode".
* Journaling nodes can be added into the system which does checkpointing. Now Standby NameNode can quickly become Primary NameNode in case of failure.

### HDFS 3.x Standby NameNodes
* Similar to Hadoop 2.x Standby NameNode.
* Hadoop 3.x allows configuring more than one Standby NameNodes (Passive).
* If the active NameNode goes down, one of the Standby NameNode will be considered as Primary (Active).

## HDFS limitations
* HDFS is "Write Once Read Multiple" times.
	* HDFS enable creating new files and appending the existing files.
	* HDFS is NOT designed to edit the existing files.
* HDFS latency is higher.
	* File access (read/write) takes more time.
	* HDFS storing all data blocks into disk (not processing into RAM).

### HBase
* HBase is NoSQL database on the top of HDFS.
* HBase enables low latency CRUD operations and high-speed searching.
	* HBase process data into RAM (use caching techiques).
	* HBase use small delta files to save the changes (UPDATE/DELETE operations).

### Hive
* Hive is RDBMS on Hadoop (HDFS + MR).
* Hive enables CRUD operations and SQL query executions.

## Design Pattern
* Design patterns are the solutions to the common problems in software development.

### Singleton design pattern
* C programming: Data to be accessed in whole project --> Global variables.
* OOP (Java, C#, ...) programming: Data to be accessed in whole project --> ??.
	* Global variables --> Not allowed -- Violates OOP principles.
	* Singleton design pattern --> Single object -- Made available throughout the project.

### Fork Join design pattern
* Design pattern for parallel computing / multi-processing (i.e. using multiple CPUs - multi-core - on the same computer).
* By default any application use single CPU.
* To use multiple CPUs, programmer should create multiple threads. OS assign different threads to different CPUs.
* Creating multiple threads and distributing work across them, is done using Fork Join Design pattern.
* In Java it is implemented using ForkJoinPool class.

### Map Reduce design pattern
* Design pattern for distributed computing.
* The task is distributed across multiple computers in a network (i.e. cluster).
* Pre-requisites:
	* map() and reduce() --> Java 8 Functional programming, Python programming.


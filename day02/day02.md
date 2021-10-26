# Big Data

## Hadoop

### Using Hadoop Single Node Cluster

```sh
jps

start-dfs.sh

jps
```

* Browser: http://localhost:9870 --> HDFS UI

```sh
df -h
# displays sizes of all partitions.
# hadoop storage capacity = size of partition on which hadoop is installed.
```

```sh
hadoop fs -help

hadoop fs -mkdir /user

hadoop fs -mkdir /user/sunbeam

hadoop fs -ls /

hadoop fs -ls /user

hadoop fs -ls /user/sunbeam

# create a new file into client (not in hdfs)
cat > file1.txt
...
...
...
# Ctrl + D

cat file1.txt

# hadoop fs -put local-filepath hdfs-dirpath
hadoop fs -put file1.txt /user/sunbeam

hadoop fs -ls /user/sunbeam

hadoop fs -cat /user/sunbeam/file1.txt

hadoop fs -tail /user/sunbeam/file1.txt

hadoop fs -head /user/sunbeam/file1.txt
```

* Browser: http://localhost:9870 --> HDFS UI
	* Utilities --> Browse File System

```sh
stop-dfs.sh
```

```sh
start-dfs.sh

start-yarn.sh

jps

stop-yarn.sh

stop-dfs.sh
```


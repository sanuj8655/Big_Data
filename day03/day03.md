# Big Data

## Using Hadoop Single Node Cluster

```sh
start-dfs.sh

jps

echo "hello everyone" > f1.txt
echo "hello sunbeam" > f2.txt
echo "hello dbda" > f3.txt

cat f1.txt
cat f2.txt
cat f3.txt
ls f*.txt

hadoop fs -put f*.txt /user/sunbeam
# -put --> hadoop 2.x ----- -copyFromLocal --> hadoop 1.x

hadoop fs -get /user/sunbeam/file1.txt
# -get --> hadoop 2.x ----- -copyToLocal --> hadoop 1.x

cat file1.txt

hadoop fs -rm /user/sunbeam/file1.txt
# to remove the dir from hdfs use "-rm -r"

hadoop fs -ls /user/sunbeam

hadoop fs -getmerge /user/sunbeam/f*.txt data.txt
cat data.txt

hadoop fs -stat /user/sunbeam/f1.txt

hadoop fs -df -h /
# partition usage

hadoop fs -du -h /user/sunbeam
# disk usage -- per file

hadoop fs -ls /user/sunbeam
# type permissions 	replication		user	group	size	timestamp	file-hdfs-path

hadoop fs -help setrep
hadoop fs -setrep 3 /user/sunbeam/f1.txt
hadoop fs -ls /user/sunbeam
```

## Troubleshooting
* HDFS is not starting properly.
	* stop HDFS. -- stop-dfs.sh
	* delete /home/sunbeam/bigdata folder (dn and nn).
	* hdfs namenode -format
	* start-dfs.sh
	* jps
* Preventive measure
	* Do not put system in sleep or hibernate while HDFS is running.
	* Stop Hadoop and then sleep/hibernate/shutdown system.
	* Do not modify any files in /home/sunbeam/bigdata (dn and nn).

## HDFS Java API

### HDFS dir listing demo
* step 1: File --> Maven project
* step 2: Check --> Create simple project (skip archtype selection).
* step 3: Maven project settings
	* Group Id: com.sunbeam
	* Artifact Id & Project Name: hdfsdirlist
	* Packaging: Jar
	* Click Finish
* step 4: Add java version into pom.xml.
	```xml
	<properties>
		<maven.compiler.source>1.8</maven.compiler.source>
		<maven.compiler.target>1.8</maven.compiler.target>
	</properties>
	```
* step 5: Update Maven project
	* Project --> (right click) --> Maven --> Update project --> Click OK.
* step 6: Add hadoop-client dependency into Maven project.
	```xml
	<dependencies>
		<dependency>
			<groupId>org.apache.hadoop</groupId>
			<artifactId>hadoop-client</artifactId>
			<version>3.2.0</version>
		</dependency>  
	</dependencies>
	```
	* This will add all necessary jars in classpath of current project.
* step 7: Create main class in package com.sunbeam and implement Dir listing code there.
* step 8: Run as Java application.

## Maven
* http://tutorials.jenkov.com/maven/maven-tutorial.html

## HDFS Read/Write
* Assignment
	* Upload emp.csv file into HDFS using command "hadoop fs -put".
	* Write a Java program to read file from HDFS and print jobwise total salary of the employee.

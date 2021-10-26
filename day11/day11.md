# Big Data

## Agenda
* Hive
	* Indexes (Hive 2.x)
	* Managed tables vs External tables
	* Partitioning
	* Bucketing
* Spark
	* Introduction
	* https://spark.apache.org/downloads.html

## Hive

### Hive 2.x Indexes

```SQL
CREATE INDEX idx_subject_compact ON TABLE books(subject) AS 'COMPACT' WITH DEFERRED REBUILD;

SHOW TABLES;

DESCRIBE dbda__books_idx_subject_compact__;

SELECT * FROM dbda__books_idx_subject_compact__;

ALTER INDEX idx_subject_compact ON books REBUILD;

SELECT * FROM dbda__books_idx_subject_compact__;

SELECT * FROM books WHERE subject='C++ Programming';

CREATE INDEX idx_subject_bitmap ON TABLE books(subject) AS 'BITMAP' WITH DEFERRED REBUILD;

SHOW TABLES;

DESCRIBE dbda__books_idx_subject_bitmap__;

ALTER INDEX idx_subject_bitmap ON books REBUILD;

SELECT * FROM dbda__books_idx_subject_bitmap__;

SELECT * FROM books WHERE subject='C++ Programming';
```

### Managed Tables
* Hive manages metadata (in metastore) as well as data (in HDFS).
* The table data --> Hive "warehouse dir" in HDFS.
* When table is created, data is uploaded separately using LOAD DATA statement.
* When table is dropped (DROP TABLE), metadata is deleted from metastore and data is deleted from HDFS warehouse dir.

### External Tables
* Hive manages metadata (in metastore) and data is already available in HDFS (not necessaily in warehouse dir).
* When table is created, location of data in HDFS is mentioned.
* When table is dropped (DROP TABLE), metadata is deleted from metastore. The data in HDFS is not deleted.
* The same data in HDFS may have different structure/schema in Hive.

```sh
terminal> hadoop fs -mkdir -p /user/sunbeam/emp/input

terminal> hadoop fs -put /home/sunbeam/may21/bigdata/data/emp.csv /user/sunbeam/emp/input
```

```SQL
CREATE EXTERNAL TABLE emp_staging(empno INT, ename STRING, job STRING, mgr INT, hire STRING, sal DOUBLE, comm DOUBLE, deptno INT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/user/sunbeam/emp/input';

SHOW TABLES;

SELECT * FROM emp_staging LIMIT 15;

CREATE EXTERNAL TABLE emp_rawdata(empno INT, ename STRING, job STRING, mgr STRING, hire STRING, sal STRING)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/user/sunbeam/emp/input';

SELECT * FROM emp_rawdata LIMIT 15;

DROP TABLE emp_rawdata;

SELECT * FROM emp_staging LIMIT 15;

DROP TABLE emp_staging;
```

* Lab Assignment
	* Upload contacts.csv into HDFS dir /user/sunbeam/contacts/input.
	* Hive external table 1 -- considering all fields of STRING type.
	* Hive external table 2 -- considering fields of appropriate collection types.
	* print contacts in 'pune' district.
		* table 1: WHERE addr LIKE '%pune%';
		* table 2: WHERE addr.dist = 'pune';

### Partitioning

```SQL
CREATE TABLE emp_dept_part(
empno INT,
ename STRING,
job STRING,
mgr INT,
hire STRING,
sal DOUBLE,
comm DOUBLE
)
PARTITIONED BY (deptno INT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

DESCRIBE emp_dept_part;
DESCRIBE FORMATTED emp_dept_part;
```

#### Static partitioning
* Upload/process data manually in given partitions.

```SQL
LOAD DATA LOCAL INPATH '/home/sunbeam/may21/bigdata/data/emp10.csv'
INTO TABLE emp_dept_part
PARTITION (deptno=10);

LOAD DATA LOCAL INPATH '/home/sunbeam/may21/bigdata/data/emp20.csv'
INTO TABLE emp_dept_part
PARTITION (deptno=20);

LOAD DATA LOCAL INPATH '/home/sunbeam/may21/bigdata/data/emp30.csv'
INTO TABLE emp_dept_part
PARTITION (deptno=30);

SELECT * FROM emp_dept_part;
-- poor practice

SELECT * FROM emp_dept_part WHERE job = 'CLERK';
-- poor practice

SELECT * FROM emp_dept_part WHERE deptno = 20 AND job = 'CLERK';
-- good practice to mention partition in which data to be processed.

TRUNCATE TABLE emp_dept_part;

DROP TABLE emp_dept_part;
```

#### Dynamic partitioning

```SQL
CREATE TABLE emp_dept_part(
empno INT,
ename STRING,
job STRING,
mgr INT,
hire STRING,
sal DOUBLE,
comm DOUBLE
)
PARTITIONED BY (deptno INT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

CREATE EXTERNAL TABLE emp_staging(empno INT, ename STRING, job STRING, mgr INT, hire STRING, sal DOUBLE, comm DOUBLE, deptno INT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

LOAD DATA LOCAL INPATH '/home/sunbeam/may21/bigdata/data/emp.csv'
INTO TABLE emp_staging;

INSERT INTO emp_dept_part PARTITION(deptno)
SELECT empno,ename,job,mgr,hire,sal,comm,deptno FROM emp_staging;
```

```SQL
CREATE TABLE emp_deptjob_part(
empno INT,
ename STRING,
mgr INT,
hire STRING,
sal DOUBLE,
comm DOUBLE
)
PARTITIONED BY(deptno INT, job STRING)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

SET hive.exec.dynamic.partition.mode = strict;

-- default dynamic partition mode = strict (if not changed in hive-site.xml).
-- giving at least first level partition is compulsory.

INSERT INTO emp_deptjob_part
PARTITION(deptno=20,job)
SELECT empno,ename,mgr,hire,sal,comm,job FROM emp_staging WHERE deptno=20;

DROP TABLE emp_deptjob_part;
```

```SQL
CREATE TABLE emp_deptjob_part(
empno INT,
ename STRING,
mgr INT,
hire STRING,
sal DOUBLE,
comm DOUBLE
)
PARTITIONED BY(deptno INT, job STRING)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

SET hive.exec.dynamic.partition.mode = nonstrict;

INSERT INTO emp_deptjob_part
PARTITION(deptno,job)
SELECT empno,ename,mgr,hire,sal,comm,deptno,job FROM emp_staging;
```

### Bucketing
* Buckets --> `java.util.HashMap<K,V>`
	* The values are stored across multiple buckets based on hash code of key.
* In Hive, the rows will be distributed across multiple rows based on hashcode of given column. Bucketing is also referred as "clustering".
* Bucketing uniformly distributes the rows into multiple files.

```SQL
CREATE TABLE emp_bucketed(empno INT, ename STRING, job STRING, mgr INT, hire STRING, sal DOUBLE, comm DOUBLE, deptno INT)
CLUSTERED BY (empno) INTO 2 BUCKETS
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

DESCRIBE emp_bucketed;
DESCRIBE FORMATTED emp_bucketed;

INSERT INTO emp_bucketed
SELECT * FROM emp_staging;

SELECT * FROM emp_bucketed LIMIT 15;
```

```SQL
CREATE TABLE emp_dept_part_bucketed(
empno INT,
ename STRING,
job STRING,
mgr INT,
hire STRING,
sal DOUBLE,
comm DOUBLE
)
PARTITIONED BY(deptno INT)
CLUSTERED BY (empno) INTO 2 BUCKETS
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

INSERT INTO emp_dept_part_bucketed
PARTITION(deptno)
SELECT * FROM emp_staging;

SELECT job, SUM(sal) FROM emp_dept_part_bucketed
WHERE deptno IN (10, 20)
GROUP BY job;
```

### Hive QL --> MR Job
* Number of cascaded jobs --> Complexity of Hive query (number of aggregations, joins, order, ...)
* Number of mappers (in each job) --> Number of HDFS blocks (input splits)
* Number of reducers (in each job)
	* Number of buckets
	* Number reducers configured using SET ...

## Apache Spark

### Word Count program using Scala
* Download and extract Apache Spark: https://spark.apache.org/downloads.html
* Execute commands on terminal.

```sh
export SPARK_HOME=/path/of/spark

export PATH=$SPARK_HOME/bin:$SPARK_HOME/sbin:$PATH

spark-shell
```

* Execute following commands on spark-shell (scala):

```scala
# read the file and load as rdd
val rdd1 = sc.textFile("file:///D:/setup/spark-3.0.1-bin-hadoop3.2/LICENSE")
# convert each line in lower case
val rdd2 = rdd1.map(line => line.toLowerCase())
# split each line into multiple words
val rdd3 = rdd2.flatMap(line => line.split("[^a-z]"))
# filter out stop words
val rdd4 = rdd3.filter(word => ! List("", "is", "are", "the", "in", "by", "of").contains(word))
# add one into each word
val rdd5 = rdd4.map(word => (word, 1))
# group by word and sum all occurrences of count
val rdd6 = rdd5.reduceByKey((a,b) => a + b)
# collect 20 words from the output
val result = rdd6.take(20)
# print result
print(result.mkString("|"))
```

* Execute following commands on pyspark (python):

```sh
pyspark
```

```python
# read the file and load as rdd
rdd1 = sc.textFile("file:///D:/setup/spark-3.0.1-bin-hadoop3.2/LICENSE")
# convert each line in lower case
rdd2 = rdd1.map(lambda line: line.lower())
# split each line into multiple words
rdd3 = rdd2.flatMap(lambda line: line.split())
# filter out stop words
rdd4 = rdd3.filter(lambda word: word not in ["", "is", "are", "the", "in", "by", "of"])
# add one into each word
rdd5 = rdd4.map(lambda word: (word, 1))
# group by word and sum all occurrences of count
rdd6 = rdd5.reduceByKey(lambda a,b: a + b)
# collect 20 words from the output
result = rdd6.take(20)
# print result
print(result)
```






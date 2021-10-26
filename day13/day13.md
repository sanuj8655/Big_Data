# Big Data

## Agenda
* Spark cluster installation
* Spark architecture
* Broadcast and Accumulators
* RDD persistence
* Data frames

## RDD Assignments
* ncdc dataset hottest year
* Friends recommendation *
* Movie recommendation *
* state_wise_daily.csv
	* Monthly total confirmed or deceased patients for each state.
	* State with highest confirmed cased per month.

## Spark cluster installation
* Spark cluster can be built with various cluster managers.
	* Standalone (built-in) ***
	* YARN
	* Mesos
	* Kubernetes
* Installation modes
	* Local mode
	* Single node cluster
	* Multi-node cluster

### Single Node cluster
* set SPARK_HOME and PATH in ~/.bashrc
* set SPARK_MASTER_HOST and SPARK_LOCAL_IP in spark-env.sh
* set spark.master in SPARK_HOME/conf/spark-defaults.conf
* terminal> spark-master.sh
* terminal> jps
* Browser: http://localhost:8080/
* terminal> spark-worker.sh
* terminal> jps
* Browser: http://localhost:8080/
* terminal> pyspark --master spark://localhost:7077
* terminal> jps
* On pyspark shell

```python
rdd = sc.textFile("file:///D:/setup/spark-3.0.1-bin-hadoop3.2/LICENSE")

rdd.map(lambda line: line.lower())\
	.saveAsTextFile("file:///D:/test/rdd")

rdd.map(lambda line: line.upper())\
	.saveAsTextFile("file:///D:/test/rdd2")

exit()
```

* terminal> spark-submit --master spark://localhost:7077  file:///D:/may21/dbda/bigdata/day12/rdd/demo01.py
* terminal> jps
* terminate the application.
* terminal> jps

* terminal> pyspark --master spark://localhost:7077 --executor-cores 4 --total-executor-cores 4
* terminal> spark-submit --master spark://localhost:7077 --executor-cores 4 --total-executor-cores 4 file:///D:/may21/dbda/bigdata/day12/rdd/demo01.py


## Dataframe Programming
* https://spark.apache.org/docs/2.3.0/sql-programming-guide.html
* https://spark.apache.org/docs/latest/sql-programming-guide.html

### Dataframe Assignments
* Implement Word Count program and find top 10 words (except stopwords).
* Find avg sal per job in emp.csv file.
* Find max sal per dept per job in emp.csv file.
* Find deptwise total sal from emp.csv and dept.csv. Print dname and total sal.
* Find number of movies for each genre using split() and explode().

#### Assignment Hints
* Load text file.
	```python
	lines = spark.read.text("file:///path/to/text/file")
	```
* where codition
	```python
	df2 = df1.where("job IN ('MANAGER', 'ANALYST', 'PRESIDENT')")
	```
* Specify schema of CSV file manually.
	```python
	books = spark.read\
		.schema("id INT, name STRING, author STRING, subject STRING, price DOUBLE")\
		.csv("file:///path/to/books.csv")
	```
* Specify NULL in CSV file.
	```python
	emps = spark.read\
		.schema(...)\
		.option("mode", "PERMISSIVE")
		.option("nullValues", "NULL")\
		.csv("file:///path/to/emp.csv")
	```

# Big Data

## Agenda
* Dataframes execution
* Dataframes: Rows & Columns
* Formats (Read/Writing)
* SQL functions
* Spark SQL

## Running Python Spark code
* terminal> python3 app.py
	* When run from PyCharm or any other IDE, internally does same.
	* Running application as a Python code.
	* Create SparkContext and runs in local mode.
* terminal> spark-submit --master local[*] app.py
	* Running application as Spark application.
	* Loads JVM process (for SparkSubmit) and then runs Python code in it creating SparkContext.
* When Spark application runs -- SparkContext is created -- it presents WebUI at port 4040+.

## SparkContext vs SparkSession
* SparkContext
	* Low level API. Works with RDDs and DAGs.
	* Single SparkContext in the application. Can be additional helper contexts like SQLContext, HiveContext, StreamingContext, etc.
	* SparkContext is in-charge of application. Creates RDDs & DAGs.
* SparkSession
	* High level / Structured API. Works with Dataframes.
	* Single SparkSession in the whole application. It encapsulate all the contexts.
	* SparkContext (within SparkSession) is in-charge of application. SparkSession is used to create Dataframes.

## Dataframes

### Reading Dataframes

#### spark.read.option("mode", "...")
* DROPMALFORMED: If data is not matching the schema, drop those rows.
* FAILFAST: If data is not matching the schema, fail read operation.
* PERMISSIVE: If data is not matching the schema, consider it as null type. (default).

#### df.write.mode("...").save()
* append: Append contents of this DataFrame to existing data.
* overwrite: Overwrite existing data.
* error or errorifexists: Throw an exception if data already exists.
* ignore: Silently ignore this operation if data already exists.

#### JDBC format
* terminal> sudo apt install mysql-server
* Create database and user in MySQL.
* Add mysql JDBC driver jar into $SPARK_HOME/jars directory.
* Implement spark application to write data into MySQL tables and execute it.
* mysql> USE sales;
* mysql> SELECT * FROM ncdc LIMIT 20;

##### Read data from MySQL/Hive using JDBC

```python
df = spark.read\
	.option("user", "...")\
	.option("password", "...")\
	.jdbc("jdbc://url", "table_name")
```

### Spark Dataframes and SQL assignments
* Implement WordCount program using SQL on Spark views.
* Find movies for each genre using SQL on Spark views.
* Find number of ratings for each year using dataframes and SQL on Spark Views.
* Read NCDC data from MySQL using JDBC and print average temperature per year.
* Read NCDC data from Hive using JDBC and print average temperature per year in descending order.
* Implement Movie recommendatation using Spark dataframes. You can configure driver memory in config() while creating SparkSession. Input MovieId from user and display similar movie titles. You can cache correlation dataframe to speed-up processing.

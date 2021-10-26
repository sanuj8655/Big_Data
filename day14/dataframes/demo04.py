#!/usr/bin/python3
from pyspark.sql import SparkSession
from pyspark.sql.functions import *

# create SparkSession object
spark = SparkSession.builder\
            .appName("EmpSummary")\
            .config("spark.sql.shuffle.partitions", "2")\
            .getOrCreate()

emps = spark.read\
            .schema("empno INT, ename STRING, job STRING, mgr INT, hire STRING, sal DOUBLE, comm DOUBLE, deptno INT")\
            .option("nullValue", "NULL")\
            .option("mode", "DROPMALFORMED")\
            .csv("file:///D:/may21/dbda/bigdata/data/emp.csv")

# result = emps\
#     .select("job", "sal", "comm")\
#     .withColumn("income", expr("sal + ifnull(comm, 0.0)"))

# result = emps\
#     .selectExpr("job", "sal + ifnull(comm, 0.0) AS income")\
#     .groupby("job").avg("income")\
#     .withColumnRenamed("avg(income)", "avg_income")

# SELECT job, AVG(income), SUM(income), MAX(income), MIN(income) FROM ... GROUP BY job;
result = emps\
    .selectExpr("job", "sal + ifnull(comm, 0.0) AS income")\
    .groupby("job")\
    .agg(avg("income").alias("avgin"), sum("income").alias("sumin"), max("income").alias("maxin"), min("income").alias("minin"))

# result.show()

result.coalesce(1)\
    .write\
    .mode("overwrite")\
    .parquet("file:///D:/test/emp_summary")

print("data is saved in the file.")

spark.stop()
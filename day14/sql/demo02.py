from pyspark.sql import SparkSession
from pyspark.sql.functions import *

# create SparkSession object
spark = SparkSession.builder\
            .appName("Emp")\
            .config("spark.sql.shuffle.partitions", "2")\
            .getOrCreate()

emps = spark.read\
            .schema("empno INT, ename STRING, job STRING, mgr INT, hire STRING, sal DOUBLE, comm DOUBLE, deptno INT")\
            .option("nullValue", "NULL")\
            .option("mode", "DROPMALFORMED")\
            .csv("file:///D:/may21/dbda/bigdata/data/emp.csv")

# emps.write.saveAsTable("emp_all", mode="OVERWRITE")
# print("emp_all table written.")

# emps.write\
#     .partitionBy("deptno")\
#     .saveAsTable("emp_dept_part", mode="OVERWRITE")
# print("emp_dept_part table written.")

# emps.write\
#     .partitionBy("deptno", "job")\
#     .saveAsTable("emp_dept_job_part", format="orc", mode="OVERWRITE")
# print("emp_dept_job_part table written.")

# emps.write\
#     .bucketBy(2, "empno")\
#     .saveAsTable("emp_bucketed", mode="OVERWRITE")
# print("emp_bucketed table written.")

emps.write\
    .partitionBy("deptno")\
    .bucketBy(2, "empno")\
    .saveAsTable("emp_dept_part_bucketed", mode="OVERWRITE")
print("emp_dept_part_bucketed table written.")

spark.stop()
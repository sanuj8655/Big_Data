#!/usr/bin/python3

from pyspark.sql import SparkSession
from pyspark.ml.feature import StringIndexer, VectorAssembler
from pyspark.ml.classification import LogisticRegression
from pyspark.ml.evaluation import BinaryClassificationEvaluator

spark = SparkSession.builder\
            .appName("ml02")\
            .master("local[2]")\
            .getOrCreate()

data = spark.read\
    .parquet("model1/data/part-00000-e30e01c9-5b1a-4f8b-a0a8-173b5b6e2dbe-c000.snappy.parquet")
data.printSchema()

data.show(truncate=False)

spark.stop()











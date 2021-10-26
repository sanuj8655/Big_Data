#!/usr/bin/python3

from pyspark.sql import SparkSession
from pyspark.ml.feature import StringIndexer, VectorAssembler
from pyspark.ml.classification import LogisticRegression
from pyspark.ml.evaluation import BinaryClassificationEvaluator

spark = SparkSession.builder\
            .appName("ml01")\
            .master("local[2]")\
            .getOrCreate()

data = spark.read\
    .option("header", "true")\
    .option("inferSchema", "true")\
    .csv("customers.csv")
data.printSchema()
# data.show(truncate=False, n=3)

df1 = data.withColumnRenamed("Purchased", "label")
df1.printSchema()
# df1.show(truncate=False, n=3)

genderIndexer = StringIndexer()\
    .setInputCol("Gender")\
    .setOutputCol("GenderIndexed")

df2 = genderIndexer.fit(df1)\
    .transform(df1)
df2.printSchema()
# df2.show(truncate=False, n=3)

vectAssembler = VectorAssembler()\
    .setInputCols(["Age", "Salary", "GenderIndexed"])\
    .setOutputCol("features")

df3 = vectAssembler.transform(df2)
df3.printSchema()
# df3.show(truncate=False, n=3)

df4 = df3.select("features", "label")
df4.printSchema()
# df4.show(truncate=False, n=3)

(train, test) = df4.randomSplit([0.7, 0.3])

model = LogisticRegression().fit(train)
predictions = model.transform(test)

predictions.printSchema()
predictions.show(truncate=False, n=3)

accuracy = BinaryClassificationEvaluator().evaluate(predictions)
print("Model Accuracy: ", accuracy)

model.save("model1")
print("Model saved.")

spark.stop()











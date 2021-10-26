#!/usr/bin/python3

from pyspark.sql import SparkSession
from pyspark.ml import Pipeline
from pyspark.ml.feature import StringIndexer, VectorAssembler
from pyspark.ml.classification import LogisticRegression
from pyspark.ml.evaluation import BinaryClassificationEvaluator

spark = SparkSession.builder\
            .appName("ml04")\
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
df1.show(truncate=False, n=3)

(train, test) = df1.randomSplit([0.7, 0.3])
# stage 1
genderIndexer = StringIndexer()\
    .setInputCol("Gender")\
    .setOutputCol("GenderIndexed")
# stage 2
vectAssembler = VectorAssembler()\
    .setInputCols(["Age", "Salary", "GenderIndexed"])\
    .setOutputCol("features")
# stage 3
model = LogisticRegression()
# create pipeline with stage 1, 2 and 3
mlPipeline = Pipeline()\
    .setStages([genderIndexer, vectAssembler, model])

trainedModel = mlPipeline.fit(train)
predictions = trainedModel.transform(test)

predictions.printSchema()
predictions.show(truncate=False, n=30)

accuracy = BinaryClassificationEvaluator().evaluate(predictions)
print("Model Accuracy: ", accuracy)

trainedModel.save("model2")
print("Model saved.")

spark.stop()











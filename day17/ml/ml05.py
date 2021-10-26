#!/usr/bin/python3

from pyspark.sql import SparkSession, Row
from pyspark.ml.feature import StringIndexer, VectorAssembler
from pyspark.ml import PipelineModel
from pyspark.ml.evaluation import BinaryClassificationEvaluator

spark = SparkSession.builder\
            .appName("ml05")\
            .master("local[2]")\
            .getOrCreate()


def prepare_data(age, sal, gender):
    spark = SparkSession.builder.getOrCreate()

    # creare Spark dataframe with single row -- user input
    custSchema = "CustID INT, Age INT, Salary DOUBLE, Gender STRING, label DOUBLE"
    row = Row(0, age, sal, gender, 0.0)
    df1 = spark.createDataFrame([row], custSchema)

    return df1


def predict_purchase():
    model = PipelineModel.load("model2")
    while True:
        userId = int(input("Enter CustId: "))
        if userId == 0:
            break
        gender = str(input("Enter Gender (Male/Female): "))
        age = int(input("Enter Age: "))
        sal = float(input("Enter Salary: "))

        data = prepare_data(age, sal, gender)
        data.show(truncate=False)

        prediction = model.transform(data)
        prediction.show(truncate=False)

        row = prediction.select("prediction").first()
        predict = float(row[0])
        if predict == 0.0:
            print("Will purchase: NO")
        else:
            print("Will purchase: YES")

predict_purchase()

spark.stop()


#!/usr/bin/python3

from pyspark.sql import SparkSession, Row
from pyspark.ml.feature import StringIndexer, VectorAssembler
from pyspark.ml.classification import LogisticRegressionModel
from pyspark.ml.evaluation import BinaryClassificationEvaluator

spark = SparkSession.builder\
            .appName("ml02")\
            .master("local[2]")\
            .getOrCreate()


def prepare_data(age, sal, gender):
    spark = SparkSession.builder.getOrCreate()

    # creare Spark dataframe with single row -- user input
    custSchema = "CustID INT, Age INT, Salary DOUBLE, Gender STRING, label DOUBLE"
    row = Row(0, age, sal, gender, 0.0)
    df1 = spark.createDataFrame([row], custSchema)

    # StringIndexer --> Male, Female = 1,0
    genderDF = spark.createDataFrame([[("Male")], [("Female")]], ["Gender"])
    genderIndexer = StringIndexer() \
        .setInputCol("Gender") \
        .setOutputCol("GenderIndexed")

    df2 = genderIndexer.fit(genderDF) \
        .transform(df1)

    # VectorAssembler --> ["Age", "Salary", "GenderIndexed"] --> "features"
    vectAssembler = VectorAssembler() \
        .setInputCols(["Age", "Salary", "GenderIndexed"]) \
        .setOutputCol("features")

    df3 = vectAssembler.transform(df2)
    df3.printSchema()

    df4 = df3.select("features", "label")
    return df4


def predict_purchase():
    model = LogisticRegressionModel.load("model1")
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


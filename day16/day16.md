# Big Data

## Agenda
* Hive JDBC connection
* DStreams Introduction
* Structured Streaming
* Structured Streaming
	* Source
	* Sinks
	* Triggers
	* Watermark
	* Windowing
* Apache Kafka
* Spark Kafka Integration

## Hive JDBC connection
* https://issues.apache.org/jira/browse/SPARK-22016
* Custom HiveDialect and Register it.
	* dept.dname --> `dept`.`dname`

## DStreams Introduction
* SparkStreamingContext
	* Stream source
	* Stream processing
	* Stream sink
* The processing is repeated after given batch duration e.g. 10 seconds.

## Spark Structured Streaming
* Spark Dataframe is wrapper on Spark RDD.
* Spark Structured Streaming is NOT wrapper on Spark DStreams.
* Spark Structured Streaming a new framework developed from scratch.
* Spark Structured Streaming data is considered as infinite Dataframe i.e. new data gets appended at the dataframe. Hence most of dataframe operations are applicable on structured streaming.

### Output Modes
* append --- only if no aggregations in the proceswsing.
	* result is processed and appended to the sink.
	* mainly used for data cleaning/filtering.
	* e.g. twitter tweets -- sentiment analysis i.e. tweet --> score.
* complete -- works with aggregation operations
	* the full aggregate result is displayed each time.
	* e.g. live poll with 4 options -- live counting -- final result is not too big.
* update -- works with or without aggregation operations
	* only updated/modified aggregate result is displayed each time.
	* e.g. monitor cabs (GPS) movement -- only modified results to be displayed.

## Apache Kafka
* Application1 --> Distributed Message Broker --> Application2

## Producer and consumer

```sh
kafka-topics.sh --zookeeper localhost:2181 --create --topic newtopic --replication-factor 1 --partitions 2

kafka-topics.sh --zookeeper localhost:2181 --list
```

```sh
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic newtopic --group 1

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic newtopic --group 2

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic newtopic --group 2

kafka-console-producer.bat --broker-list localhost:9092 --topic newtopic
```








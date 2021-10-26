# Big Data

## Agenda
* Spark Kafka Integration
* Spark ML

## Kafka
* App1 --> Kafka --> App2
* Kafka cluster --> Multiple nodes --> Running broker (JVM process).
* Kafka Topic --> Logical entity on which messages are sent (published) and received (subscribed).
* Topic is divided into multiple parts --> Partitions (Physical entity).
	* Each broker have one or partitions of same or different topics.
	* Data is appended into the partitions with serial numbers -- offsets.
* Topics are replicated i.e. each partition copied maintained on multiple brokers.
	* One replica -- Leader
	* Other replicas -- Followers (Replicas)
	* Replicas try to be in sync with Leader.
* Producer --> Topic partitions --> Leader 
	* Round robin or Hash of key.
* Consumer <-- Topic partitions <-- Leader or Replicas.
* Consumer group -- set of consumers
	* Message is received by any one consumer in the group.

### Spark Kafka Integration
* Start Kafka cluster.
	* Start zookeeper.
	* Start broker.
* List the topics.
	* kafka-topics.sh --zookeeper localhost:2181 --list
* Create iot topic (if not present).
	* kafka-topics.sh --zookeeper localhost:2181 --create --topic iot --replication-factor 1 --partitions 2
* Execute Kafka console consumer.
	* kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic iot

## Advanced Analytics
* Analysis vs Analytics
	* Analysis --> Past data -- Understading data, Summarizing data, Visualizing data.
		* Python: Pandas, Numpy, Matplotlib, etc.
		* R: Frames, Stats, Charts, etc.
		* Excel: Ordering, Filtering, Pivot table/charts, etc.
		* PowerBI: Charts, Reports, etc. 
	* Analytics --> Future/Predictions
		* Machine Learning --> Single system --> Languages: Python, R, C++, etc
		* Machine Learning --> Distributed systems --> Mahaout on Hadoop (outdated), Spark ML, etc.

### Analytics
* Analytics refer to various techniques to solve core problem of deriving insights and making predictions/recommendations.
* Most common tasks are:
	* Supervised Learning
		* most common
		* using historical data train model
			* data have label (dependent variable)
			* data have features
		* Includes
			* classification: predict disease, predict purchase or not, classify images, ...
			* regression: predict sales, predict viewer count, ...
	* Recommendation Engines
		* movie recommendation, product recommendation, ...
	* Unsupervised Learning
		* find pattern or discover underlying struct in data
		* clustering, anomaly detection, topic modeling, ...
	* Graph Analytics
		* based on graph data struct algos
		* fraud detection, classficiation, recommendation, find patterns in social network, ...

### Analytics Process
1. Collect relevant data.
2. Clean & inspect the data -- EDA.
3. Feature Engg -- Extract features into numerical vectors
4. Build ML model using portion of data (training set).
5. Evaluate model using portion of data (test set).
6. Serve ML model to predict, recommend, ...


### Spark ML
1. Collect relevant data.
	* Spark can get data from any source -- HDFS, S3, RDBMS, NoSQL, ...
	* Spark can get live data (streaming) -- Kafka, Flume, Kinesis, ...
2. Clean & inspect the data.
	* Spark can do Regex, Filtering, Corrections, Enriching, etc.
	* Spark can do Batch processing and/or Streaming processing
3. Feature Engg -- Extract features into numerical vectors
	* Spark has Transformers and Estimators.
4. Build ML model using portion of data (training set).
	* Spark ML supports Supervised ML, Unsupervised ML, Recommendations, NLP, etc.
5. Evaluate model using portion of data (test set).
	* Spark does accuracy check using Evaluators.
6. Serve ML model to predict, recommend, ...
	* Allows to export model in various formats like PMML, Pickle, etc.

#### Spark ML components
* Includes data collection, cleaning, feature engg, training, evaluating large scale supervised & unsupervised models.
* Advantages/Applications
	* Preprocessing & feature engg.
	* Building models for huge train data.
* (High Level) Components
	* Transformers -- transform
	* Estimators -- fit + transform
	* Evaluators -- checking accuracy of model on test data
	* Pipelines -- stages to build model (includes Transformers & Estimators).
* (Low Level) Components
	* Vectors -- Sparse or Dense vectors.

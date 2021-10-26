# Big Data

## Agenda
* Spark SQL
* Spark Streaming

## Spark SQL

### Spark SQL setup steps
* Download spark and extract it.
* In ~/.bashrc, set SPARK_HOME and PATH.
* Setup single-node cluster settings spark-defaults.conf and spark-env.sh
* Copy hive-site.xml in $SPARK_HOME/conf.
* Start Master and Slaves.
	* terminal> start-master.sh
	* terminal> start-slaves.sh
* Start ThriftServer.
	* terminal> start-thriftserver.sh
	* Internally creates spark-warehouse directory and spark metastore_db (in Hive metastore format).
* Start beeline.
	* terminal> beeline -u jdbc:hive2://localhost:10000 -n $USER
* Run following commands on beeline prompt

```SQL
SHOW DATABASES;

CREATE DATABASE test;

SHOW DATABASES;

USE test;

SHOW TABLES;

-- using hive serde/inputformats
CREATE TABLE lbooks(id INT, name STRING, author STRING, subject STRING, price DOUBLE) 
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

SHOW TABLES;

LOAD DATA
LOCAL INPATH '/home/sunbeam/may21/bigdata/data/books.csv'
INTO TABLE lbooks;

SELECT * FROM lbooks LIMIT 10;

SELECT subject, SUM(price) FROM lbooks
GROUP BY subject;
-- browser: http://localhost:8080 -- Hive ThriftServer2

SET spark.sql.shuffle.partitions = 2;

SELECT subject, SUM(price) FROM lbooks
GROUP BY subject;
-- browser: http://localhost:8080 -- Hive ThriftServer2
```

```SQL
-- using spark data formats
-- spark managed tables
CREATE TABLE sbooks(id INT, name STRING, author STRING, subject STRING, price DOUBLE) 
USING csv;

INSERT INTO sbooks
SELECT * FROM lbooks;

SELECT * FROM sbooks LIMIT 10;

DROP TABLE sbooks;
-- delete metadata from metastore_db.
-- delete data from spark-warehouse dir.

```

```SQL
-- spark external table
CREATE TABLE movies(movieId INT,
title STRING,
genres STRING)
USING csv OPTIONS(header true, path 'file:///home/sunbeam/movies_db');

SELECT * FROM movies LIMIT 20;

CREATE VIEW movie_genres AS
SELECT movieId, title, explode(split(genres, '[|]')) genre FROM movies;

SELECT * FROM movie_genres LIMIT 20;

SELECT genre, COUNT(movieId) FROM movie_genres
GROUP BY genre;

SHOW TABLES;

DROP VIEW movie_genres;

DROP TABLE movies;
```

## Tweepy
* Twitter tweet format: https://developer.twitter.com/en/docs/twitter-api/v1/data-dictionary/object-model/tweet









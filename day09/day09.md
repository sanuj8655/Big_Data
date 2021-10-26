# Big Data

## Agenda
* Hive

## Hive

### Hive installation

#### Hive versions
* Hive 0.x --> Outdated
* Hive 1.x --> Compatible with Hadoop 1.x --> Outdated
* Hive 2.x --> Compatible with Hadoop 2.x
* Hive 3.x --> Compatible with Hadoop 3.x

#### Hive components
* Config --> hive-site.xml
	* Metastore settings
	* Metastore thrift service -- 9083 (optional for Hive CLI)
	* HDFS warehouse directory
	* Security config (disabled)
	* Partitioning, Bucketing, Compaction, TransactionManager
* Hive Metastore service
* HiveServer2
* Hive CLI
* Hive Beelline

### Hive QL
* DDL: CREATE DATABASE, CREATE TABLE, TRUNCATE, ...
* DQL
* DML
* DCL

```SQL
TRUNCATE TABLE students;

SELECT * FROM students;

DESCRIBE students;
```

* newstudents.csv --> /home/sunbeam/share/newstudents.csv

```SQL
LOAD DATA LOCAL INPATH '/home/sunbeam/share/newstudents.csv'
INTO TABLE students;

SELECT * FROM students;
```

```SQL
CREATE TABLE books (id INT, name STRING, author STRING, subject STRING, price DECIMAL(8,3))
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

LOAD DATA LOCAL INPATH '/home/sunbeam/may21/bigdata/data/books.csv'
INTO TABLE books;

SELECT * FROM books;

SELECT * FROM books WHERE price > 500.0;

SELECT subject, SUM(price) total FROM books GROUP BY subject;

SELECT * FROM books ORDER BY price DESC LIMIT 3;
```

### Hive Data Types
* https://cwiki.apache.org/confluence/display/hive/languagemanual+udf

```
1,nilesh ghule,nilesh@sunbeaminfo.com|ghule.nilesh@gmail.com,marketyard|pune|411037,office:24260308|mobile:9527331338
2,nitin kudale,nitin@sunbeaminfo.com,marketyard|pune|411037,office:24260308|mobile:9881208115
3,prashant lad,prashant@sunbeaminfo.com,peth|karad|421021,office:225500|mobile:9881208114
```

```SQL
CREATE TABLE contacts(
id INT,
name STRING,
emails ARRAY<STRING>,
addr STRUCT<area:STRING, dist:STRING, pin: INT>,
phones MAP<STRING,STRING>
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
COLLECTION ITEMS TERMINATED BY '|'
MAP KEYS TERMINATED BY ':'
STORED AS TEXTFILE;

DESCRIBE contacts;

LOAD DATA LOCAL INPATH '/home/sunbeam/may21/bigdata/data/contacts.csv'
INTO TABLE contacts;

SELECT * FROM contacts;

LOAD DATA LOCAL INPATH '/home/sunbeam/may21/bigdata/data/contacts.csv'
INTO TABLE contacts;
-- append to existing data in the table

SELECT * FROM contacts;

LOAD DATA LOCAL INPATH '/home/sunbeam/may21/bigdata/data/contacts.csv'
OVERWRITE INTO TABLE contacts;
-- truncate existing data and upload new file.

SELECT * FROM contacts;

SELECT emails, addr, phones FROM contacts;

-- display first email, dist and mobile number of contacts.
SELECT emails[0], addr.dist, phones['mobile'] FROM contacts;

-- display user whose email is nilesh@sunbeaminfo.com.
SELECT name, emails FROM contacts WHERE emails[0] = 'nilesh@sunbeaminfo.com';

SELECT name, emails FROM contacts WHERE ARRAY_CONTAINS(emails, 'nilesh@sunbeaminfo.com');

-- display user who have more than one email.
SELECT name, emails FROM contacts WHERE SIZE(emails) > 1;
```

```SQL
-- display all users in dist 'pune'.
SELECT name, addr FROM contacts WHERE addr.dist = 'pune';
```

```SQL
-- display all users whose office number is '24260308'
SELECT name, phones FROM contacts WHERE phones['office'] = '24260308';

-- display all users whose mobile number start from '98812'.
SELECT name, phones FROM contacts WHERE phones['mobile'] LIKE '98812%';
```

### SerDe

```SQL
CREATE TABLE movies(
id INT,
title STRING,
genres STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

LOAD DATA LOCAL INPATH '/home/sunbeam/share/movies/movies.csv'
INTO TABLE movies;

SELECT * FROM movies LIMIT 15;

DROP TABLE movies;
```

```SQL
CREATE TABLE movies_staging(
id INT,
title STRING,
genres STRING
)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
WITH SERDEPROPERTIES(
'separatorChar'=',',
'quoteChar'='\"',
'escapeChar'='\\'
)
TBLPROPERTIES('skip.header.line.count'='1');

LOAD DATA LOCAL INPATH '/home/sunbeam/share/movies/movies.csv'
INTO TABLE movies_staging;

SELECT * FROM movies_staging LIMIT 15;
```

* ncdc dataset
	* sensor reading -- one line
		* year: 15-19
		* temperature: 87-92
		* quality: 92-93

```SQL
CREATE TABLE ncdc(
year SMALLINT,
temperature SMALLINT,
quality TINYINT
)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES('input.regex'='^.{15}([0-9]{4}).{68}([-\+][0-9]{4})([0-9]).*$');

LOAD DATA LOCAL INPATH '/home/sunbeam/may21/bigdata/data/ncdc/*'
INTO TABLE ncdc;

SELECT * FROM ncdc LIMIT 20;

SELECT year, AVG(temperature) avgtemp FROM ncdc
WHERE quality IN (0, 1, 4, 5, 9) AND temperature != 9999
GROUP BY year;

(SELECT year, AVG(temperature) avgtemp FROM ncdc
WHERE quality IN (0, 1, 4, 5, 9) AND temperature != 9999
GROUP BY year
ORDER BY avgtemp DESC LIMIT 1)
UNION
(SELECT year, AVG(temperature) avgtemp FROM ncdc
WHERE quality IN (0, 1, 4, 5, 9) AND temperature != 9999
GROUP BY year
ORDER BY avgtemp ASC LIMIT 1);
```

### Hive 2.x Views
* Hive 2.x views are same as MySQL views.

```SQL
CREATE VIEW ncdc_yearavg AS
SELECT year, AVG(temperature) avgtemp FROM ncdc
WHERE quality IN (0, 1, 4, 5, 9) AND temperature != 9999
GROUP BY year;

SHOW TABLES;

(SELECT year, avgtemp FROM ncdc_yearavg ORDER BY avgtemp DESC LIMIT 1)
UNION
(SELECT year, avgtemp FROM ncdc_yearavg ORDER BY avgtemp ASC LIMIT 1);
```

### ORC FORMAT
* Text Data (Extract) --(Load)--> Hive Text Table (Staging Table) --(Transform)--> Hive Orc Table

```SQL
CREATE TABLE ncdc_orc(
year SMALLINT,
temperature SMALLINT,
quality TINYINT
)
STORED AS ORC;

INSERT INTO ncdc_orc
SELECT * FROM ncdc WHERE quality IN (0, 1, 4, 5, 9) AND temperature != 9999;

(SELECT year, AVG(temperature) avgtemp FROM ncdc_orc
GROUP BY year
ORDER BY avgtemp DESC LIMIT 1)
UNION
(SELECT year, AVG(temperature) avgtemp FROM ncdc_orc
GROUP BY year
ORDER BY avgtemp ASC LIMIT 1);
```

```SQL
SELECT * FROM movies_staging LIMIT 20;

DESCRIBE movies_staging;

SELECT SPLIT(genres, '\\|') FROM movies_staging LIMIT 5;

CREATE TABLE movies(
id INT,
title STRING,
genres ARRAY<STRING>
)
STORED AS ORC;

INSERT INTO movies
SELECT id, title, SPLIT(genres, '\\|') FROM movies_staging;

SELECT * FROM movies LIMIT 5;

DESCRIBE movies;

SELECT COUNT(id) FROM movies WHERE ARRAY_CONTAINS(genres, 'Romance');
```

### EXPLODE() function
* EXPLODE() is table valued function i.e. converts single row into multiple rows.
* Similar to mongo $unwind operation.

```
| 2          | Jumanji (1995)                      | ["Adventure","Children","Fantasy"]                 |
```

```
| 2          | Jumanji (1995)                      | Adventure   |
| 2          | Jumanji (1995)                      | Children    |
| 2          | Jumanji (1995)                      | Fantasy     |
```


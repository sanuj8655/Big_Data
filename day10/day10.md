# Big Data

## Hive
* Hive UDFs
* Hive Joins
* Hive DML & Transactions
* Hive views
* Hive indexes
* External tables
* Partitioning
* Bucketing
* Python connectivity

## Hive UDFs
* Hive functions
	* Scalar functions
	* Group functions
	* Table functions

### EXPLODE()
* Input row: ["Adventure","Children","Fantasy"]
* Output table -- LATERAL VIEW -- genres_tbl
	-------------------
	| genre           |
	|-----------------|
	| Adventure       |
	| Children        |
    | Fantasy         |
	-------------------

```SQL
USE edbda;

SHOW TABLES;

DESCRIBE movies;

SELECT * FROM movies LIMIT 5;

SELECT id, title, EXPLODE(genres) AS genre FROM movies;
-- error in hive (but work in spark sql).

SELECT id, title, genre FROM movies
LATERAL VIEW EXPLODE(genres) genres_tbl AS genre
LIMIT 20;

SELECT genre, COUNT(id) FROM
(SELECT id, title, genre FROM movies
LATERAL VIEW EXPLODE(genres) genres_tbl AS genre) AS movie_genres
GROUP BY genre;
-- group by on derived table
```

```SQL
-- get the top 10 words in a text file except stop words.
CREATE TABLE lines(line STRING) STORED AS TEXTFILE;

LOAD DATA LOCAL INPATH '/home/sunbeam/hadoop-3.2.0/LICENSE.txt'
INTO TABLE lines;

SELECT * FROM lines LIMIT 20;

SELECT LOWER(line) FROM lines LIMIT 20;

SELECT SPLIT(LOWER(line), '[^a-z]') FROM lines LIMIT 20;

SELECT EXPLODE(SPLIT(LOWER(line), '[^a-z]')) FROM lines LIMIT 20;
-- error in hive 2.x (work in hive 3.x spark sql)

SELECT word FROM lines
LATERAL VIEW EXPLODE(SPLIT(LOWER(line), "[^a-z]")) line_words AS word
LIMIT 100;

-- LAB WORK: on the output of above query
--	1. filter out stop words -- NOT IN ('', 'as', 'in', 'of', ...)
--	2. group by word and count
--	3. sort desc by count
--	4. limit 10
```

```SQL
CREATE TABLE ratings_staging(userid INT, movieid INT, rating DOUBLE, rtime BIGINT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
TBLPROPERTIES('skip.header.line.count'='1');

LOAD DATA LOCAL INPATH '/home/sunbeam/share/movies/ratings.csv'
INTO TABLE ratings_staging;

SELECT * FROM ratings_staging LIMIT 10;

SELECT userid, movieid, rating, FROM_UNIXTIME(rtime) FROM ratings_staging LIMIT 10;

CREATE TABLE ratings(userid INT, movieid INT, rating DOUBLE, rtime TIMESTAMP)
STORED AS ORC;

INSERT INTO ratings
SELECT userid, movieid, rating, FROM_UNIXTIME(rtime) FROM ratings_staging;

SELECT YEAR(rtime), COUNT(*) FROM ratings
GROUP BY YEAR(rtime);

```

## Hive Joins
* Same as SQL joins i.e. Cross Join, Inner Join, Outer Joins, Self Joins, etc.

```SQL
-- find top 20 movies (movies with max number of ratings)
SELECT movieid, COUNT(*) cnt FROM ratings
GROUP BY movieid
ORDER BY cnt DESC
LIMIT 20;

SELECT m.title, COUNT(r.rating) cnt FROM movies m
LEFT JOIN ratings r ON m.id = r.movieid
GROUP BY m.title
ORDER BY cnt DESC
LIMIT 20;
-- job1: join + group --> map side join --> 1 mapper and 1 reducer
-- job2: order by

SET hive.auto.convert.join=false;
SET hive.auto.convert.join.noconditionaltask=false;

SELECT m.title, COUNT(r.rating) cnt FROM movies m
LEFT JOIN ratings r ON m.id = r.movieid
GROUP BY m.title
ORDER BY cnt DESC
LIMIT 20;

-- job1: join + group --> reducer side join --> 2 mappers and 1 reducer
-- job2: order by
```

## Hive Scripts
* hive> SOURCE /home/sunbeam/share/top20movies.hql
* OR
* beeline> !run /home/sunbeam/share/top20movies.hql

## Hive connectivity

### Hive Python connectivity
* terminal> sudo apt install python3-dev libsasl2-dev python3-pip
* terminal> python3.8 -m pip install thrift sasl thrift_sasl
* terminal> python3.8 -m pip install pyhive
* terminal> python3.8 /home/sunbeam/share/python-hive.py

### SQL Injection
* When SQL queries are formed using string concat, then end user may add malicious contents into the query so that whole data is fetched or manipulated. This is called as SQL injection.
* To ensure that SQL injection is disabled, it is standard practice not to create queries with string concat. Instead parameterized queries are used and param values are set (taken from user).

### Hive Java connectivity
* Hive can be connected from Java using JDBC.
* Java App --> JDBC Driver --> RDBMS
* JDBC Driver is a component that converts Java requests into RDBMS understandable form and RDBMS response into Java understandable form.
* JDBC Driver are typically packaged as "JAR" -- set of classes inherited from JDBC interfaces.
* JDBC interfaces/objects:
	* Driver -- responsible for making database connection (socket)
	* Connection -- wrapper on socket connection and communicate with database.
	* Statement -- execute SQL query on server and get the result.
	* ResultSet -- represent result from SELECT query and access it row by row.
* JDBC steps
	* step 1: add JDBC driver into classpath.
	* step 2: load and register driver.
	* step 3: create connection object using DriverManager.
	* step 4: create statement object using Connection.
	* step 5: execute statement object and process the result.
	* step 6: close all (statement, connection).

#### Hive JDBC steps
* step 1: add JDBC driver into classpath.
	* Maven Project: Add hive-jdbc dependency.
		```xml
		<dependency>
			<groupId>org.apache.hive</groupId>
			<artifactId>hive-jdbc</artifactId>
			<version>3.1.2</version>
		</dependency>
		```
* step 2: load and register driver (one time - static block).
	```Java
	static {
		try {
			String DB_DRIVER = "org.apache.hive.jdbc.HiveDriver";
			Class.forName(DB_DRIVER);
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	```
* step 3: create connection object using DriverManager.
	```Java
	String DB_URL = "jdbc:hive2://localhost:10000/edbda";
	String DB_USER = "sunbeam";
	String DB_PASSWORD = " ";
	Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	```
* step 4: create statement object using Connection.
	```Java
	String sql = "SELECT * FROM books WHERE id = ?";
	PreparedStatement stmt = con.prepareStatement(sql);
	```
* step 5: execute statement object (executeQuery() for SELECT or executeUpdate() for non-SELECT) and process the result.
	```Java
	System.out.print("Enter id: ");
	int id = scanner.nextInt();
	stmt.setInt(1, id);
	ResultSet rs = stmt.executeQuery();
	while(rs.next()) {
		int id = rs.getInt("id");
		String name = rs.getString("name");
		String author = rs.getString("author");
		String subject = rs.getString("subject");
		double price = rs.getDouble("price");
		System.out.println(id + ", " + name + ", " + author + ", " + subject + ", " + price);
	}
	```
* step 6: close all (statement, connection).
	```Java
	rs.close();
	stmt.close();
	con.close();
	```

## Hive DML

```SQL
CREATE TABLE books_orc(
id INT,
name STRING,
author STRING,
subject STRING,
price DOUBLE
)
STORED AS ORC
TBLPROPERTIES('transactional'='true');

INSERT INTO books_orc
SELECT * FROM books;

SELECT * FROM books_orc LIMIT 15;

UPDATE books_orc SET price = price + price * 0.10 WHERE subject = 'C Programming';

SELECT * FROM books_orc LIMIT 15;

UPDATE books_orc SET price = price - price * 0.10 WHERE subject = 'Java Programming';

ALTER TABLE books_orc COMPACT 'major';

SELECT * FROM books_orc LIMIT 15;

```

## Hive 3.x Materialized Views
* Materialized view is caching the result of view SELECT query.
* This will speed-up queries executed on the view at the cost of HDFS space occupied by view.

```SQL
SELECT subject, SUM(price) total, AVG(price) avgprice FROM books_orc
GROUP BY subject
HAVING SUM(price) > 1500.0;

CREATE VIEW v_booksummary AS
SELECT subject, SUM(price) total, AVG(price) avgprice FROM books_orc
GROUP BY subject;

SELECT * FROM v_booksummary
WHERE total > 1500.0;

EXPLAIN
SELECT * FROM v_booksummary
WHERE total > 1500.0;

CREATE MATERIALIZED VIEW mv_booksummary AS
SELECT subject, SUM(price) total, AVG(price) avgprice FROM books_orc
GROUP BY subject;

SELECT * FROM mv_booksummary
WHERE total > 1500.0;

EXPLAIN
SELECT * FROM mv_booksummary
WHERE total > 1500.0;

SHOW TABLES;

INSERT INTO books_orc VALUES
(5001, 'Atlas Shrugged', 'Ayn Rand', 'Novell', 723.8),
(5002, 'The Fountainhead', 'Ayn Rand', 'Novell', 423.39),
(5003, 'The Alchemist', 'Paulo Cohelo', 'Novell', 528.9);

SELECT * FROM mv_booksummary
WHERE total > 500.0;

SELECT * FROM v_booksummary
WHERE total > 500.0;

ALTER MATERIALIZED VIEW mv_booksummary REBUILD;

SELECT * FROM mv_booksummary
WHERE total > 500.0;

SHOW MATERIALIZED VIEWS;

DROP MATERIALIZED VIEW mv_booksummary;
```
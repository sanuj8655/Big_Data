### Spark Hive Integration
* Spark store table metadata in hive format.
* Spark catalog is abstraction of hive metastore.
* Spark metadata format is compatible with hive 2.3.7.
* Spark can directly use existing Hive installation.

#### Installation steps

##### Hive 2.3.7 Installation with Remote Metastore
* step 1: Download and extract hive-2.3.7 into $HOME. Set HIVE_HOME and PATH in ~/.bashrc.
* step 2: Create hive-site.xml in $HIVE_HOME/conf directory.

```xml
<?xml version="1.0" encoding="UTF-8"?><?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
	<property>
		<name>javax.jdo.option.ConnectionURL</name>
		<value>jdbc:mysql://localhost:3306/metastore_db</value>
	</property>
	<property>
		<name>javax.jdo.option.ConnectionDriverName</name>
		<value>com.mysql.cj.jdbc.Driver</value>
	</property>
	<property>
		<name>javax.jdo.option.ConnectionUserName</name>
		<value>hive</value>
	</property>
	<property>
		<name>javax.jdo.option.ConnectionPassword</name>
		<value>hive</value>
	</property>
	<property>
		<name>hive.metastore.warehouse.dir</name>
		<value>hdfs://localhost:9000/user/hive1/warehouse</value>
	</property>
	<property>
		<name>javax.jdo.PersistenceManagerFactoryClass</name>
		<value>org.datanucleus.api.jdo.JDOPersistenceManagerFactory</value>
	</property>
	<property>
		<name>hive.metastore.uris</name>
		<value>thrift://0.0.0.0:9083</value>
	</property>
	<property>
		<name>hive.server2.enable.doAs</name>
		<value>false</value>
	</property>
	<property>
		<name>hive.support.concurrency</name>
		<value>true</value>
	</property>
	<property>
		<name>hive.enforce.bucketing</name>
		<value>true</value>
	</property>
	<property>
		<name>hive.exec.dynamic.partition.mode</name>
		<value>nonstrict</value>
	</property>
	<property>
		<name>hive.txn.manager</name>
		<value>org.apache.hadoop.hive.ql.lockmgr.DbTxnManager</value>
	</property>
	<property>
		<name>hive.compactor.initiator.on</name>
		<value>true</value>
	</property>
	<property>
		<name>hive.compactor.worker.threads</name>
		<value>2</value>
	</property>
</configuration>
```

* step 3: Create hive warehouse directory in HDFS.
	* terminal> hadoop fs -mkdir -p /user/hive1/warehouse
	* terminal> hadoop fs -chmod g+w /user/hive1/warehouse

* step 4: Copy mysql driver jars into $HIVE_HOME/lib.
* step 5: Create database for hive metastore in mysql (with root login).

```SQL
CREATE DATABASE metastore_db;

CREATE USER hive@localhost IDENTIFIED BY 'hive';

GRANT ALL PRIVILEGES ON metastore_db.* TO hive@localhost;

FLUSH PRIVILEGES;

EXIT;
```

* step 6: Create metastore using hive schematool.
	* terminal> schematool -initSchema -dbType mysql -verbose

* step 7: Start hive metastore service (9083 port)
	* terminal> hive --service metastore

##### Spark Installation
* step 8: Install spark (in "psuedo-distributed" or full-distributed mode).
	* Download & extract spark-3.x and set SPARK_HOME & update PATH in ~/.bashrc.
	* In $SPARK_HOME/conf/spark-defaults.conf:
		* spark.master	spark://localhost:7077
	* In $SPARK_HOME/conf/spark-env.sh
		* export SPARK_MASTER_HOST=localhost
		* export SPARK_LOCAL_IP=localhost
	* Copy hive-site.xml into $SPARK_HOME/conf.

* step 9: Start spark.
	* terminal> start-master.sh
	* terminal> start-workers.sh

* step 10: Start spark thrift server (10000 port)
	* terminal> start-thriftserver.sh

* step 11: Start spark beeline.
	* terminal> beeline -u jdbc:hive2://localhost:10000 -n $USER


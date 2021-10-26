### Psuedo-Distribution mode installation
1. Download and extract hbase-2.4.0-bin.tar.gz from this link https://archive.apache.org/dist/hbase/2.4.0/
2. .bashrc: 
	- export HBASE_HOME=<hbase dir>
	- export PATH=$HBASE_HOME/bin:$PATH
3. $HBASE_HOME/conf/hbase-env.sh
	- export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
	- export HBASE_MANAGES_ZK=true
4. $HBASE_HOME/conf/hbase-site.xml

	```xml
	<property>
		<name>hbase.cluster.distributed</name>
		<value>true</value>
	</property>
	<property>
		<name>hbase.rootdir</name>
		<value>hdfs://localhost:9000/hbase</value>
	</property>
	```

5. In hdfs create directiry /hbase (from terminal):
	- start-dfs.sh
	- hadoop fs -mkdir /hbase
6. Start HBase (from terminal):
	- start-hbase.sh
7. verify:
	- jps
		- HMaster
		- HRegionServer
		- HQuorumPeer
8. verify: webui
	http://localhost:16010 (HMaster)

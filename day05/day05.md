# Big Data

## Map Reduce

### Word Count

#### Pseudo Code

```Java
class WordCountMapper {
	// executed once for each line
	void map(String line, Context ctx) {
		String[] words = line.split("[^a-z]")
		for(String word: words)
			ctx.write(word, 1);
	}
}
```

```Java
class WordCountReducer {
	// executed once for each group
	void reduce(String word, Iterable<Integer> counts, Context ctx) {
		int sum = 0;
		for(int cnt: counts)
			sum = sum + cnt;
		ctx.write(word, sum);
	}	
}
```

#### Hadoop Wrapper/IO classes
* Hadoop wrapper classes - Writable classes - does efficient transfer over the network.
* Declared in package hadoop.io
* int --> Integer --> IntWritable
* float --> Float --> FloatWritable
* double --> Double --> DoubleWritable
* String --> ~StringWritable~ Text
* 

#### Hadoop MR Code

```Java
class WordCountMapper extends Mapper<LongWritable,Text,Text,IntWritable> {
	// executed once for each line
	@Override
	public void map(LongWritable keyOffset, Text valueLine, MapperLongWritable,Text,Text,IntWritable>.Context ctx) {
		String line = valueLine.toString();
		String[] words = line.split("[^a-z]");
		for(String word: words) {
			Text keyWord = new Text(word);
			IntWritable valueOne = new IntWritable(1);
			ctx.write(keyWord, valueOne);
		}
	}
}
```

```Java
class WordCountReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
	// executed once for each group
	@Override
	public void reduce(Text keyWord, Iterable<IntWritable> valueCounts, Reducer<Text,IntWritable,Text,IntWritable>.Context ctx) {
		int sum = 0;
		for(IntWritable valueCnt: valueCounts) {
			int cnt = valueCnt.get();
			sum = sum + cnt;
		}
		IntWritable valueSum = new IntWritable(sum);
		ctx.write(keyWord, valueSum);
	}	
}
```

* WordCount -- Main class.
	* Configure MR job and submit.
	* Command line arguments:
		* args[0] --> HDFS input dir path --> file(s) whose words to be counted
		* args[1] --> HDFS output dir path --> output file -- word & counts.

#### Execute Hadoop MR Code
* Create Jar file of Maven project.
	* Project (right click) --> Run As --> Maven Build --> Goal = package --> Apply & Run.
* terminal> start-dfs.sh
* terminal> start-yarn.sh
* terminal> jps
* terminal> hadoop fs -mkdir -p /user/sunbeam/wordcount/input
* terminal> cat > colors.txt
red green blue
green blue black
red red green
green red green
* terminal> hadoop fs -put colors.txt /user/sunbeam/wordcount/input
* terminal> hadoop jar mrwordcount-0.0.1-SNAPSHOT.jar com.sunbeam.wordcount.WordCountMain /user/sunbeam/wordcount/input /user/sunbeam/wordcount/output
* Syntax:  hadoop jar /path/of/mr_app_jar pkg.MainClass /hdfs/input/dir/path /hdfs/output/dir/path
* terminal> hadoop fs -ls /user/sunbeam/wordcount/output
* terminal> hadoop fs -cat /user/sunbeam/wordcount/output/part-r-00000

### NCDC Temperature Analysis (Avg Temp per year).

#### Mapper and Reducer
* AvgTempMapper
	* Input: `<offset, line>` --> `<LongWritable, Text>`
	* Output: `<year, temperature>` --> `<IntWritable, IntWritable>`
* AvgTempReducer
	* Input: `<year, temperatures>` --> `<IntWritable, IntWritables>`
	* Output: `<year, avg_temperature>` --> `<IntWritable, DoubleWritable>`

#### Upload data
* terminal> hadoop fs -mkdir -p /user/sunbeam/ncdc/input
* Go to directory in which all ncdc files are present (cd dirpath).
* terminal> hadoop fs -put * /user/sunbeam/ncdc/input
* Go to directory in which mr jar is present (cd dirpath).
* terminal> hadoop jar ncdc-0.0.1-SNAPSHOT.jar com.sunbeam.ncdc.NcdcMain /user/sunbeam/ncdc/input /user/sunbeam/ncdc/output1
* terminal> hadoop fs -cat /user/sunbeam/ncdc/output1/part-r-00000

## Execute MR program
* terminal> hadoop jar mr.jar main_class input_path output_path
	* The program will run on cluster configured in HADOOP_HOME.
	* e.g. localhost (yarn-site.xml) -- yarn (mapred-site.xml)
* However a program may need additional config (performance, cluster, etc).
* terminal> hadoop fs -help
* The job configuration can be given using generic options.
* Generic options supported are:
	* -conf `<configuration file>`: specify an application configuration file
	* -D `<property=value>`: define a value for a given property
	* -fs `<file:///|hdfs://namenode:port>`: specify default filesystem URL to use, overrides 'fs.defaultFS' property from configurations.
	* -jt `<local|resourcemanager:port>`: specify a ResourceManager
* Examples:
	* terminal> hadoop jar mr.jar main_class generic_options input_path output_path ...
	* terminal> hadoop jar mrwordcount-0.0.1-SNAPSHOT.jar com.sunbeam.wordcount.WordCountMain -Ddfs.replication=3 /user/sunbeam/wordcount/input /user/sunbeam/wordcount/output
		* MR output file replication factor will be 3.
	* terminal> hadoop jar mrwordcount-0.0.1-SNAPSHOT.jar com.sunbeam.wordcount.WordCountMain -fs hdfs://192.168.56.10:9000 -jt 192.168.56.10:8032 /user/sunbeam/wordcount/input /user/sunbeam/wordcount/output

### Java command line args
* terminal> java pkg.MainClass arg0 arg1 arg2 ...

### GenericOptionsParser class
* While executing Hadoop MR job generic options can be given for performance settings or cluster settings (e.g. -D, -fs, -jt, -conf, etc).
* These generic options should be handled and create hadoop Configuration object should be created automatically. This task is done by GenericOptionsParser.

### Configured class
* A class inherited from Configured class will have a Configuration associated with it.
	* setConf() and getConf()

### Tool interface and ToolRunner class
* A "standard" way of handling generic options and command line args is to implement Tool interface and execute run() method using ToolRunner.
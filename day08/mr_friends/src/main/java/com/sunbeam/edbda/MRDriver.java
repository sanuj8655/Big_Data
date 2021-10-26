package com.sunbeam.edbda;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MRDriver extends Configured implements Tool {
	public static void main(String[] args) {
		try {
			// create Configuration object from generic options
			GenericOptionsParser parser = new GenericOptionsParser(args);
			Configuration conf = parser.getConfiguration();
			// execute Tool.run()
			int ret = ToolRunner.run(conf, new MRDriver(), args);
			System.exit(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// create MR job and submit to cluster
	@Override
	public int run(String[] args) throws Exception {
		// check if valid command line args
		if(args.length != 3) {
			System.err.println("Invalid command line arguments.");
			System.out.println("args[0] -- input (friends)");
			System.out.println("args[1] -- auxilary output (job1 output)");
			System.out.println("args[2] -- final output (job2 output)");
			System.exit(1);
		}
		// get the configuration
		Configuration conf = this.getConf();
		// create MR job
		Job job1 = Job.getInstance(conf, "FriendsRecoJob1");
		// set the jar in which mapper and reducer classes are available
		job1.setJarByClass(MRDriver.class);
		// set mapper class and its output
		job1.setMapperClass(FriendsMapper1.class);
		job1.setMapOutputKeyClass(FriendsPairWritable.class);
		job1.setMapOutputValueClass(IntWritable.class);
		// set reducer class and its output
		job1.setReducerClass(FriendsReducer1.class);
		job1.setOutputKeyClass(FriendsPairWritable.class);
		job1.setOutputValueClass(IntWritable.class);
		// set input & output format
		job1.setInputFormatClass(TextInputFormat.class);
		job1.setOutputFormatClass(TextOutputFormat.class);
		// set input & output path
		FileInputFormat.addInputPaths(job1, args[0]); // hdfs path of dir or file -- path must exists
		FileOutputFormat.setOutputPath(job1, new Path(args[1])); // hdfs path of output dir -- dir will be created at runtime 
		// set combiner & partitioner (if required)
		// submit the job and wait for completion
		job1.submit();
		boolean success = job1.waitForCompletion(true);
		if(!success)
			return 1;
		
		// create MR job
		Job job2 = Job.getInstance(conf, "FriendsRecoJob2");
		// set the jar in which mapper and reducer classes are available
		job2.setJarByClass(MRDriver.class);
		// set mapper class and its output
		job2.setMapperClass(FriendsMapper2.class);
		job2.setMapOutputKeyClass(Text.class);
		job2.setMapOutputValueClass(FriendCountWritable.class);
		// set reducer class and its output
		job2.setReducerClass(FriendsReducer2.class);
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(FriendCountWritable.class);
		// set input & output format
		job2.setInputFormatClass(KeyValueTextInputFormat.class);
		job2.setOutputFormatClass(TextOutputFormat.class);
		// set input & output path
		FileInputFormat.addInputPaths(job2, args[1]); // hdfs path of dir or file -- path must exists
		FileOutputFormat.setOutputPath(job2, new Path(args[2])); // hdfs path of output dir -- dir will be created at runtime 
		// set combiner & partitioner (if required)
		// submit the job and wait for completion
		job2.submit();
		success = job2.waitForCompletion(true);

		int ret = success ? 0 : 1;
		return ret;
	}
}

/*
terminal> hadoop jar /tmp/mr.jar -D max.recommendation=1 /user/sunbeam/friends/input /user/sunbeam/friends/auxout2 /user/sunbeam/friends/output2
*/

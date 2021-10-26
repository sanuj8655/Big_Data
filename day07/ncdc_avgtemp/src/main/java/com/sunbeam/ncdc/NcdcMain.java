package com.sunbeam.ncdc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class NcdcMain extends Configured implements Tool {
	public static void main(String[] args) {
		try {
			GenericOptionsParser parser = new GenericOptionsParser(args);
			Configuration conf = parser.getConfiguration();
			int ret = ToolRunner.run(conf, new NcdcMain(), args);
			System.exit(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = this.getConf();
		
		Job job = Job.getInstance(conf, "NcdcAvgTemp");
		job.setJarByClass(NcdcMain.class);
		
		job.setMapperClass(AvgTempMapper.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setReducerClass(AvgTempReducer.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(DoubleWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
				
		job.setPartitionerClass(HashPartitioner.class); // default
		job.setNumReduceTasks(2); // default = 1
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.submit();
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}
}












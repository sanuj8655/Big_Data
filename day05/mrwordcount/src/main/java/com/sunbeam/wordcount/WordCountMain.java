package com.sunbeam.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCountMain extends Configured implements Tool {
	public static void main(String[] args) {
		try {
			GenericOptionsParser parser = new GenericOptionsParser(args);
			Configuration conf = parser.getConfiguration();
			int ret = ToolRunner.run(conf, new WordCountMain(), args);
			System.exit(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public int run(String[] args) throws Exception {
		if(args.length != 2) {
			System.out.println("Syntax: hadoop jar mr.jar <input-path> <output-path>");
			return 1;
		}
		
		Configuration conf = this.getConf();
		Job job = Job.getInstance(conf, "WordCount");
		job.setJarByClass(WordCountMain.class);
		
		job.setMapperClass(WordCountMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setReducerClass(WordCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
	
		TextInputFormat.addInputPath(job, new Path(args[0]));
		TextOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.submit();
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}
}


package com.sunbeaminfo.dbda.mrhbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HBaseBooksDriver extends Configured implements Tool {
	public static void main(String[] args) {
		try {
			GenericOptionsParser parser = new GenericOptionsParser(args);
			Configuration conf = parser.getConfiguration();
			Configuration hbaseConf = HBaseConfiguration.create(conf);
			HBaseBooksDriver driver = new HBaseBooksDriver();
			int ret = ToolRunner.run(hbaseConf, driver, args);
			System.exit(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		if(args.length != 1) {
			System.out.println("Please specify output paths...");
			System.exit(1);
		}
		Configuration conf = this.getConf();
		
		Job job = Job.getInstance(conf, "BooksJob");
		job.setJarByClass(HBaseBooksDriver.class);

		Scan scan = new Scan();
		scan.addFamily(Util.BOOKS_SUBJECT);
		scan.addFamily(Util.BOOKS_PRICE);
		TableMapReduceUtil.initTableMapperJob(Util.BOOKS, scan, 
				BooksMapper.class, Text.class, DoubleWritable.class, job);
		
		job.setReducerClass(BooksReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);

		job.setOutputFormatClass(TextOutputFormat.class);
		FileOutputFormat.setOutputPath(job, new Path(args[0]));
		
		job.setNumReduceTasks(1);
		job.setPartitionerClass(HashPartitioner.class);

		job.submit();
		boolean success = job.waitForCompletion(true);
		int ret = success ? 0 : 1;
		return ret;
	}
}

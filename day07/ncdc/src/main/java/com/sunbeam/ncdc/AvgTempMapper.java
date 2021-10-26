package com.sunbeam.ncdc;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AvgTempMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
	private int[] validQualities = { 0, 1, 4, 5, 9 };
	private IntWritable yearWr = new IntWritable();
	IntWritable tempWr = new IntWritable();
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, IntWritable, IntWritable>.Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		try {
			int year = Integer.parseInt(line.substring(15, 19));
			int temp = Integer.parseInt(line.substring(87, 92));
			int quality = Integer.parseInt(line.substring(92, 93));
			if(Arrays.binarySearch(validQualities, quality) >= 0 && temp != 9999) {
				yearWr.set(year);
				tempWr.set(temp);
				context.write(yearWr, tempWr);
				context.getCounter(NcdcJobCounter.VALID_RECORDS).increment(1);
			} 
			else {
				// invalid reading
				context.getCounter(NcdcJobCounter.INVALID_RECORDS).increment(1);
				System.out.println("INVALID: " + line);
			}
		} catch (IndexOutOfBoundsException e) {
			// partial reading
			context.getCounter(NcdcJobCounter.PARTIAL_RECORDS).increment(1);
			System.out.println("PARTIAL: " + line);
		} catch (NumberFormatException e) {
			// garbage reading
			context.getCounter(NcdcJobCounter.GARBAGE_RECORDS).increment(1);
			System.out.println("GARBAGE: " + line);
		}
	}
}

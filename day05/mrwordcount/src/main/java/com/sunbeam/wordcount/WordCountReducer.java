package com.sunbeam.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	@Override
	protected void reduce(Text keyWord, Iterable<IntWritable> valueCounts, Reducer<Text,IntWritable,Text,IntWritable>.Context ctx) throws IOException, InterruptedException {
		int sum = 0;
		for(IntWritable valueCnt: valueCounts) {
			int cnt = valueCnt.get();
			sum = sum + cnt;
		}
		IntWritable valueSum = new IntWritable(sum);
		ctx.write(keyWord, valueSum);
	}
}

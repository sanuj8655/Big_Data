package com.sunbeam.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	@Override
	protected void map(LongWritable keyOffset, Text valueLine, Mapper<LongWritable, Text, Text, IntWritable>.Context ctx)
			throws IOException, InterruptedException {
		String line = valueLine.toString();
		String[] words = line.split("[^a-z]");
		for(String word: words) {
			Text keyWord = new Text(word);
			IntWritable valueOne = new IntWritable(1);
			ctx.write(keyWord, valueOne);
		}
	}
}



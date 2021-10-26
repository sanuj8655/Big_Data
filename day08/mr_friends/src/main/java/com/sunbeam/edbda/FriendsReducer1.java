package com.sunbeam.edbda;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class FriendsReducer1 extends Reducer<FriendsPairWritable, IntWritable, FriendsPairWritable, IntWritable> {
	private IntWritable countWr = new IntWritable();
	@Override
	protected void reduce(FriendsPairWritable key, Iterable<IntWritable> values,
			Reducer<FriendsPairWritable, IntWritable, FriendsPairWritable, IntWritable>.Context context)
			throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable oneWr : values)
			sum = sum + oneWr.get();
		countWr.set(sum);
		context.write(key, countWr);
	}
}

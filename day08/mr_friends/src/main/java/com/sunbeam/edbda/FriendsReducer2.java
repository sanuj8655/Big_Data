package com.sunbeam.edbda;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FriendsReducer2 extends Reducer<Text, FriendCountWritable, Text, FriendCountWritable> {
	@Override
	protected void reduce(Text key, Iterable<FriendCountWritable> values,
			Reducer<Text, FriendCountWritable, Text, FriendCountWritable>.Context context)
			throws IOException, InterruptedException {
		int maxRecommendation = context.getConfiguration().getInt("max.recommendation", 2);
		List<FriendCountWritable> valueList = StreamSupport.stream(values.spliterator(), false)
			.sorted((a,b) -> b.getCount() - a.getCount())
			.limit(maxRecommendation)
			.collect(Collectors.toList());
		for (FriendCountWritable value : valueList)
			context.write(key, value);
		
		/*
		StreamSupport.stream(values.spliterator(), false)
		.sorted((a,b) -> b.getCount() - a.getCount())
		.limit(maxRecommendation)
		.forEach(value -> {
			try {
				context.write(key, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		*/
	}
}

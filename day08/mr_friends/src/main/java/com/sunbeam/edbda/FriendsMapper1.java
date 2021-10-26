package com.sunbeam.edbda;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FriendsMapper1 extends Mapper<LongWritable, Text, FriendsPairWritable, IntWritable> {
	private FriendsPairWritable pairWr = new FriendsPairWritable();
	private IntWritable oneWr = new IntWritable(1);
	
	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, FriendsPairWritable, IntWritable>.Context context)
			throws IOException, InterruptedException {
		String line = value.toString();	
		String[] parts = line.split("\\s+", 2);
		//String person = parts[0];
		String friends = parts[1];
		String[] friendsArr = friends.split(",");
		for (int i = 0; i < friendsArr.length; i++) {
			for (int j = i+1; j < friendsArr.length; j++) {
				String f1 = friendsArr[i], f2 = friendsArr[j];
				pairWr.setFriend1(f1);
				pairWr.setFriend2(f2);
				context.write(pairWr, oneWr);
			}
		}
	}
}

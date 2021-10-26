package com.sunbeam.edbda;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FriendsMapper2 extends Mapper<Text, Text, Text, FriendCountWritable> {
	private Text personWr = new Text();
	private FriendCountWritable friendCountWr = new FriendCountWritable();
	
	@Override
	protected void map(Text key, Text value, Mapper<Text, Text, Text, FriendCountWritable>.Context context)
			throws IOException, InterruptedException {
		try {
			int count = Integer.parseInt(value.toString());
			String[] keyParts = key.toString().split(",");
			personWr.set(keyParts[0]);
			friendCountWr.setFriend(keyParts[1]);
			friendCountWr.setCount(count);
			context.write(personWr, friendCountWr);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(key + "\t" + value);
		}
	}
}

package com.sunbeam.edbda;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class FriendCountWritable implements Writable {
	private String friend;
	private int count;
	
	public FriendCountWritable() {
		this.friend = "";
		this.count = 0;
	}

	public FriendCountWritable(String friend, int count) {
		this.friend = friend;
		this.count = count;
	}

	public String getFriend() {
		return friend;
	}

	public void setFriend(String friend) {
		this.friend = friend;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return friend + "," + count;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.friend);
		out.writeInt(this.count);
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		this.friend = in.readUTF();
		this.count = in.readInt();
	}
}

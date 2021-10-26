package com.sunbeam.edbda;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.io.WritableComparable;

public class FriendsPairWritable implements WritableComparable<FriendsPairWritable> {
	private String friend1;
	private String friend2;
		
	public FriendsPairWritable() {
		this.friend1 = "";
		this.friend2 = "";		
	}
	public FriendsPairWritable(String friend1, String friend2) {
		this.friend1 = friend1;
		this.friend2 = friend2;
	}
	
	public String getFriend1() {
		return friend1;
	}
	public void setFriend1(String friend1) {
		this.friend1 = friend1;
	}
	public String getFriend2() {
		return friend2;
	}
	public void setFriend2(String friend2) {
		this.friend2 = friend2;
	}
	
	@Override
	public String toString() {
		return friend1 + "," + friend2;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(friend1, friend2);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FriendsPairWritable))
			return false;
		FriendsPairWritable other = (FriendsPairWritable) obj;
		return Objects.equals(friend1, other.friend1) && Objects.equals(friend2, other.friend2);
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.friend1);
		out.writeUTF(this.friend2);
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		this.friend1 = in.readUTF();
		this.friend2 = in.readUTF();
	}
	
	@Override
	public int compareTo(FriendsPairWritable other) {
		int diff = this.friend1.compareTo(other.friend1);
		if(diff == 0)
			diff = this.friend2.compareTo(other.friend2);
		return diff;
	}
}





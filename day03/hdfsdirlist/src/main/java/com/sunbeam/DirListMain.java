package com.sunbeam;

import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

public class DirListMain {
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		//conf.set("fs.defaultFS", "hdfs://localhost:9000");
		conf.set("fs.defaultFS", "hdfs://192.168.56.10:9000");
		
		try(DistributedFileSystem dfs = (DistributedFileSystem) FileSystem.get(conf)) {
			String dirPath = "/user/sunbeam";
			Path path = new Path(dirPath);
			FileStatus[] list = dfs.listStatus(path);
			for(FileStatus file : list) {
				System.out.println("Name: " + file.getPath().getName());
				System.out.println("Size: " + file.getLen());
				System.out.println("Block size: " + file.getBlockSize());
				System.out.println("Time: " + new Date(file.getAccessTime()));
				System.out.println("Replication: " + file.getReplication());
				System.out.println("Owner: " + file.getOwner() + " - " + file.getGroup());
				System.out.println("Permissions: " + file.getPermission().toOctal());
				System.out.println();
			}
		} // dfs.close
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "file:///");
		
		try(LocalFileSystem dfs = (LocalFileSystem) FileSystem.get(conf)) {
			String dirPath = "/";
			Path path = new Path(dirPath);
			FileStatus[] list = dfs.listStatus(path);
			for(FileStatus file : list) {
				System.out.println("Name: " + file.getPath().getName());
				System.out.println("Permission: " + file.getLen());
				System.out.println();
			}
		} // dfs.close
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
}

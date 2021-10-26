package com.sunbeam;

import java.util.Scanner;
import java.util.stream.Stream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

public class FileReadMain {
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://192.168.56.10:9000/");
		
		try(DistributedFileSystem dfs = (DistributedFileSystem) FileSystem.get(conf)) {
			String filePath = "/user/sunbeam/welcome.txt";
			Path path = new Path(filePath);
			try(FSDataInputStream in = dfs.open(path)) {
				try(Scanner sc = new Scanner(in)) {
					while(sc.hasNextLine()) {
						String line = sc.nextLine();
						System.out.println(line);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}







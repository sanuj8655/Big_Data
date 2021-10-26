package com.sunbeam;

import java.io.PrintStream;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

public class FileWriteMain {
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://192.168.56.10:9000/");
		conf.set("dfs.replication", "3");
		
		String[] data = {
			"Hello DBDA,",
			"We are learning HDFS Java API.",
			"They are used to read/write files on HDFS"
		};
		
		try(DistributedFileSystem dfs = (DistributedFileSystem) FileSystem.get(conf)) {
			String filePath = "/user/admin/hi.txt";
			Path path = new Path(filePath);
			try(FSDataOutputStream out = dfs.create(path)) {
				try(PrintStream prn = new PrintStream(out)) {
					for(String line:data)
						prn.println(line);
				}
				//out.flush();
				//out.hflush();
				//out.hsync();
			}
			System.out.println("File created.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


// hadoop fs -ls /user/sunbeam
// hadoop fs -cat /user/sunbeam/hello.txt




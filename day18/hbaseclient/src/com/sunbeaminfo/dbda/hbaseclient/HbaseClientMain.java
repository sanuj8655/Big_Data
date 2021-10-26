package com.sunbeaminfo.dbda.hbaseclient;

import java.util.concurrent.CompletableFuture;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.AsyncConnection;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseClientMain {
	public static void main(String[] args) {
		//listTables();
		//getBook();
		//scanBooks();
		putBook();	
	}

	private static void putBook() {
		//TODO: Homework
		//table.put(put);
	}

	private static void scanBooks() {
		final byte[] NAME = Bytes.toBytes("name");
		final byte[] AUTHOR = Bytes.toBytes("author");
		final byte[] SUBJECT = Bytes.toBytes("subject");
		final byte[] PRICE = Bytes.toBytes("price");
		
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "localhost");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		try(Connection con = ConnectionFactory.createConnection(conf)) {
			TableName tblName = TableName.valueOf("books");
			Table table = con.getTable(tblName);
	
			Scan scan = new Scan();
			//scan.setStartRow(Bytes.toBytes("2001"));
			//scan.setStopRow(Bytes.toBytes("3003"));
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result result : resultScanner) {
				String id = Bytes.toString(result.getRow());

				Cell cell = result.getColumnLatestCell(NAME, NAME);
				String name = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());

				cell = result.getColumnLatestCell(AUTHOR, AUTHOR);
				String author = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
				
				cell = result.getColumnLatestCell(SUBJECT, SUBJECT);
				String subject = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
				
				cell = result.getColumnLatestCell(PRICE, PRICE);
				String price = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());

				System.out.printf("%s, %s, %s, %s, %s\n", id, name, author, subject, price);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private static void getBook() {
		final byte[] NAME = Bytes.toBytes("name");
		final byte[] AUTHOR = Bytes.toBytes("author");
		final byte[] SUBJECT = Bytes.toBytes("subject");
		final byte[] PRICE = Bytes.toBytes("price");
		
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "localhost");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		try(Connection con = ConnectionFactory.createConnection(conf)) {
			TableName tblName = TableName.valueOf("books");
			Table table = con.getTable(tblName);
			String rowId = "1001";
			Get get = new Get( Bytes.toBytes(rowId) );
			Result result = table.get(get);
			
			String id = Bytes.toString(result.getRow());
			System.out.println(id);

			Cell cell = result.getColumnLatestCell(NAME, NAME);
			String name = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
			System.out.println(name);

			cell = result.getColumnLatestCell(AUTHOR, AUTHOR);
			String author = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
			System.out.println(author);
			
			cell = result.getColumnLatestCell(SUBJECT, SUBJECT);
			String subject = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
			System.out.println(subject);
			
			cell = result.getColumnLatestCell(PRICE, PRICE);
			String price = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
			System.out.println(price);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void listTables() {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "localhost");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		try(Connection con = ConnectionFactory.createConnection(conf)) {
			Admin admin = con.getAdmin();
			TableName[] tableNames = admin.listTableNames();
			for (TableName tableName : tableNames) {
				String name = tableName.getNameAsString();
				System.out.println(name);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}

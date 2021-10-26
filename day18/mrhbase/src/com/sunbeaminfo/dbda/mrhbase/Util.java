package com.sunbeaminfo.dbda.mrhbase;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class Util {
	public static final byte[] BOOKS = Bytes.toBytes("books");
	public static final byte[] BOOKS_SUBJECT = Bytes.toBytes("subject");
	public static final byte[] BOOKS_PRICE = Bytes.toBytes("price");
	public static String getSubject(Result result) {
		byte[] data = result.getValue(BOOKS_SUBJECT, BOOKS_SUBJECT);
		if(data == null)
			return null;
		return Bytes.toString(data);
	}
	public static Double getPrice(Result result) {
		byte[] data = result.getValue(BOOKS_PRICE, BOOKS_PRICE);
		if(data == null)
			return null;
		String strData = Bytes.toString(data);
		return Double.parseDouble(strData);
	}
}

package com.sunbeam.edbda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

//* step 1: Create Java project and Hive JDBC driver.

public class HiveJdbcMain {
	public static final String DB_DRIVER = "org.apache.hive.jdbc.HiveDriver";
	public static final String DB_URL = "jdbc:hive2://localhost:10000/edbda";
	public static final String DB_USER = "sunbeam";
	public static final String DB_PASSWORD = "";
	
	static {
		try {
			//	* step 2: Load and register hive driver.
			Class.forName(DB_DRIVER);
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public static void main(String[] args) {
		//	* step 3: Create connection (url, username, password).
		try(Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			//	* step 4: Create PreparedStatement.
			String hql = "SELECT id, name, author, subject, price FROM books";
			try(PreparedStatement stmt = con.prepareStatement(hql)) {
				// * step 5: Set query parameters and execute the query.
				try(ResultSet rs = stmt.executeQuery()) {
					// * step 6: For select queries process the ResultSet.
					while(rs.next()) {
						int id = rs.getInt("id");
						String name = rs.getString("name");
						String author = rs.getString("author");
						String subject = rs.getString("subject");
						double price = rs.getDouble("price");
						System.out.println(id + ", " + name + ", " + author + ", " + subject + ", " + price);
					}
				} // * step 8: close resultset.
			} // * step 7: close statement.
		}//	* step 7: close connection.
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.sunbeaminfo.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

object RddAdvanced1 {
	def createSparkContext(appName:String) = {
		Logger.getLogger("org").setLevel(Level.ERROR)

		val conf = new SparkConf()
		if(conf.get("spark.master", "").isEmpty)
			conf.set("spark.master", "local[*]")
		if(conf.get("spark.driver.bindAddress", "").isEmpty)
			conf.set("spark.driver.bindAddress", "127.0.0.1")
		conf.setAppName(appName)
		new SparkContext(conf)
	}
	def parseLineSubjectPrice(line:String) = {
		val parts = line.split(",")
		if(parts.length == 5)
			Some(parts(3), parts(4).toDouble)
		else
			None
	}
	
	def main(args: Array[String]): Unit = {
		val sc = createSparkContext("rdd1")
		
		val books = sc.textFile("/home/nilesh/spark/SP01/books.csv")
		
		/*
		def parseLineSubject(line:String) = {
			val parts = line.split(",")
			if(parts.length == 5)
				Some(parts(3))
			else
				None
		}
		val booksSubjects = books
			.flatMap(parseLineSubject)
	    	.keyBy(subject => subject)
	    	.countByKey()
		booksSubjects.foreach(println)
		println()
		*/
		
		/*
		val booksPriceWithTax = books
			.flatMap(line => parseLineSubjectPrice(line))
	    	.mapValues(price => price + price * 0.05)
		booksPriceWithTax.foreach(println)
		println()
		*/
		
		/*
		val booksSubjectPrices = books
			.flatMap(parseLineSubjectPrice)
			.groupByKey()
	    	//.map(spl => (spl._1, spl._2.max))
	    	.mapValues(pl => pl.max)
		booksSubjectPrices.foreach(println)
		println()
		*/
		
		/*
		val maxSubjectTotal = books
			.flatMap(parseLineSubjectPrice)
	    	.aggregateByKey(0.0)(
			    (p1,p2) => p1 + p2, // combiner -- within partition
			    (t1,t2) => t1 + t2  // reducer -- across partition
		    )
		maxSubjectTotal.foreach(println)
		*/
		/*
		case class Book(id:Int, name:String, author:String, subject:String, price:Double)
		
		def parseLine(line:String) = {
			val parts = line.split(",")
			if(parts.length == 5)
				Some(Book(parts(0).toInt, parts(1), parts(2), parts(3), parts(4).toDouble))
			else
				None
		}
		val booksAll = books.flatMap(parseLine)
		val subjectAuthors = booksAll.map(b => (b.subject, b.author))
		val subjectNames = booksAll.map(b => (b.subject, b.name))
		val subjectPrices = booksAll.map(b => (b.subject, b.price))
		
		val result = subjectAuthors.cogroup(subjectNames, subjectPrices)
		result.foreach(println)
		*/
		
		val nums = sc.parallelize(1 to 10)
		val sqrs = sc.parallelize((1 to 10).map(n => n * n))
		val numSqrs = nums.zip(sqrs)
		numSqrs.foreach(println)
		
		sc.stop()
	}
}




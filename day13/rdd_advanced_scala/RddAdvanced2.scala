package com.sunbeaminfo.spark

import com.sunbeaminfo.spark.RddAdvanced1.createSparkContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}


object RddAdvanced2 {
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
	
	def parseMovies(line:String) = {
		try {
			val parts = line.split("""\^""")
			Some(parts(0).toInt, parts(1))
		} catch {
			case e:Throwable => None
		}
	}
	
	def parseRatings(line:String) = {
		try {
			val parts = line.split(",")
			Some(parts(0).toInt, parts(1).toInt, parts(2).toDouble)
		} catch {
			case e:Throwable => None
		}
	}
	
	def main(args: Array[String]): Unit = {
		val sc = createSparkContext("rdd2")

		val moviesFile = sc.textFile("/home/nilesh/spark/SP01/movies.csv")
		val movies = moviesFile.flatMap(parseMovies)
	    //movies.foreach(println)
		
		val ratingsFile = sc.textFile("/home/nilesh/spark/SP01/ratings.csv")
		val ratings = ratingsFile.flatMap(parseRatings)
		//ratings.foreach(println)
		
		val topRatings = ratings
	    	.map(umr => (umr._2, 1))
	    	.reduceByKey(_ + _)
	    	.filter(_._2 > 300)
	    	.sortBy(_._2, ascending = false, numPartitions = 1)
		//topRatings.foreach(println)
		
		val topMovies = topRatings
	    	                .join(movies)
							.map(rm => (rm._1, rm._2._1, rm._2._2))
		//topMovies.foreach(println)
		println()
		
		/*
		val highestRating = ratings
			.map(umr => (umr._2, 1))
			.reduceByKey(_ + _)
			.sortBy(_._2, false, 1)
			.first()
		val highestMovie = movies.lookup(highestRating._1).head
		println(s"${highestRating._1}, $highestMovie, ${highestRating._2}")
		*/
		
		sc.stop()
	}
}

package com.sunbeaminfo.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source


object RddAdvanced3 {
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
	
	def loadMovies():Map[Int,String] = {
		val moviesFile = Source.fromFile("/home/nilesh/spark/SP01/movies.csv").getLines()
		val movies = moviesFile
	    	.flatMap(parseMovies)
	    	.toMap
		movies
	}
	
	def main(args: Array[String]): Unit = {
		val sc = createSparkContext("rdd3")

		val movies = loadMovies()
	    val moviesBr = sc.broadcast(movies)
		
		val ratingsFile = sc.textFile("/home/nilesh/spark/SP01/ratings.csv")
		val ratings = ratingsFile.flatMap(parseRatings)
		//ratings.foreach(println)
		
		val topRatings = ratings
	    	.map(umr => (umr._2, 1))
	    	.reduceByKey(_ + _)
	    	.filter(_._2 > 300)
	    	.sortBy(_._2, false, 1)
		//topRatings.foreach(println)
		
		val topMovies = topRatings
	    	.map(mc => (mc._1, moviesBr.value.getOrElse(mc._1, "-"), mc._2))
		topMovies.foreach(println)
		println()
		
		sc.stop()
	}
}

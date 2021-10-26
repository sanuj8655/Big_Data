package com.sunbeaminfo.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer
import scala.io.StdIn._
import scala.math._

object RddAdvanced4 {
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
	
	case class Movie(id:Int, title:String)
	case class Rating(userId:Int, movieId:Int, rating:Double)
	case class MoviePair(m1:Int, m2:Int)
	case class RatingPair(r1:Double, r2:Double)
	case class Corr(m1:Int, m2:Int, cnt:Int, corr:Double)
	
	def parseMovies(line:String) = {
		try {
			val parts = line.split("""\^""")
			Some(Movie(parts(0).toInt, parts(1)))
		} catch {
			case e:Throwable => None
		}
	}
	
	def parseRatings(line:String) = {
		try {
			val parts = line.split(",")
			Some(Rating(parts(0).toInt, parts(1).toInt, parts(2).toDouble))
		} catch {
			case e:Throwable => None
		}
	}
	
	def combnRatings(userIdRatingValues:(Int,Iterable[Rating])) = {
		val values = userIdRatingValues._2
		val list:ListBuffer[(MoviePair,RatingPair)] = ListBuffer.empty
		for(rat1 <- values) {
			for(rat2 <- values) {
				if(rat1.movieId < rat2.movieId)
					list.append(MoviePair(rat1.movieId, rat2.movieId) -> RatingPair(rat1.rating, rat2.rating))
			}
		}
		list
	}
	
	def corrRatings(moviePairRatingPairValues: (MoviePair, Iterable[RatingPair])) = {
		val movies = moviePairRatingPairValues._1
		val values:Iterable[RatingPair] = moviePairRatingPairValues._2
		var n = 0
		var sumxy = 0.0
		var sumx = 0.0
		var sumy = 0.0
		var sumxx = 0.0
		var sumyy = 0.0
		for(rp <- values) {
			sumx += rp.r1
			sumy += rp.r2
			sumxx += rp.r1 * rp.r1
			sumyy += rp.r2 * rp.r2
			sumxy += rp.r1 * rp.r2
			n += 1
		}
		
		var result:Option[Corr] = None
		if(n > 1) {
			val corr = (n * sumxy - sumx * sumy) / sqrt((n * sumxx - sumx * sumx) * (n * sumyy - sumy * sumy))
			if(!corr.isNaN)
				result = Some(Corr(movies.m1, movies.m2, n, corr))
		}
		result
	}
	
	def main(args: Array[String]): Unit = {
		val sc = createSparkContext("rdd3")
		
		val movies =
			sc.textFile("/home/nilesh/spark/SP01/movies.csv")
			.flatMap(parseMovies)
	    	.keyBy(_.id)
	    	.cache()
		
		val ratings =
			sc.textFile("/home/nilesh/spark/SP01/ratings.csv")
	    	.flatMap(parseRatings)
		
		val corr = ratings
	    	.keyBy(r => r.userId)
	    	.groupByKey()
	    	.flatMap(combnRatings)
	    	.groupByKey()
	    	.flatMap(corrRatings)
	    	.cache()
		//corr.foreach(println)
		var m = -1
		do {
			print("Enter a movie id : ")
			m = readInt()
			val mb = sc.broadcast(m)
			
			def filterSimilarMovies(c:Corr) = {
				val m = mb.value
				if((c.m1 == m || c.m2 == m) && c.cnt > 30 && c.corr > 0.5) {
					val sm = if (m == c.m1) c.m2 else c.m1
					Some(sm -> (c.corr, c.cnt))
				} else
					None
			}
			
			val similar = corr
		    	.flatMap(filterSimilarMovies)
			
			val result = similar.join(movies)
		    	.map(v => (v._1, v._2._2.title, v._2._1._1, v._2._1._2))
		    	.sortBy(m => m._3, ascending = false, 1)
				.top(5)
				
			result.foreach(println)
		} while (m > 0)
		sc.stop()
	}
}

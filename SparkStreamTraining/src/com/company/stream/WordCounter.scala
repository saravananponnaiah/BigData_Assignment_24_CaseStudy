package com.company.stream

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.api.java._
import org.apache.spark.api.java.function._

object WordCounter {
  def main(args : Array[String]) : Unit = {
      val conf = new SparkConf().setAppName("Spark Word Counter")      
      val ssc = new StreamingContext(conf, Seconds(10))
      val sc = ssc.sparkContext
      var dataPollingPath = "/home/acadgild/workspace/spark/data"
      sc.setLogLevel("ERROR")
     
      if (args != null) {
        dataPollingPath = args(0)
      }
      // HDFS Path : hdfs:///home/acadgild/workspace/spark/data
      // Local Path : /home/acadgild/workspace/spark/data
      println("Polling folder path : " + dataPollingPath)
      val lines = ssc.textFileStream(dataPollingPath)
      //val lines = ssc.textFileStream("E:\\Hadoop\\StreamData")
      println("Spark application started...")
      lines.print()
      val words = lines.flatMap(line => line.split(","))
      val pairs = words.map(word => (word, 1))
      pairs.print()
      val wordCount = pairs.reduceByKey(_ + _)
      wordCount.print()
      
      ssc.start()
      ssc.awaitTermination()
  }
}
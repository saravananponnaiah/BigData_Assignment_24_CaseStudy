package com.company.stream

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.api.java._
import org.apache.spark.api.java.function._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem,Path}
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.conf.Configured

object Assignment24_CaseStudy_Part2 {
  def main(args : Array[String]) : Unit = {
      val conf = new SparkConf().setAppName("Spark Word Counter")      
      val ssc = new StreamingContext(conf, Seconds(2))
      val sc = ssc.sparkContext
      var localPollingPath = "file:///home/acadgild/workspace/spark/data"
      var hdfsPollingPath = "hdfs:///home/spark/data"
      sc.setLogLevel("ERROR")
      println("Spark application started...")
      
      if (args != null) {
        localPollingPath = args(0)    // Set the data polling local folder path
        hdfsPollingPath = args(1)    // Set the data polling hdfs folder path
      }

      // Local Path : file:///home/acadgild/workspace/spark/data
      println("Local Polling folder path : " + localPollingPath)
      val lines = ssc.textFileStream(localPollingPath)
      
      // Copy file from local folder path to HDFS folder path
      val sourcePath = new Path(localPollingPath)
      val destPath = new Path(hdfsPollingPath) 
      val hadoopConf = sc.hadoopConfiguration 
      val fs = FileSystem.get(hadoopConf)
      fs.copyFromLocalFile(false, true, sourcePath, destPath)    
            
      // Process data from local folder 
      val words = lines.flatMap(line => line.split(","))
      val pairs = words.map(word => (word, 1))
      val wordCount = pairs.reduceByKey(_ + _)
      wordCount.print() 
      
      val hdfsLines = ssc.textFileStream(hdfsPollingPath)
      val hdfsWords = hdfsLines.flatMap(line => line.split(","))
      val hdfsPairs = hdfsWords.map(word => (word, 1))
      val hdfsWordCount = hdfsPairs.reduceByKey(_ + _)
      hdfsWordCount.print()
      
      ssc.start()
      ssc.awaitTermination()
  }
}
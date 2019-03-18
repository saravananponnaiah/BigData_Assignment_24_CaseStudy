package com.company.stream

import org.apache.spark.{SparkConf,SparkContext}
import org.apache.spark.streaming.{Seconds,StreamingContext}
import scala.collection
import scala.util
import java.lang.String

object Assignment24_task2 {
  def main(args : Array[String]) : Unit = {
        
    // Create Spark Configuration and spark streaming context
    val conf = new SparkConf().setMaster("local[4]").setAppName("StreamTest")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc,Seconds(20))
    
    val offensiveKeywords = Set("idiot","fool","stupid","mad") 
    val userLines = ssc.socketTextStream("localhost", 9999)    
    println(offensiveKeywords)
    // Print user input lines
    userLines.map(x => println(x))
    
    val splitedLines = userLines.flatMap(line => line.split(" "))
    val words = splitedLines.map(word => word)
    val filterOffensiveWords = words.filter(x => offensiveKeywords.contains(x.toLowerCase())).map(x => (x, 1))
    val offensiveWordCount = filterOffensiveWords.reduceByKey(_ + _) 
    
    // Print Offensive Word Count
    print("Offensive Word Count : ")
    offensiveWordCount.print()
    
    ssc.start()
    ssc.awaitTermination()    
  }
}
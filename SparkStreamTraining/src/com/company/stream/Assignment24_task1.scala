package com.company.stream

import org.apache.spark.{SparkConf,SparkContext}
import org.apache.spark.streaming.{Seconds,StreamingContext}
import scala.collection
import scala.util
import java.lang.String

object Assignment24_task1 {
  def main(args : Array[String]) : Unit = {
   
    // Method to extract numbers and sum the values
    def getLinesNumericSum(input : String) : Int = {      
      val line = input.split("")
      var number : Int = 0
      for (x <- line) {
        try {
          val value = x.toInt
          number = number + value
        }
        catch {
          case ex : Exception => {}
        }
      }
      return number      
    }
    
    val conf = new SparkConf().setMaster("local[4]").setAppName("Word Counter")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc,Seconds(20))
    
    val lines = ssc.socketTextStream("localhost", 9999)
    val filtered_lines = lines.filter(x => getLinesNumericSum(x)%2 == 0)
    val summed_lines = filtered_lines.map(x => getLinesNumericSum(x))
    filtered_lines.print()
    summed_lines.reduce(_+_).print()
    // Start Spark Streaming Context
    ssc.start()
    ssc.awaitTermination()
  }
}
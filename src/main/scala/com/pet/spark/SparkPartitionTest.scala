package com.pet.spark

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object SparkPartitionTest extends App {

  import sparkSession.implicits._

  def printDataInPartition(df: DataFrame) = {
    println(df.rdd.getNumPartitions)
    df.rdd
      .mapPartitionsWithIndex((i, p) => p.map((i, _)))
      .foreachPartition(p => println(p.mkString("\n")))
  }

  val df = sparkSession.read.option("delimiter", ":").csv("./test1.txt").toDF("date", "value")
  val first = df.repartition(10, 'date).cache()
  first.count()

  printDataInPartition(first)
  first.unpersist()
  val sec = first
    .withColumn("new_data", sum('value).over(Window.partitionBy("date")))
  sec.count()
  printDataInPartition(sec)

  import org.apache.spark.sql.execution.debug._

  sec.debugCodegen()
  //  println(sec.rdd.dependencies.head.rdd.dependencies)
}

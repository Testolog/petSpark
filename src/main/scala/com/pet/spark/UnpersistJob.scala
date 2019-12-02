package com.pet.spark

import java.io.{PrintStream, PrintWriter}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.col

import scala.util.{Failure, Success, Try}

/**
  * com.pet.spark
  *
  * @author Robert Nad
  */
object UnpersistJob extends App {
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val sparkSession = SparkSession
    .builder()
    .config(new SparkConf()
      .setAppName("pet spark project")
      .setMaster("local[*]"))
    .enableHiveSupport
    .getOrCreate()

  def cascadeUnpersit(rdd: RDD[_]): Unit = {
    rdd.unpersist(true)
    rdd.dependencies.foreach(dep => cascadeUnpersit(dep.rdd))
  }

  Range(0, 3) foreach (_ => {
    val realDS = sparkSession.range(10000)
      .toDF()
      .withColumn("id8", col("id") % 8)
      .unpersist(true)
      .persist()
      .filter(col("id8") === 0)
//    assert(sparkSession.sparkContext.getPersistentRDDs.nonEmpty)
    realDS.count()
    println(sparkSession.sparkContext.getPersistentRDDs)
    println("____________________")

    cascadeUnpersit(realDS.rdd)

//    assert(sparkSession.sparkContext.getPersistentRDDs.isEmpty)
    realDS.count()
    println(sparkSession.sparkContext.getPersistentRDDs)
    println("____________________")
  })

}

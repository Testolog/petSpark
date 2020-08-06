package com.pet

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
package object spark {
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  val sparkSession: SparkSession = SparkSession
    .builder()
    .config(new SparkConf()
      .setAppName("pet spark project")
      .setMaster("local[*]"))
    .enableHiveSupport
    .getOrCreate()


}

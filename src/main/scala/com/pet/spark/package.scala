package com.pet

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
package object spark {
//  Logger.getLogger("org").setLevel(Level.ERROR)
//  Logger.getLogger("akka").setLevel(Level.ERROR)
val sparkSession: SparkSession = SparkSession
  .builder()
  .config(new SparkConf()
    .setAppName("pet spark project")
    .setMaster("local[*]"))
  .enableHiveSupport
  .getOrCreate()


}

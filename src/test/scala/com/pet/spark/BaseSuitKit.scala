package com.pet.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

/**
  * com.pet.spark
  *
  * @author Robert Nad
  */
trait BaseSuitKit  extends FlatSpec with BeforeAndAfterAll with Matchers {
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  var sparkSession:SparkSession = _

  override def beforeAll(): Unit = {
    sparkSession = SparkSession
      .builder()
      .config(new SparkConf()
        .setAppName("pet spark project")
        .setMaster("local[*]"))
      .enableHiveSupport
      .getOrCreate()
  }

  override def afterAll(): Unit = {
    sparkSession.stop()
  }

}

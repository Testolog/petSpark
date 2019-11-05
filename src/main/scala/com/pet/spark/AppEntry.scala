package com.pet.spark

import org.apache.spark.{SparkConf, SparkContext}

/**
  * com.pet.spark
  *
  * @author Robert Nad
  */
object AppEntry extends App {

  val sparkConf = new SparkConf()
  sparkConf.setMaster("local[*]")
  sparkConf.setAppName("pet test")

  val sparkContext=SparkContext.getOrCreate(sparkConf)


}

package com.pet.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * com.pet.spark
  *
  * @author Robert Nad
  */
object PersistsLeveling extends App {

  //do not jump between leveling api, like DataFrame to rdd, and using persist.
  //Persist for Dataframe are upper level than RDD, and if u use rdd persists and
  // unpersists at the df level, clear never happened
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  val sparkSession = SparkSession
    .builder()
    .config(new SparkConf()
      .setAppName("pet spark project")
      .setMaster("local[*]"))
    .enableHiveSupport
    .getOrCreate()

  import sparkSession.implicits._

  val cachePlane = Range(1, 4).map(p => {
    val df = sparkSession.range(0, p)
    df.rdd
    sparkSession.createDataset(df.rdd.persist()).persist()
  })
  cachePlane.foreach(_.count())

  println("pesists are exists we could check in cacheManager")
  assert(!sparkSession.sharedState.cacheManager.isEmpty)
  println("also check what are realpersisted")
  println(sparkSession.sparkContext.getPersistentRDDs.size)


  //  val before = sparkSession.sharedState.cacheManager.lookupCachedData(cachePlane.head)
  //just for make sure all is okay we should to have 3 of rdd levelv and 3 for dataframe level
  assert(sparkSession.sparkContext.getPersistentRDDs.size == 6)

  println("unpersits DataFrame level, and check what is left")
  cachePlane.foreach(_.unpersist())
  assert(sparkSession.sparkContext.getPersistentRDDs.size == 3)
  println(sparkSession.sparkContext.getPersistentRDDs.size)

  println("now we unperstis of RDDs level, after dataframe and see what we get")
  sparkSession.sparkContext.getPersistentRDDs.foreach(_._2.unpersist(true))
  println(sparkSession.sparkContext.getPersistentRDDs.size)

  // if u have any jump between of leveling and persits on each, use of clearCache() before huge calculation
  //sparkSession.sharedState.cacheManager.clearCache()

  assert(sparkSession.sharedState.cacheManager.isEmpty)
  assert(sparkSession.sparkContext.getPersistentRDDs.isEmpty)

}

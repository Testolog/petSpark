package com.pet.spark

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object PersistsStorageUsing extends App {

  import sparkSession.implicits._

  sparkSession.conf.set("spark.sql.autoBroadcastJoinThreshold", -1)
  val base = Seq(
    (1, "name1"),
    (2, "name2"),
    (3, "name3"),
  ).toDF("id", "name")
  val semi = Seq(
    (1, 11),
    (2, 12),
    (3, 13),
  ).toDF("baseid", "semiid")
    .persist()
  val problems = Seq(
    (11, "error1"),
    (12, "error2"),
    (13, "error3"),
  ).toDF("id", "name")

  val res = base
    .join(semi, base("id") === semi("baseid"))
    .join(problems, semi("semiid") === problems("id"))

  //  semi.count()
  res.count()
  res.explain()
  println("first check of persist")
  //there is exists persist when do action
  sparkSession.sharedState.cacheManager.lookupCachedData(semi).foreach(println(_))
  sparkSession.sparkContext.getPersistentRDDs.foreach(println(_))
  assert(sparkSession.sparkContext.getPersistentRDDs.size == 1)
  //drop persists
  semi
    .unpersist()
    .count()

  println("after a part of persist")
  //persits data not exists anymore
  assert(sparkSession.sparkContext.getPersistentRDDs.isEmpty)
  sparkSession.sharedState.cacheManager
  //try to re persist by do action of upper level dataframe
  res.count()
  res.explain()
  println("after try to re persists from upper logical plan")
  sparkSession.sharedState.cacheManager.lookupCachedData(semi).foreach(println(_))
  assert(sparkSession.sparkContext.getPersistentRDDs.isEmpty)
  println("cache manager work on unique ids based on logical plan")
  res.explain()
  //persist again
  semi.persist().count()
  assert(sparkSession.sparkContext.getPersistentRDDs.size == 1)
  res.explain()
}

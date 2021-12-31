package com.pet.spark

import scala.util.Try

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object HowSelectWork extends App {

  import sparkSession.implicits._

  sparkSession.conf.set("spark.sql.autoBroadcastJoinThreshold", -1)
  sparkSession.conf.set("spark.sql.crossJoin.enable", value = false)
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
  val problems = Seq(
    (11, "error1"),
    (12, "error2"),
    (13, "error3"),
  ).toDF("id", "name")

  //  == Physical Plan ==
  //    LocalTableScan [id#7, name#8]
  base.select(base("*")).explain()
  //  == Physical Plan ==
  //    LocalTableScan [baseid#18, semiid#19]
  semi.select(semi("*")).explain()
  //  == Physical Plan ==
  //    LocalTableScan [id#29, name#30]
  problems.select(problems("*")).explain()
  //option 1
  //all column in dataframe have not only name also unique id
  Try(problems.select(base("*"))).failed.foreach(p => println(p.getMessage))
  //option 2
  //same aas first one
  Try(problems.select(base("id"))).failed.foreach(p => println(p.getMessage))
  //alias always change of unique id for column after naming
  //LocalTableScan [baseid#39]
  semi.select(semi("baseid").alias("baseid")).explain()
  val resolve = Seq(
    (11, "resolve"),
    (12, "resolve"),
    (13, "resolve"),
  ).toDF("id", "name")
  val semi2 = semi.select(semi("semiid").alias("semiid"), semi("baseid").alias("baseid"))
  //probably join work fine, but find case when after join we have get a reassigned ids for dataframe
  //
  base
    .join(semi, base("id") === semi("baseid"))
    .join(problems, semi("semiid") === problems("id"))
    .join(resolve, semi("semiid") === resolve("id"))
    .join(semi2, semi2("semiid") === resolve("id"))
    .show()


}

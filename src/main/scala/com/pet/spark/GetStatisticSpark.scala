package com.pet.spark

import java.sql.Date
import java.text.SimpleDateFormat

import org.apache.commons.lang.RandomStringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SQLContext, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, dense_rank, ntile, rank, sum, when, year}
import org.apache.spark.sql.hive.HiveContext
import org.joda.time.DateTime

import scala.annotation.tailrec

/**
  * com.pet.spark
  *
  * @author Robert Nad
  */
object GetStatisticSpark extends App {

  import org.apache.log4j.Logger
  import org.apache.log4j.Level

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  val session = SparkSession
    .builder()
    .config(new SparkConf()
      .setAppName("test fetch kafka trigger ")
      .setMaster("local[*]"))
    .enableHiveSupport
    .getOrCreate()
  val rawData = session.sparkContext.makeRDD(Seq.range(0, 10000)).map(r => Row(r))

  val cashData = rawData.cache()
  //  val df = sql.createDataFrame(rawData, StructType(Seq(StructField("a", IntegerType, nullable = true))))
  //    .withColumn("a15", col("a") % 15 === 0)
  //    .withColumn("a5", col("a") % 5 === 0)
  //    .withColumn("a3", col("a") % 3 === 0)
  //    .withColumn("r1", rand())
  //    .cache()
  //  val d15 = df.filter(col("a15") === true)
  //  val d5 = df.filter(col("a5") === true)
  //  val d3 = df.filter(col("a3") === true)
  //  val merge = d15
  //    .join(d5
  //      .select(col("a"), col("r1").as("d5r1")).
  //      join(d3
  //        .select(col("a"), col("r1").as("d3r1")), "a"), "a")
  //    .select(col("a"), col("d3r1"), col("d5r1"), col("r1").as("d15r1"))
  //  merge.explain()
  //  merge.show()
  val date = new SimpleDateFormat("yyyy/MM/dd")
  val startDate = new DateTime(date.parse("2016/01/01"))
//
//  //  date.parse("2016/01/01")
//  case class DataStat(value: Int, name: String, date: Date)
//
//  val dataStatByDay = Range(0, 365 * 3).map(r => {
//    DataStat(1, "name1", new Date(startDate.plusDays(r).getMillis))
//  })
//
//  val df = sql.createDataFrame(dataStatByDay)
//  df.select(
//    col("name"),
//    col("value"),
//    col("date"),
//    dense_rank().over(Window.orderBy(col("date").desc)).as("rankNumb")
//  ).filter(col("rankNumb") <= 365)
//    .select(col("*"),
//      ntile(365/7).over(Window.partitionBy(col("name"), col("rankYear")).orderBy(col("date").desc)).as("rank7"),
//      ntile(365/30).over(Window.partitionBy(col("name"), col("rankYear")).orderBy(col("date").desc)).as("rank30"),
//      ntile(365/60).over(Window.partitionBy(col("name"), col("rankYear")).orderBy(col("date").desc)).as("rank60"),
//      ntile(365/180).over(Window.partitionBy(col("name"), col("rankYear")).orderBy(col("date").desc)).as("rank180")
//    )
//    .select(
//      col("*"),
//      sum("value").over(Window.partitionBy(col("rank7"))).as("sum7"),
//      sum("value").over(Window.partitionBy(col("rank30"))).as("sum30"),
//      sum("value").over(Window.partitionBy(col("rank60"))).as("sum60"),
//      sum("value").over(Window.partitionBy(col("rank180"))).as("sum180")
//    )
//    .filter(
//      col("rank7") === 1 and
//        col("rank30") === 1 and
//        col("rank60") === 1 and
//        col("rank180") === 1
//    )
//    .select("sum7", "sum30", "sum60", "sum180")
//    .distinct

  //    .show

  case class NumbStat(name: String, value: Int, date: Date)

  val data = Range(0, 365 * 3).flatMap(r => {
    Range(0, r % 6 + 1).map(p => {
      NumbStat(RandomStringUtils.random(10, true, false), 1, new Date(startDate.plusDays(r).getMillis))
    })
  })
  import session.implicits._
  val df2 = session.createDataset(data)
  df2.select(col("*"),
    dense_rank().over(Window.orderBy(col("date").desc)).as("rankNumb")
  ).select(
    sum(when(col("rankNumb") <= 7, 1).otherwise(0)).as("col7"),
    sum(when(col("rankNumb") <= 14, 1).otherwise(0)).as("col14"),
    sum(when(col("rankNumb") <= 30, 1).otherwise(0)).as("col30"),
    sum(when(col("rankNumb") <= 60, 1).otherwise(0)).as("col60"),
    sum(when(col("rankNumb") <= 180, 1).otherwise(0)).as("col180"),
    sum(when(col("rankNumb") <= 365, 1).otherwise(0)).as("col365")
  ).show()

}

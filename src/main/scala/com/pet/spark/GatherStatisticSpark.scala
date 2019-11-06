package com.pet.spark

import java.text.SimpleDateFormat

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, dense_rank, sum, when, lit}
import org.apache.spark.sql.{Column, DataFrame, Dataset}
import org.joda.time.DateTime

/**
  * com.pet.spark
  *
  * @author Robert Nad
  */
object GatherStatisticSpark {

  def getCountEventsByDatePeriod[T](sourceDS: Dataset[T], dateColumn: String, statColumn: Seq[(String, Int)]): DataFrame = {
    val nameRankColumn = UtilFunctions.notExistsColumn("rankNumb", sourceDS.columns, (x: String) => x)
    sourceDS
      .withColumn(nameRankColumn, dense_rank().over(Window.orderBy(col(dateColumn).desc)))
      .filter(col(nameRankColumn) <= statColumn.maxBy(p => p._2)._2)
      .select(
        statColumn
          .map(r =>
            sum(when(col(nameRankColumn) <= r._2, 1).otherwise(0)).as(r._1)
          ): _*
      )
  }

  //  val cashData = rawData.cache()
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
}

package com.pet.spark.utils

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, dense_rank, sum, when}
import org.apache.spark.sql.{DataFrame, Dataset}

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object StatisticFunctionSpark {

  def getCountEventsByDatePeriod[T](sourceDS: Dataset[T], dateColumn: String, statColumn: Seq[(String, Int)]): DataFrame = {
    val nameRankColumn = UtilFunctions.notExistsColumn("rankNumb", sourceDS.columns, (x: String) => x)
    sourceDS
      .withColumn(nameRankColumn, dense_rank().over(Window.orderBy(col(dateColumn).desc)))
      .filter(col(nameRankColumn) <= statColumn.maxBy(p => p._2)._2)
      .persist()
      .select(
        statColumn
          .map(r =>
            sum(when(col(nameRankColumn) <= r._2, 1).otherwise(0)).as(r._1)
          ): _*
      )
  }
}

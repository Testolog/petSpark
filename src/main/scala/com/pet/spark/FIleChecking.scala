package com.pet.spark

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}


/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object FIleChecking extends App {

  //  import sparkSession.implicits._
  val schemaWrite = StructType(Seq(
    StructField("col1", StringType, nullable = false),
    StructField("col2", StringType, nullable = false),
    StructField("col3", StringType, nullable = false)
  ))
  val schemaRead1 = StructType(Seq(
    StructField("col1", StringType, nullable = false),
    StructField("col2", IntegerType, nullable = false)
  ))
  val schemaRead2 = StructType(Seq(
    StructField("col1", StringType, nullable = false),
    StructField("col4", StringType, nullable = false),
    StructField("col5", StringType, nullable = false),
    StructField("col6", StringType, nullable = false),
    StructField("col7", StringType, nullable = false)
  ))
  private val data = Seq(
    Row("1", "2", "3"),
    Row("1", "3", "4"),
    Row("1", "5", "6"),
  )

  private val pathTo = ".testParquet"
  sparkSession
    .createDataFrame(sparkSession.sparkContext.parallelize(data), schemaWrite)
    .coalesce(1)
    .write
    .mode("overwrite")
    .parquet(pathTo)

  val df = sparkSession.read.schema(schemaRead2).parquet(pathTo)
  df.queryExecution.debug.codegen()
}

package com.pet.spark

import java.sql.Date

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object WindowSamples extends App {

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  case class SamplesSales(
                           ordernumber: Option[Int],
                           quantityordered: Option[Int],
                           priceeach: Option[Double],
                           orderlinenumber: Option[Int],
                           sales: Option[Double],
                           orderdate: Option[Date],
                           status: Option[String],
                           qtr_id: Option[Int],
                           month_id: Option[Int],
                           year_id: Option[Int],
                           productline: Option[String],
                           msrp: Option[Int],
                           productcode: Option[String],
                           customername: Option[String],
                           phone: Option[String],
                           addressline1: Option[String],
                           addressline2: Option[String],
                           city: Option[String],
                           state: Option[String],
                           postalcode: Option[String],
                           country: Option[String],
                           territory: Option[String],
                           contactlastname: Option[String],
                           contactfirstname: Option[String],
                           dealsize: Option[String]
                         )

  val sparkSession = SparkSession
    .builder()
    .config(new SparkConf()
      .setAppName("pet spark project")
      .setMaster("local[*]"))
    .enableHiveSupport
    .getOrCreate()

  import sparkSession.implicits._

  private val structType = ScalaReflection.schemaFor[SamplesSales].dataType.asInstanceOf[StructType]
  val ds = sparkSession.read
    .option("header", "true")
    .option("inferSchema", "true")
    .option("dateFormat", "MM/dd/yyyy")
    .schema(structType)
    .csv("/Users/testolog/Downloads/datasets_435_896_sales_data_sample.csv")
  val baseInfo = ds.select(
    'orderdate,
    'ordernumber,
    'quantityordered,
    'sales,
    //    'priceeach,
    'productline,
    'qtr_id,
    'msrp,
    'productcode
  )
  //Rank function
  //сортування визначає що саме буде ранкуватись, та в межах яких об'ємів данних
  val productLineOrdering = Window.partitionBy('orderdate, 'ordernumber)
    .orderBy('quantityordered)

  baseInfo.select(
    'orderdate, 'ordernumber, 'quantityordered, 'sales,
    row_number().over(productLineOrdering).as("row_number"),
    //для прикладу зрівння функцій ранкування
    dense_rank().over(productLineOrdering).as("dense_rank"),
    rank().over(productLineOrdering).as("rank"),
    percent_rank().over(productLineOrdering).as("percent_rank"),
    //функція росподілу данних по партиції
    cume_dist().over(productLineOrdering).as("cume_dist"),
  ).orderBy('row_number)
    .show()
  //як працює рендж при агрегуванні
  //rangeBetween має в собі стартову позиці, та кінцеву, вираховується наступним чином
  // value in (current row + start between current row + end)
  // таким чином для прикладу візьмемо 'sales' |1151.44, а також нижню границю в -250, а верхню в 500
  // та сформуємо список номерів замовлень, які потрапляють в один ціновий сегментів за однуй й туж дату
  // для простоти прикладу в якості унікального айді замовлення буду використовувати 'row_number' поточного дня
  //  також потрібно зауважити, що rangeBetween працює тільки з одинокою колонкою яка є в orderBy відвов функції, а також працює тільки з однією колонкою

  baseInfo
    .withColumn("row_number", row_number().over(Window.partitionBy('orderdate, 'ordernumber)
      .orderBy('sales)))
    .select('row_number,
      'sales,
      ('sales - 250).as("lowerBountris"),
      ('sales + 500).as("upperBountris"),
      collect_list('row_number).over(Window.partitionBy('orderdate)
        .orderBy('sales)
        .rangeBetween(-250, 500)).as("linkedSales")
    ).orderBy('sales)
    .show()
  //  point1.withColumnRenamed()
  /*

    +----------+-------+-------------+-------------+------------+
    |row_number|  sales|lowerBountris|upperBountris| linkedSales|
    +----------+-------+-------------+-------------+------------+
    |         1|1151.44|       901.44|      1651.44|         [1]|
    |         2|2174.42|      1924.42|      2674.42|      [2, 3]|
    |         3|2436.72|      2186.72|      2936.72|      [3, 4]|
    |         4|2840.48|      2590.48|      3340.48|   [4, 5, 6]|
    |         5|2969.96|      2719.96|      3469.96|   [4, 5, 6]|
    |         6| 3165.5|       2915.5|       3665.5|[5, 6, 7, 8]|
    |         7|3543.28|      3293.28|      4043.28|      [7, 8]|
    |         8|3611.16|      3361.16|      4111.16|      [7, 8]|
    |         9|4419.89|      4169.89|      4919.89|         [9]|
    |        10|5121.59|      4871.59|      5621.59|    [10, 11]|
    |        11|5362.83|      5112.83|      5862.83|    [10, 11]|
    |        12|5891.04|      5641.04|      6391.04|        [12]|
    +----------+-------+-------------+-------------+------------+
    */

  //подібне до rangeBetween тільки працюєш з номерами ровом, по сортуванню в orderBy, по репартиції
  baseInfo
    .select(
      'sales,
      'quantityordered,
      collect_list('quantityordered).over(Window
        .orderBy('quantityordered)
        .rowsBetween(-1, 2)).as("linkedSales")
    ).orderBy('quantityordered)
    .show()


  //для подальшого розгляду можна вивести  статитиску продажів за послідні 7, 14, 30, 180, 365 днів
  GatherStatisticSpark.getCountEventsByDatePeriod(baseInfo, "orderDate", Seq(("col7", 7), ("col14", 14))).show()
  baseInfo
    .withColumn("minimumDate", first('orderDate).over(Window.orderBy('orderDate)))
    .select(
      sum(when('orderDate <= date_add('minimumDate, 7), 1)
        .otherwise(0)).alias("count7"),
      sum(when('orderDate <= date_add('minimumDate, 14), 1)
        .otherwise(0)).alias("count14"),
      sum(when('orderDate <= date_add('minimumDate, 30), 1)
        .otherwise(0)).alias("count30"),
      sum(when('orderDate <= date_add('minimumDate, 180), 1)
        .otherwise(0)).alias("count180"),
      sum(when('orderDate <= date_add('minimumDate, 365), 1)
        .otherwise(0)).alias("count365"),
      max('sales).alias("maxPrice")
    ).show()

  val duplicate = baseInfo.union(baseInfo)
  println(duplicate.count())
  println(baseInfo.count())
  duplicate.select(duplicate.columns.map(max): _*).show()

}

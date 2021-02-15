package com.pet.spark

import org.apache.spark.sql.DataFrame
import org.apache.spark.util.sketch.BloomFilter

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object BloomFilterJoin extends App {

  import sparkSession.implicits._

  def getBFFromRecords[T](records: List[T], count: Long, fpp: Double) = {
    val _bf = BloomFilter.create(count, fpp)
    records.foreach(_bf.put(_))
    _bf
  }

  val df = Range(0, 150).map(c => (c, c % 2, c % 3, c % 4)).toDF("key", "key2", "key3", "key4")
  private val smallDf: DataFrame = Seq(1, 5, 7).toDF("key3")
  smallDf.exceptAll(df.select("key3")).explain()
  smallDf.join(df, smallDf("key3") === df("key3"), "left_anti").explain()
  smallDf.rdd.subtract(df.select("key3").rdd)
  //  val bf = df.stat.bloomFilter("key2", 1024, 0.9)
  //  println(bf.mightContain("0"))
  //  print(df.stat.countMinSketch("key", 10, 10, 10).confidence())
}

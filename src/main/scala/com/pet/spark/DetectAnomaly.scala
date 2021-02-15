package com.pet.spark

import org.apache.spark.sql.functions

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object DetectAnomaly extends App {

  import sparkSession.implicits._

  case class Bounds(lower_bound: Double, upper_bound: Double)

  case class Numb(n: Double)

  val df = sparkSession.createDataset(Seq(2, 3, 5, 2, 3, 12, 5, 3, 4).map(Numb(_)))
  val avgDf = df.select(functions.avg("n"))
  val deviation = df.select(functions.stddev("n"))
  avgDf.show()
  deviation.show()
  val boundsDeviation = df.select(
    (functions.avg("n") - functions.stddev("n")).alias("lower_bound"),
    (functions.avg("n") + functions.stddev("n")).alias("upper_bound")
  ).as[Bounds].collect().head

  df.select($"n", (!$"n".between(boundsDeviation.lower_bound, boundsDeviation.upper_bound)).alias("is_anomaly"))
    .show()


  sparkSession.conf.set("spark.sql.crossJoin.enabled", "true")
  df
    .join(functions.broadcast(df.select(
      (functions.avg("n") - functions.stddev("n")).alias("lower_bound"),
      (functions.avg("n") + functions.stddev("n")).alias("upper_bound")
    )))
    .select(
      $"n",
      (!$"n".between($"lower_bound", $"upper_bound")).alias("is_anomaly")
    )
    .show()

}

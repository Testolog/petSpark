package com.pet.spark.aggregation

import com.pet.spark.aggregation.anomaly.DetectAnomaly
import org.apache.spark.sql.{DataFrame, functions}

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */

object Boundaries {

  private case class Bounds(lower_bound: Double, upper_bound: Double)

  def appendAnomalyColumnByBounds(detectAnomaly: DetectAnomaly): DetectAnomaly = {
    import detectAnomaly.frame.sparkSession.implicits._
    val boundsDeviation = detectAnomaly.frame.select(
      (functions.avg(detectAnomaly.processColumn) - functions.stddev(detectAnomaly.processColumn)).alias("lower_bound"),
      (functions.avg(detectAnomaly.processColumn) + functions.stddev(detectAnomaly.processColumn)).alias("upper_bound")
    ).as[Bounds].collect().head
    detectAnomaly.callBack(
      detectAnomaly.frame.withColumn(detectAnomaly.anomalyColumnName,
        !detectAnomaly.frame(detectAnomaly.processColumn)
          .between(boundsDeviation.lower_bound, boundsDeviation.upper_bound)
      )
    )
  }

  def removeAnomalies(detectAnomaly: DetectAnomaly): DataFrame =
    detectAnomaly.frame.filter(detectAnomaly.frame(detectAnomaly.anomalyColumnName) === true)


}

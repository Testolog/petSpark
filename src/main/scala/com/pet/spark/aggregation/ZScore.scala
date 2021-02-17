package com.pet.spark.aggregation

import com.pet.spark.aggregation.anomaly.DetectAnomaly
import org.apache.spark.sql.{DataFrame, functions}

/**
 * com.pet.spark.aggregation
 *
 * @author Robert Nad
 */
sealed trait ZScore {
  val lower: Option[Int]
  val upper: Option[Int]
}

object ZScore {

  case object UNBOUND_LOWER extends ZScore {
    override val lower: Option[Int] = None
    override val upper: Option[Int] = Some(2)
  }

  case object UNBOUND_UPPER extends ZScore {
    override val lower: Option[Int] = Some(-2)
    override val upper: Option[Int] = None
  }

  case object UPPER_ONLY extends ZScore {
    override val lower: Option[Int] = Some(-2)
    override val upper: Option[Int] = Some(2)
  }

  case class CustomBoundaries(override val lower: Option[Int],
                              override val upper: Option[Int]) extends ZScore

  def appendZScoreColumn(detectAnomaly: DetectAnomaly): DetectAnomaly =
    detectAnomaly.callBack(
      detectAnomaly.frame
        .withColumn(detectAnomaly.anomalyColumnName,
          (detectAnomaly.frame(detectAnomaly.processColumn) - functions.avg(detectAnomaly.processColumn)) / functions.stddev(detectAnomaly.processColumn)
        )
    )

  def filter(detectAnomaly: DetectAnomaly)(zScore: ZScore): DataFrame = zScore match {
    case UNBOUND_LOWER => filter(detectAnomaly, UNBOUND_LOWER.lower, UNBOUND_LOWER.upper)
    case UNBOUND_UPPER => filter(detectAnomaly, UNBOUND_UPPER.lower, UNBOUND_UPPER.upper)
    case UPPER_ONLY => filter(detectAnomaly, UPPER_ONLY.lower, UPPER_ONLY.upper)
    case CustomBoundaries(lower, upper) => filter(detectAnomaly, lower, upper)
  }

  def filter(detectAnomaly: DetectAnomaly, lower: Option[Int], upper: Option[Int]): DataFrame = {
    (lower, upper) match {
      case (Some(l), Some(u)) => detectAnomaly.frame
        .filter(detectAnomaly.frame(detectAnomaly.anomalyColumnName)
          .between(l, u))
      case (None, Some(u)) =>
        detectAnomaly.frame
          .filter(detectAnomaly.frame(detectAnomaly.anomalyColumnName) <= u)
      case (Some(l), None) =>
        detectAnomaly.frame
          .filter(detectAnomaly.frame(detectAnomaly.anomalyColumnName) >= l)

    }

  }
}

package com.pet.spark.aggregation.anomaly

import org.apache.spark.sql.DataFrame

/**
 * com.pet.spark.aggregation.anomaly
 *
 * @author Robert Nad
 */
case class DetectAnomaly(frame: DataFrame, processColumn: String, anomalyColumnName: String = "is_anomaly") {
  def callBack(df: DataFrame): DetectAnomaly =
    DetectAnomaly(df, processColumn, anomalyColumnName)
}

object DetectAnomaly {
  def apply(frame: DataFrame, processColumn: String, anomalyColumnName: String = "is_anomaly"): DetectAnomaly = {
    new DetectAnomaly(frame, processColumn, anomalyColumnName)
  }

}

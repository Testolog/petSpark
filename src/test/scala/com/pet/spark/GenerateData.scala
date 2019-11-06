package com.pet.spark

import java.sql.Date
import java.text.SimpleDateFormat

import org.apache.commons.lang.RandomStringUtils
import org.apache.spark.sql.{Encoder, Encoders}
import org.joda.time.DateTime

import scala.collection.immutable

/**
  * com.pet.spark
  *
  * @author Robert Nad
  */
case class DateNumbStat(name: String, value: Int, date: Date)

trait GenerateData {

  val date = new SimpleDateFormat("yyyy/MM/dd")
  val startDate = new DateTime(date.parse("2016/01/01"))


  implicit val dateNumbStat: Encoder[DateNumbStat] = Encoders.product[DateNumbStat]

  def generateDateNumbs: immutable.IndexedSeq[DateNumbStat] = Range(0, 365 * 3).flatMap(row => {
    Range(0, row % 7 + 1).map(_ => {
      DateNumbStat(
        RandomStringUtils.random(10, true, false), 1, new Date(startDate.plusDays(row).getMillis)
      )
    })
  })


}

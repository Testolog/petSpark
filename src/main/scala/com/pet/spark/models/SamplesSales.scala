package com.pet.spark.models

import java.sql.Date

/**
 * com.pet.spark.models
 *
 * @author Robert Nad
 */
case class SamplesSales(
                         ordernumber: Option[Double],
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

package com.pet.spark

import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object CheckHowSchemaEffectOnIO extends App {
  //there is idea to check how predifine schema effect on IO

  val schemaCase = sparkSession
    .read
    .schema(StructType(
      Seq(
        StructField("ORDERNUMBER", IntegerType),
        StructField("PRODUCTLINE", StringType)
      )
    ))
    .parquet("sales_data_sample")
    .withColumn("col", lit("asd"))
    .withColumn("col2", lit("asd"))
    .withColumn("col3", lit("asd"))

  val selectCase = sparkSession
    .read
    .parquet("sales_data_sample")

  schemaCase.explain(true)
  selectCase.explain(true)

  schemaCase
    .join(selectCase, schemaCase("ORDERNUMBER") === selectCase("ORDERNUMBER"))
    .select(selectCase("ORDERNUMBER"), selectCase("PRODUCTLINE"), schemaCase("*"))
    .explain()


  val selectCase2 = sparkSession
    .read
    .schema(StructType(
      Seq(
        StructField("ORDERNUMBER", IntegerType),
        StructField("PRODUCTLINE", StringType),
        StructField("cool", StringType)
      )
    ))
    .parquet("sales_data_sample")
  selectCase2.show()
  selectCase2.explain(true)

  schemaCase
    .select(schemaCase("ORDERNUMBER"))
    .explain(true)
}

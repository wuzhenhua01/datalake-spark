package com.xueyingying.datalake.e2e

import com.xueyingying.datalake.SparkSuiteBase

/**
 * @author cx330.1000ly@gmail.com
 * @version 1.0.0
 * @since 2023-02-17
 */
class ParquetSparkSQLTest extends SparkSuiteBase {
  "parquet" should "write" in {
    spark.sql(
      """
        |CREATE TABLE t1 (
        |  id STRING,
        |  name STRING
        |)
        |USING parquet
        |
      """.stripMargin)

    spark.sql("INSERT INTO t1 VALUES('1', 'aa')")
  }
}

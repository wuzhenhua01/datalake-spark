package com.xueyingying.datalake.e2e

import com.xueyingying.datalake.SparkSuiteBase

/**
 * @author cx330.1000ly@gmail.com
 * @version 1.0.0
 * @since 2025-03-06
 */
class JdbcSparkSQLTest extends SparkSuiteBase {
  "sqlserver" should "read" in {
    spark.sql(
      """
        |CREATE TEMPORARY VIEW t1
        |USING org.apache.spark.sql.jdbc
        |OPTIONS (
        |  url "jdbc:sqlserver://pn41",
        |  dbtable "shifenzheng.dbo.cdsgus",
        |  user 'sa',
        |  password 'root',
        |  partitionColumn 'id',
        |  numPartitions 2,
        |  lowerBound 1,
        |  upperBound 1000
        |)
      """.stripMargin)

    spark.sql(
      """
        |CREATE TEMPORARY VIEW t2
        |USING org.apache.spark.sql.jdbc
        |OPTIONS (
        |  url "jdbc:mysql://xueyingying.com/demo",
        |  dbtable "cdsgus",
        |  user 'root',
        |  password 'root'
        |)
      """.stripMargin)

    spark.sql("INSERT INTO t2 SELECT * FROM t1 WHERE id >= 10000 AND id < 20000")
  }

  "mysql" should "read" in {
    spark.sql(
      """
        |CREATE TEMPORARY VIEW t1
        |USING org.apache.spark.sql.jdbc
        |OPTIONS (
        |  url "jdbc:mysql://xueyingying.com/demo",
        |  dbtable "t1",
        |  user 'root',
        |  password 'root'
        |)
      """.stripMargin)

    spark.sql("SELECT count(1) FROM t1").show
  }

  "postgres" should "read" in {
    spark.sql(
      """
        |CREATE TEMPORARY VIEW t1
        |USING org.apache.spark.sql.jdbc
        |OPTIONS (
        |  url "jdbc:postgresql://xueyingying.com/demo",
        |  dbtable "t1",
        |  user 'postgres',
        |  password 'postgres'
        |)
      """.stripMargin)

    spark.sql("SELECT count(1) FROM t1 limit 10").show
  }
}

package com.xueyingying.datalake.e2e

import com.xueyingying.datalake.SparkSuiteBase

/**
 * @author cx330.1000ly@gmail.com
 * @version 1.0.0
 * @since 2025-03-06
 */
class FileSparkSQLTest extends SparkSuiteBase {
  "csv" should "read" in {
    spark.sql(
      """
        |CREATE TABLE t1 (
        |  name varchar(255),
        |  cardno varchar(255),
        |  descriot varchar(255),
        |  ctftp varchar(255),
        |  ctfid varchar(255),
        |  gender varchar(255),
        |  birthday varchar(255),
        |  address varchar(255),
        |  zip varchar(255),
        |  dirty varchar(255),
        |  district1 varchar(255),
        |  district2 varchar(255),
        |  district3 varchar(255),
        |  district4 varchar(255),
        |  district5 varchar(255),
        |  district6 varchar(255),
        |  firstnm varchar(255),
        |  lastnm varchar(255),
        |  duty varchar(255),
        |  mobile varchar(255),
        |  tel varchar(255),
        |  fax varchar(255),
        |  email varchar(255),
        |  nation varchar(255),
        |  taste varchar(255),
        |  education varchar(255),
        |  company varchar(255),
        |  ctel varchar(255),
        |  caddress varchar(255),
        |  czip varchar(255),
        |  family varchar(255),
        |  version varchar(255),
        |  id int
        |)
        |USING csv
        |OPTIONS (
        |  path "D:/a.txt",
        |  encoding "GBK"
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

    spark.sql("INSERT INTO t2 SELECT * FROM t1")
  }
}

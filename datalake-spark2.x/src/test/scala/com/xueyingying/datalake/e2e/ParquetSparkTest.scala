package com.xueyingying.datalake.e2e

import com.xueyingying.datalake.SparkSuiteBase
import org.apache.hadoop.io.ArrayWritable
import org.apache.parquet.hadoop.ParquetInputFormat
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}

/**
 * @author cx330.1000ly@gmail.com
 * @version 1.0.0
 * @since 2023-02-17
 */
class ParquetSparkTest extends SparkSuiteBase {
  "parquet" should "read" in {
    sc.newAPIHadoopFile("hdfs://hacluster/tmp/t1_parquet", classOf[ParquetInputFormat[ArrayWritable]], classOf[Void], classOf[ArrayWritable])
      .take(10)
      .foreach { case (k, v) =>
        val writables = v.get()
        val c1 = writables(0)
        val c2 = writables(1)
        println(writables.length + "    " + c1 + "   " + c2)
      }

    val parquet = spark.read.parquet("hdfs://hacluster/tmp/t1_parquet")
    parquet.printSchema()
    parquet.select(parquet("c1"), parquet("c2")).show()
  }

  "parquet" should "write" in {
    val rdd = sc.makeRDD(List("1 Scala", "2 Spark"))

    val schema = (new StructType)
      .add("c1", IntegerType, nullable = false)
      .add("c2", StringType, nullable = true)

    rdd.map(_.split(" ")).map(line => Row(Integer.valueOf(line(0).trim), line(1))).saveAsTextFile("/tmp/t1")
  }
}

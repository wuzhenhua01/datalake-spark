package com.xueyingying.datalake

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.internal.SQLConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec

import scala.reflect.io.Path

/**
 * @author cx330.1000ly@gmail.com
 * @version 1.0.0
 * @since 2023-03-17
 */
trait SparkSuiteBase extends AnyFlatSpec with BeforeAndAfterAll {
  @transient var spark: SparkSession = _
  @transient var sc: SparkContext = _
  @transient var ssc: StreamingContext = _
  @transient var sparkConf: SparkConf = _

  override def beforeAll: Unit = {
    sparkConf = new SparkConf(false)
    spark = SparkSession.builder
      .master("local[4]")
      .config(SQLConf.SHUFFLE_PARTITIONS.key, "4")
      .config("spark.default.parallelism", "4")
      .config("spark.sql.crossJoin.enabled", "true")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config(SQLConf.PARQUET_COMPRESSION.key, "snappy")
      //  .config(StaticSQLConf.SPARK_SESSION_EXTENSIONS.key, classOf[HoodieSparkSessionExtension].getCanonicalName)
      // .config("spark.extraListeners", classOf[SparkListenerTest].getCanonicalName)
      .config(sparkConf)
      .getOrCreate()
    sc = spark.sparkContext
    ssc = new StreamingContext(sc, Seconds(10))
  }

  override def afterAll(): Unit = {
    try {
      spark.sparkContext.stop()
      SparkSession.clearActiveSession()
      spark.stop()
      sc.stop()

      cleanTestHiveData()
    } finally {
      super.afterAll()
    }
  }

  def cleanTestHiveData(): Unit = {
    val metastoreDB = Path("/tmp/metastore_db")
    if (metastoreDB.exists) {
      metastoreDB.delete()
    }
    val sparkWarehouse = Path("spark-warehouse")
    if (sparkWarehouse.exists) {
      sparkWarehouse.delete()
    }
  }
}

package com.xueyingying.datalake.e2e

import java.util

import com.xueyingying.datalake.SparkSuiteBase
import org.apache.hudi.QuickstartUtils.DataGenerator
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.hudi.{DataSourceReadOptions, DataSourceWriteOptions, QuickstartUtils}
import org.apache.spark.sql.SaveMode

import scala.collection.JavaConversions.asScalaBuffer

/**
 * @author cx330.1000ly@gmail.com
 * @version 1.0.0
 * @since 2023-02-17
 */
class HudiSparkSQLTest extends SparkSuiteBase {
  val tableName = "t1"
  val basePath = "file:/tmp/t1"
  val t2basePath = "file:/tmp/t2"
  val dataGen = new DataGenerator

  override def beforeAll: Unit = {
    super.beforeAll()
  }

  "insert" should "run" in {
    val spark = this.spark
    import spark.implicits._

    val inserts: util.List[String] = QuickstartUtils.convertToStringList(dataGen.generateInserts(10))
    spark.read.json(spark.createDataset(inserts)).
      write.format("hudi").
      options(QuickstartUtils.getQuickstartWriteConfigs).
      option(DataSourceWriteOptions.PRECOMBINE_FIELD.key, "ts").
      option(DataSourceWriteOptions.RECORDKEY_FIELD.key, "uuid").
      option(DataSourceWriteOptions.PARTITIONPATH_FIELD.key, "partitionpath").
      option(HoodieWriteConfig.TBL_NAME.key, tableName).
      option(DataSourceWriteOptions.HIVE_STYLE_PARTITIONING.key, value = true).
      mode(SaveMode.Append).
      save(basePath)
  }

  "update" should "run" in {
    val inserts = QuickstartUtils.convertToStringList(dataGen.generateInserts(10))
    spark.read.json(spark.sparkContext.parallelize(inserts, 2)).
      write.format("hudi").
      options(QuickstartUtils.getQuickstartWriteConfigs).
      option(DataSourceWriteOptions.PRECOMBINE_FIELD.key, "ts").
      option(DataSourceWriteOptions.RECORDKEY_FIELD.key, "uuid").
      option(DataSourceWriteOptions.PARTITIONPATH_FIELD.key, "partitionpath").
      option(HoodieWriteConfig.TBL_NAME.key, tableName).
      option(DataSourceWriteOptions.HIVE_STYLE_PARTITIONING.key, value = true).
      mode(SaveMode.Overwrite).
      save(basePath)

    val updates = QuickstartUtils.convertToStringList(dataGen.generateUpdates(10))
    spark.read.json(spark.sparkContext.parallelize(updates, 2)).
      write.format("hudi").
      options(QuickstartUtils.getQuickstartWriteConfigs).
      option(DataSourceWriteOptions.PRECOMBINE_FIELD.key, "ts").
      option(DataSourceWriteOptions.RECORDKEY_FIELD.key, "uuid").
      option(DataSourceWriteOptions.PARTITIONPATH_FIELD.key, "partitionpath").
      option(HoodieWriteConfig.TBL_NAME.key, tableName).
      option(DataSourceWriteOptions.HIVE_STYLE_PARTITIONING.key, value = true).
      mode(SaveMode.Append).
      save(basePath)
  }

  "select" should "run" in {
    spark.read.format("hudi").load("/tmp/t2").createOrReplaceTempView("t1")
    spark.sql("select count(*) from (select log_id,count(*) from t1 group by log_id having count(*)>1 ) a limit 1").show(Int.MaxValue, truncate = false)
  }

  "aa" should "run" in {
    spark.read.format("hudi").load("file:/tmp/t1").createOrReplaceTempView("t1")
    spark.sql("select * from t1").show(Int.MaxValue, truncate = false)
  }

  "select2" should "run" in {
    spark.read.
      format("hudi").
      option(DataSourceReadOptions.TIME_TRAVEL_AS_OF_INSTANT.key, "20230910221442376").
      option("as.of.instant", "20230910221442376").
      load(basePath).
      createOrReplaceTempView("t1")

    spark.sql("SELECT * FROM t1").show
  }

  "select3" should "run" in {
    spark.read.
      format("hudi").
      option(DataSourceReadOptions.QUERY_TYPE.key, DataSourceReadOptions.QUERY_TYPE_INCREMENTAL_OPT_VAL).
      option(DataSourceReadOptions.BEGIN_INSTANTTIME.key, "20230910221442375").
      load(basePath).
      createOrReplaceTempView("t1")

    spark.sql("SELECT * FROM t1").show
  }

  "insert 2" should "work" in {
    spark.sql(
      """
        |create table t2 (
        |  id int,
        |  name string,
        |  price double,
        |  ts bigint
        |) using hudi
        |options (
        |  type = 'mor',
        |  primaryKey = 'id',
        |  preCombineField = 'ts'
        |)
        |location '/tmp/t2'
      """.stripMargin)

    spark.sql("insert into t2 values (1, 'a2', 20, 1000)")
    spark.sql("SELECT * FROM t2").show
  }

  "select t2" should "work" in {
    val tripsSnapshotDF = spark.read.
      format("hudi").
      option(DataSourceReadOptions.TIME_TRAVEL_AS_OF_INSTANT.key, "20230913110228049").
      load(t2basePath)
    tripsSnapshotDF.createOrReplaceTempView("t2")

    spark.sql("SELECT * FROM t2").show
  }

  "show commit time" should "work" in {
    val spark = this.spark
    spark.read.
      format("hudi").
      load(basePath).
      createOrReplaceTempView("t2")

    import spark.implicits._
    val commits = spark.sql("select distinct(_hoodie_commit_time) as commitTime from t2 order by commitTime").map(k => k.getString(0)).take(50)
    commits.foreach {
      commit => println(commit)
    }
  }

  "test " should "work" in {
    spark.read.
      format("hudi").
      load(t2basePath).
      createOrReplaceTempView("t2")
    val commits = spark.sql("call show_commits(table => 't2')").limit(2).collect()
    commits.foreach {
      commit => println(commit)
    }
  }

  "create" should "work" in {
    spark.sql("create table t1 using hudi location '/tmp/t1'")
    spark.sql("call show_commits(table => 't1', limit => 100)").show(200)
  }

  "zz" should "work" in {
    spark.read.
      format("hudi").
      option(DataSourceReadOptions.QUERY_TYPE.key, DataSourceReadOptions.QUERY_TYPE_INCREMENTAL_OPT_VAL).
      option(DataSourceReadOptions.BEGIN_INSTANTTIME.key, "000").
      option(DataSourceReadOptions.END_INSTANTTIME.key, "20230913161536101").
      load().
      createOrReplaceTempView("t1")

    spark.sql("SELECT * FROM t1").show(true)
  }

  "t2" should "create" in {
    spark.sql(
      """
        |create table t3 (
        |  id int,
        |  name string
        |) using hudi
        |TBLPROPERTIES (
        |  type = 'mor'
        |)
        |location '/tmp/t2'
      """.stripMargin)

    sc.hadoopConfiguration.set("textinputformat.record.delimiter", "^")
    sc.textFile("")
    val rdd = sc.textFile("/Volumes/Samsung\\ USB/a.txt")
    rdd.count()
  }

  "a" should "run" in {
    spark.sql(
      """
        |CREATE TABLE dws_ord_order_item (
        |  op_type string,
        |  op_ts string
        |) using hudi location 'file:/tmp/dws_ord_order_item'
      """.stripMargin)
    spark.sql("SELECT * FROM dws_ord_order_item").show
  }

  "conf" should "a" in {
    sparkConf.getAll.foreach(println)
  }

  "stddev" should "check" in {
    spark.sql("SELECT mean(age),variance(age),stddev(age),corr(age,yearsmarried),skewness(age),kurtosis(age) FROM Affairs")
  }
}

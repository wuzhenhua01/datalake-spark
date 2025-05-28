package com.xueyingying.datalake.e2e

import com.xueyingying.datalake.SparkSuiteBase

/**
 * @author cx330.1000ly@gmail.com
 * @version 1.0.0
 * @since 2023-02-17
 */
class HudiSparkSQLTest extends SparkSuiteBase {
  override def beforeAll: Unit = {
    super.beforeAll
  }

  "insert operation" should "write" in {
    spark.sql(
      """
        |CREATE TABLE t1 (
        |  ts BIGINT,
        |  uuid STRING,
        |  rider STRING,
        |  driver STRING,
        |  fare DOUBLE,
        |  city STRING
        |) USING HUDI
        |LOCATION 'file:///tmp/t1/'
        |TBLPROPERTIES(
        |  type = 'mor',
        |  primaryKey = 'uuid',
        |  preCombineField = 'ts'
        |)
      """.stripMargin)

    spark.sql(
      """
        |INSERT OVERWRITE t1 VALUES
        |(1695159649087, '334e26e9-8355-45cc-97c6-c31daf0df330', 'rider-A', 'driver-K', 19.10, 'san_francisco'),
        |(1695091554788, 'e96c4396-3fad-413a-a942-4cb36106d721', 'rider-C', 'driver-M', 27.70, 'san_francisco'),
        |(1695046462179, '9909a8b1-2d15-4d3d-8ec9-efc48c536a00', 'rider-D', 'driver-L', 33.90, 'san_francisco'),
        |(1695332066204, '1dced545-862b-4ceb-8b43-d2a568f6616b', 'rider-E', 'driver-O', 93.50, 'san_francisco'),
        |(1695516137016, 'e3cf430c-889d-4015-bc98-59bdce1e530c', 'rider-F', 'driver-P', 34.15, 'sao_paulo'),
        |(1695376420876, '7a84095f-737f-40bc-b62f-6b69664712d2', 'rider-G', 'driver-Q', 43.40, 'sao_paulo'),
        |(1695173887231, '3eeb61f7-c2b0-4636-99bd-5d7a5a1d2c04', 'rider-I', 'driver-S', 41.06, 'chennai'),
        |(1695115999911, 'c8abbe79-8d89-47ea-b4ce-4d224bae5bfa', 'rider-J', 'driver-T', 17.85, 'chennai')
      """.stripMargin)

    spark.sql(
      """
        |INSERT OVERWRITE TABLE t1
        |SELECT 1695159649087, '334e26e9-8355-45cc-97c6-c31daf0df330', 'rider-A', 'driver-K', 19.10, 'san_francisco'
      """.stripMargin)

    spark.sql("SELECT ts, uuid, fare, rider, driver, city FROM t1").show()
  }

  "test partial update" should "write" in {
    spark.sql(
      """
        |CREATE TABLE t1 (
        |  a BIGINT,
        |  b STRING,
        |  c STRING,
        |  ts BIGINT
        |) USING HUDI
        |LOCATION 'file:///tmp/t1/'
        |TBLPROPERTIES(
        |  type = 'mor',
        |  primaryKey = 'a',
        |  preCombineField = 'ts',
        |  hoodie.datasource.write.payload.class = 'org.apache.hudi.common.model.PartialUpdateAvroPayload'
        |)
      """.stripMargin)

    spark.sql(
      """
        |INSERT INTO TABLE t1
        |SELECT 1, 'a', null, 1
      """.stripMargin)
    spark.sql("SELECT * FROM t1").show()

    spark.sql(
      """
        |INSERT INTO TABLE t1
        |SELECT 1, null, 'b', 2
      """.stripMargin)
    spark.sql("SELECT * FROM t1").show()
  }

  "incre" should "read" in {
    spark.sql(
      """
        |SELECT *
        |FROM hudi_table_changes(
        |  '/tmp/t1',
        |  'latest_state',
        |  'earliest',
        |  '2024-10-30 17:11:00.000'
        |)
      """.stripMargin).show
  }


  "select4" should "run" in {
    spark.read.format("hudi").load("/tmp/t1").createOrReplaceTempView("t1")
    //spark.sql("select _hoodie_file_name, count(1) from t1 group by _hoodie_file_name").show(Int.MaxValue, truncate = false)
    spark.sql("select count(1) from t1").show(Int.MaxValue, truncate = false)
  }
}

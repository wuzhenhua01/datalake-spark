### Spark Shell
```shell
/cluster/spark-2.4.4-without-hadoop/bin/spark-shell \
  --jars hdfs:/apps/hudi/hudi-spark2.4-bundle_2.11-0.14.0.jar
```

### Test bootstrap
```sql
CREATE TABLE IF NOT EXISTS t1(id int, name string)
USING parquet
LOCATION '/tmp/t1'
```

```sql
INSERT INTO t1 VALUES(1, 'a')
```

```shell
unset HADOOP_CLASSPATH && /cluster/spark-2.4.4/bin/spark-submit \
  --class org.apache.hudi.utilities.streamer.HoodieStreamer \
  hdfs:/apps/hudi/hudi-utilities-bundle_2.11-0.14.0.jar \
  --target-base-path /tmp/t2 --table-type MERGE_ON_READ \
  --target-table t2 \
  --run-bootstrap \
  --bootstrap-overwrite \
  --hoodie-conf hoodie.bootstrap.base.path=hdfs:/tmp/t1 \
  --hoodie-conf hoodie.datasource.write.recordkey.field=id \
  --hoodie-conf hoodie.datasource.write.precombine.field=name \
  --hoodie-conf hoodie.datasource.write.keygenerator.type=NON_PARTITION \
  --hoodie-conf hoodie.bootstrap.parallelism=2 \
  --hoodie-conf hoodie.bootstrap.mode.selector=org.apache.hudi.client.bootstrap.selector.FullRecordBootstrapModeSelector \
  --hoodie-conf hoodie.bulkinsert.shuffle.parallelism=100 \
  --checkpoint ""
```

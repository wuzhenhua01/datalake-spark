### Spark Shell
```shell
/cluster/spark3/bin/spark-shell \
  --jars hdfs:/apps/hudi/hudi-spark3.0-bundle_2.12-0.14.1.jar
```

```shell
/cluster/spark3/bin/spark-sql \
  --jars hdfs:/apps/hudi/hudi-spark3.0-bundle_2.12-0.14.1.jar
```

### Test Syncing to Hive
```sql
CREATE TABLE IF NOT EXISTS t1(id int, name string)
USING HUDI
LOCATION '/tmp/t1'
TBLPROPERTIES (
  type = 'mor',
  hoodie.datasource.hive_sync.enable = 'true',
  hoodie.datasource.hive_sync.mode = 'jdbc',
  hoodie.datasource.hive_sync.jdbcurl = 'jdbc:hive2://localhost:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2',
  hoodie.datasource.hive_sync.username = 'xiaowu'
)
```

```sql
INSERT INTO t1 VALUES(1, 'a')
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
/cluster/spark3/bin/spark-submit \
  --class org.apache.hudi.utilities.streamer.HoodieStreamer \
  hdfs:/apps/hudi/hudi-utilities-bundle_2.12-0.14.0.jar \
  --target-base-path /tmp/t2 --table-type MERGE_ON_READ \
  --target-table t2 \
  --run-bootstrap \
  --bootstrap-overwrite \
  --hoodie-conf hoodie.bootstrap.base.path=/tmp/t1 \
  --hoodie-conf hoodie.datasource.write.recordkey.field=id \
  --hoodie-conf hoodie.datasource.write.precombine.field=name \
  --hoodie-conf hoodie.datasource.write.keygenerator.type=NON_PARTITION \
  --hoodie-conf hoodie.bootstrap.parallelism=2 \
  --hoodie-conf hoodie.bootstrap.mode.selector=org.apache.hudi.client.bootstrap.selector.FullRecordBootstrapModeSelector \
  --hoodie-conf hoodie.bulkinsert.shuffle.parallelism=10 \
  --checkpoint ""
```

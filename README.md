# datalake-spark

```shell
spark-sql \
  --jars datalake-spark3.x-1.0.0.jar \
  --conf spark.ui.enabled=true \
  --conf spark.extraListeners=com.asiainfo.ctc.datalake.A \
  --conf spark.datalake.pushgateway.host=10.37.72.29 \
  --conf spark.datalake.pushgateway.port=9091 \
  --conf spark.datalake.pushgateway.jobName=dwm_lbs_evt_position_compress_hour \
  --conf spark.datalake.pushgateway.groupingKey=prov_id=110000 \
  -f test.sql
```

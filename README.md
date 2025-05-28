# datalake-spark

```shell
spark-sql \
  --jars datalake-spark3.x-1.0.0.jar \
  --conf spark.ui.enabled=true \
  --conf spark.extraListeners=com.xueyingying.datalake.A \
  --conf spark.datalake.pushgateway.host=localhost \
  --conf spark.datalake.pushgateway.port=9091 \
  --conf spark.datalake.pushgateway.jobName=test \
  --conf spark.datalake.pushgateway.groupingKey=prov_id=110000 \
  -f test.sql
```

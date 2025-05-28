package com.xueyingying.datalake.e2e

import org.apache.spark.scheduler.{SparkListener, SparkListenerApplicationEnd, SparkListenerApplicationStart, SparkListenerExecutorMetricsUpdate}

class SparkListenerTest extends SparkListener {
  override def onApplicationStart(applicationStart: SparkListenerApplicationStart): Unit = {
    println(s"applicationStart.appAttemptId = ${applicationStart.appAttemptId}")
    println(s"applicationStart.appId = ${applicationStart.appId}")
    println(s"applicationStart.appName = ${applicationStart.appName}")
    println(s"applicationStart.driverLogs = ${applicationStart.driverLogs}")
    println(s"applicationStart.sparkUser = ${applicationStart.sparkUser}")
    println(s"applicationStart.time = ${applicationStart.time}")
  }

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd): Unit = {
    println(s"applicationEnd.time  =  ${applicationEnd.time}")
  }

  override def onExecutorMetricsUpdate(executorMetricsUpdate: SparkListenerExecutorMetricsUpdate): Unit = {
    println("onExecutorMetricsUpdate")
    val execId = executorMetricsUpdate.execId
    val accu = executorMetricsUpdate.accumUpdates
    println(s"execId   ${execId}")
    for ((taskId, stageId, stageAttemptId, accumUpdates) <- accu) {
      //println(s"""${stageId}\t${accumUpdates.mkString("<==>")}""")
      for (acc <- accumUpdates if (acc.name.isDefined && "number of output rows" == acc.name.get)) {
        println(s"""${stageId}\t${taskId}\t${acc}""")
      }
    }
    println("onExecutorMetricsUpdate")
  }
}

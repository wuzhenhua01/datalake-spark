package com.xueyingying.datalake.e2e

import org.apache.spark.scheduler.{SparkListener, SparkListenerStageCompleted}

class SparkListenerTest extends SparkListener {
  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
    println(stageCompleted.stageInfo.taskMetrics.outputMetrics.recordsWritten)
  }

  def printx(label: String): Unit = {
    println(s"=" * 20 + label + s"=" * 20)
  }
}

package io.wonder.soft.lib.aws

import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration
import com.amazonaws.services.kinesis.producer.KinesisProducer
import io.wonder.soft.lib.oracle.LogMnrContent
import java.nio.ByteBuffer

import com.amazonaws.auth.profile.ProfileCredentialsProvider


trait KinesisRecorder {

  def buildProducer(): KinesisProducer = {
    val config: KinesisProducerConfiguration = new KinesisProducerConfiguration

    config.setRegion("ap-northeast-1") //todo change configuration
    config.setCredentialsProvider(new ProfileCredentialsProvider)
    config.setMaxConnections(1)
    config.setRequestTimeout(60000)
    config.setRecordMaxBufferedTime(15000)

    new KinesisProducer(config)
  }

  val producer = this.buildProducer()

  def putRecords(logMnrContent: LogMnrContent): Unit = {
    val data = ByteBuffer.wrap(logMnrContent.sqlRedo.getBytes("UTF-8"))
    producer.addUserRecord("example", logMnrContent.timestamp, data) //todo change stream name from `example` to config
  }

}

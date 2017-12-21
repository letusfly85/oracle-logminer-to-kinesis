package io.wonder.soft.actor

import akka.actor.Actor
import io.wonder.soft.lib.aws.KinesisRecorder
import io.wonder.soft.lib.oracle.LogMnrContent

class KinesisActor extends Actor with KinesisRecorder {

  def receive  = {
    case logMnrContent: LogMnrContent =>
      putRecords(logMnrContent)
  }
}

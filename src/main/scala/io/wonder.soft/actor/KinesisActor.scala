package io.wonder.soft.actor

import akka.actor.Actor

class KinesisActor extends Actor {

  def receive  = {
    case msg: String =>
    //todo pass data to kinesis
  }

}

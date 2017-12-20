package io.wonder.soft.actor

import akka.actor.Actor

class AuditTraceActor extends Actor {

  def receive  = {
    case msg: String =>
      //todo get data from Oracle LogMiner
  }

}

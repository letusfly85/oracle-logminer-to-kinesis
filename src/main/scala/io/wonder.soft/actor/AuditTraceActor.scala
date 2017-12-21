package io.wonder.soft.actor

import akka.actor.{Actor, ActorRef, Props}
import io.wonder.soft.lib.oracle.{LogFile, LogMinerWatcher}
import scalikejdbc.DB
import scalikejdbc.config.DBs

class AuditTraceActor extends Actor with LogMinerWatcher {
  val kinesisActor: ActorRef = context.system.actorOf(Props(classOf[KinesisActor]), "kinesis-actor")
  DBs.setupAll()

  var fileName = ""

  def receive  = {
    case schemaName: String =>
      DB localTx {implicit session =>
        val logFile = findLogFile
        this.fileName = logFile.fileName

        self ! (logFile, schemaName)
      }

    case (LogFile(_, fileName, _), schemaName: String) =>
      var scn: Int = 0
      var preScn: Int = 0
      DB localTx {implicit session =>
        addLogFileToLogMiner(session, fileName)

        while (true) {
          Thread.sleep(1000L)
          val currentLogFile = findLogFile
          if (findLogFile.fileName != this.fileName) {
            this.fileName = currentLogFile.fileName
            addLogFileToLogMiner(session, this.fileName)
            scn = 0; preScn = 0
          }

          preScn = scn
          val resultSet = findLogMnrContents(session, scn, schemaName)
          if (resultSet.nonEmpty) {
            scn = resultSet.last.scn
          }

          if (scn > preScn) {
            resultSet.foreach { lmc =>
              println(lmc)
              kinesisActor ! lmc
            }
          }
        }
      }
  }

}

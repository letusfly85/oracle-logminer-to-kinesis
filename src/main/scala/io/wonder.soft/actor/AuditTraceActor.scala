package io.wonder.soft.actor

import akka.actor.Actor
import io.wonder.soft.lib.oracle.{LogFile, LogMinerWatcher}
import scalikejdbc.DB
import scalikejdbc.config.DBs

class AuditTraceActor extends Actor with LogMinerWatcher {
  DBs.setupAll()

  var fileName = ""
  var sleepCount = 0

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
          sleepCount += 1
          if (sleepCount > 3000 && findLogFile.fileName != this.fileName) {
            sleepCount = 0
            self ! schemaName
          }

          preScn = scn
          val resultSet = findLogMnrContents(session, scn, schemaName)
          if (resultSet.nonEmpty) {
            scn = resultSet.last.scn
          }

          if (scn > preScn) {
            resultSet.foreach(lmc => println(lmc))
            //TODO pass message to kinesis actor
          }
        }
      }
  }

}

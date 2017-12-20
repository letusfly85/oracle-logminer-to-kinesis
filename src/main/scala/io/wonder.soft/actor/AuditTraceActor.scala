package io.wonder.soft.actor

import akka.actor.Actor
import io.wonder.soft.lib.oracle.{LogFile, LogMinerWatcher}
import scalikejdbc.DB
import scalikejdbc.config.DBs

class AuditTraceActor extends Actor with LogMinerWatcher {
  DBs.setupAll()

  def receive  = {
    case tableName: String =>
      DB localTx {implicit session =>
        val logFile = findLogFile
        self ! (logFile, tableName)
      }

    case (LogFile(group, fileName, status), tableName: String) =>
      println(fileName)

      DB localTx {implicit session =>
        addLogFileToLogMiner(session, fileName)

        while (true) {
          Thread.sleep(1000L)
          findLogMnrContents(session, tableName).foreach(lmc => println(lmc))
        }
      }
  }

}

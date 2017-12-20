package io.wonder.soft.lib.oracle

import scalikejdbc._

case class LogFile(group: Int, fileName: String, status: String)
case class LogMnrContent(timestamp: String, operation: String, sqlRedo: String)
trait LogMinerWatcher {

  def findLogFile(implicit session: DBSession): LogFile = {
    val logFiles = sql"""
            SELECT
              L.GROUP#,
              F.MEMBER as logfile,
              L.STATUS as status
            FROM
              V$$LOG L,
              V$$LOGFILE F
            WHERE L.GROUP# = F.GROUP#
        """.map(rs => LogFile(rs.int(1), rs.string("logfile"), rs.string("status"))).list.apply

    logFiles.filter(lf => lf.status == "CURRENT").head
  }

  def addLogFileToLogMiner(implicit session: DBSession, logFileName: String): Unit = {
    sql"""
      begin dbms_logmnr.add_logfile(logfilename => $logFileName ,options => dbms_logmnr.new); end;
    """.execute.apply

    sql"""
       begin dbms_logmnr.start_logmnr(options => dbms_logmnr.dict_from_online_catalog); end;
      """.execute.apply
  }

  def findLogMnrContents(implicit session: DBSession, tableName: String): List[LogMnrContent] = {
    val result = sql"""
       select TIMESTAMP, OPERATION, SQL_REDO from v$$logmnr_contents where username = $tableName
    """.map(rs => LogMnrContent(rs.string("TIMESTAMP"), rs.string("OPERATION"), rs.string("SQL_REDO"))).list.apply

    result
  }

}

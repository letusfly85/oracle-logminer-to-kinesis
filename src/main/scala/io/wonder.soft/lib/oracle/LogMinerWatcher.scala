package io.wonder.soft.lib.oracle

import scalikejdbc._

case class LogFile(group: Int, fileName: String, status: String)
case class LogMnrContent(scn: Int, timestamp: String, operation: String, sqlRedo: String)
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

  // https://docs.oracle.com/cd/E60665_01/db112/REFRN/dynviews_2035.htm
  def findLogMnrContents(implicit session: DBSession, scn: Int, schemaName: String): List[LogMnrContent] = {

    val result = if (scn == 0) {
      sql"""
       select SCN, TIMESTAMP, OPERATION, SQL_REDO from v$$logmnr_contents where username = $schemaName
      """.map(rs => LogMnrContent(rs.int("SCN"), rs.string("TIMESTAMP"), rs.string("OPERATION"), rs.string("SQL_REDO"))).list.apply
      } else {
      sql"""
       select SCN, TIMESTAMP, OPERATION, SQL_REDO from v$$logmnr_contents where username = $schemaName and scn > $scn
      """.map(rs => LogMnrContent(rs.int("SCN"), rs.string("TIMESTAMP"), rs.string("OPERATION"), rs.string("SQL_REDO"))).list.apply
    }

    result
  }

}

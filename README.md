# Oracle LogMiner to Kinesis Stream

this is example program.

## Usage

### download Oracle JDBC Driver

You have to download Oracle JDBC Driver jar from Oracle Support HP.
And, put it to project root lib folder like below.

```
lib/ojdbc6.jar
```

### Start SBT Project

After that, you can start Akka Http server on your machine.

```sh
sbt run
```

### Start monitoring Your Schema

Then, you can call GET method to `localhost:8080` like below.
Query parameter `schemaName` is the name you want to trace.

```sh
curl -XGET localhost:8080/v1/log-files?schemaName=SAMPLE
```

### Can handle DDL, DML operations like below

For example, you execute like below queries. 

```sql
create table department (
  id number(10,0),
  name varchar2(200),
  description varchar2(2000),
  primary key(id)
);

insert into department (id, name, description) values (1, 'tax',      'count all amount of tax');
insert into department (id, name, description) values (2, 'sales',    'sales our products');
insert into department (id, name, description) values (3, 'engineer', 'deliver our services');
commit;
```

You can get like below.

```sql
LogMnrContent(651053,2017-12-20 15:38:55.0,DDL,create table department (
  id number(10,0),
  name varchar2(200),
  description varchar2(2000),
  primary key(id)
);)
LogMnrContent(651056,2017-12-20 15:38:55.0,UPDATE,update "SYS"."SEG$" set "TYPE#" = '5', "BLOCKS" = '8', "EXTENTS" = '1', "INIEXTS" = '8', "MINEXTS" = '1', "MAXEXTS" = '2147483645', "EXTSIZE" = '128', "EXTPCT" = '0', "USER#" = '48', "LISTS" = '0', "GROUPS" = '0', "BITMAPRANGES" = '2147483645', "CACHEHINT" = '0', "SCANHINT" = '0', "HWMINCR" = '20495', "SPARE1" = '4325633' where "FILE#" = '4' and "BLOCK#" = '362' and "TYPE#" = '3' and "TS#" = '4' and "BLOCKS" = '8' and "EXTENTS" = '1' and "INIEXTS" = '8' and "MINEXTS" = '1' and "MAXEXTS" = '2147483645' and "EXTSIZE" = '128' and "EXTPCT" = '0' and "USER#" = '48' and "LISTS" = '0' and "GROUPS" = '0' and "BITMAPRANGES" = '2147483645' and "CACHEHINT" = '0' and "SCANHINT" = '0' and "HWMINCR" = '20495' and "SPARE1" = '4325633' and ROWID = 'AAAAAIAABAAAHJ0AAD';)
LogMnrContent(651057,2017-12-20 15:38:55.0,UPDATE,update "SYS"."SEG$" set "TYPE#" = '6', "BLOCKS" = '8', "EXTENTS" = '1', "INIEXTS" = '8', "MINEXTS" = '1', "MAXEXTS" = '2147483645', "EXTSIZE" = '128', "EXTPCT" = '0', "USER#" = '48', "LISTS" = '0', "GROUPS" = '0', "BITMAPRANGES" = '2147483645', "CACHEHINT" = '0', "SCANHINT" = '0', "HWMINCR" = '20496', "SPARE1" = '4325633' where "FILE#" = '4' and "BLOCK#" = '370' and "TYPE#" = '3' and "TS#" = '4' and "BLOCKS" = '8' and "EXTENTS" = '1' and "INIEXTS" = '8' and "MINEXTS" = '1' and "MAXEXTS" = '2147483645' and "EXTSIZE" = '128' and "EXTPCT" = '0' and "USER#" = '48' and "LISTS" = '0' and "GROUPS" = '0' and "BITMAPRANGES" = '2147483645' and "CACHEHINT" = '0' and "SCANHINT" = '0' and "HWMINCR" = '20496' and "SPARE1" = '4325761' and ROWID = 'AAAAAIAABAAAHJ0AAE';)
LogMnrContent(651058,2017-12-20 15:38:55.0,COMMIT,commit;)
LogMnrContent(651059,2017-12-20 15:38:55.0,START,set transaction read write;)
LogMnrContent(651060,2017-12-20 15:38:55.0,COMMIT,commit;)
LogMnrContent(651061,2017-12-20 15:38:56.0,START,set transaction read write;)
LogMnrContent(651061,2017-12-20 15:38:56.0,INSERT,insert into "SAMPLE"."DEPARTMENT"("ID","NAME","DESCRIPTION") values ('1','tax','count all amount of tax');)
LogMnrContent(651061,2017-12-20 15:38:56.0,INSERT,insert into "SAMPLE"."DEPARTMENT"("ID","NAME","DESCRIPTION") values ('2','sales','sales our products');)
LogMnrContent(651061,2017-12-20 15:38:56.0,INSERT,insert into "SAMPLE"."DEPARTMENT"("ID","NAME","DESCRIPTION") values ('3','engineer','deliver our services');)
LogMnrContent(651062,2017-12-20 15:38:56.0,COMMIT,commit;)
```

@rem **************************************************
@rem 8/20/20
@rem Satya
@rem 
@rem what for?
@rem ************
@rem To run TestCollection
@rem 
@rem **************************************************


@rem **************************************************
@rem These you set for your env
@rem **************************************************
set repo_root=C:\satya\data\code\aspire-integration-repo
set jdk_path=C:\satya\i\jdk12


@rem **************************************************
@rem Derived
@rem **************************************************
set aspire_properties=%repo_root%\test-data\aspire-batch\aspire.properties
set ai_jar=%repo_root%\output-jars\aspire_integration_jdk12.jar

C:\satya\data\code\aspire-integration-repo\jars


set sqlserver_jar=%repo_root%\jars\mssql-jdbc-8.4.1.jre11.jar
set csv_jar=%repo_root%\jars\commons-csv-1.8.jar
set json_jar=%repo_root%\jars\gson-2.8.6.jar

set classpath=%ai_jar%;%sqlserver_jar%;%csv_jar%;%json_jar%;

%jdk_path%\bin\java com.ai.test.TestCollection %aspire_properties%
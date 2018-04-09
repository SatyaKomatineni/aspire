package com.ai.db;

import com.ai.data.*;
import java.sql.*;
import java.util.*;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.*;

public class DBUtils {

   public static IMetaData createMetaData(ResultSetMetaData rsMetaData)
      throws java.sql.SQLException
   {
         Vector m_columnNames = new Vector();
         if (rsMetaData == null)
         {
            AppObjects.log("Error: Null meta data ");
         }
         for(int i=1;i <= rsMetaData.getColumnCount();i++)
         {
            m_columnNames.addElement(rsMetaData.getColumnName(i));
         }                      
         return new VectorMetaData(m_columnNames );
   }
   
   public static Connection getConnectionFromAPool(String datasourceName)
   throws DBException
   {
	   return SWIConnectionManager.getConnection(datasourceName);
   }
   
   public static void putConnection(Connection con)
   throws DBException
   {
	   SWIConnectionManager.putConnection(con);
   }
   public static Connection getReservedJDBConnection(Map arguments)
   {
	   return (Connection)arguments.get("aspire.reserved.jdbc_connection");
   }
   public static void putReservedJDBConnection(Map arguments, Connection con)
   {
	   arguments.put("aspire.reserved.jdbc_connection",con);
   }
   public static void transferConnection(Map srcMap, Map targetMap)
   {
	  Connection con = DBUtils.getReservedJDBConnection(srcMap);
	  DBUtils.putReservedJDBConnection(targetMap, con);
   }
}//eof-class 
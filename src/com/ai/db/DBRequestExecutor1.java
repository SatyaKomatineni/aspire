/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.db;

import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.sql.*;
import java.util.*;
import com.ai.common.*;

// Please move this object to com.ai.common or db
// 
import com.ai.application.interfaces.RequestExecutorResponse;

public class DBRequestExecutor1 extends DBBaseJavaProcedure
{
   public Object executeProcedure(Connection con
                                          ,boolean bConnectionCreator
                                           ,String requestName
                                           , Hashtable arguments )
         throws DBException, SQLException
   {
      try 
      {
         String statementString
         = AppObjects.getValue(requestName + ".stmt");
         
         AppObjects.trace(this,"Arguments in to the statement are : %1s",arguments );
         
        String modifiedStatementString = getArgSubstitutor().substitute(statementString
                                                            ,arguments);
         AppObjects.trace(this,"statement to execute : %1s", modifiedStatementString );
         
         String statementType = AppObjects.getIConfig().getValue(requestName + ".query_type","");
         if (statementType.equals("update"))
         {
            return execUpdate(con, bConnectionCreator, modifiedStatementString);
         }
         else
         {
            return execQuery(con, bConnectionCreator, modifiedStatementString );
         }
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         throw new DBException("Config exception",x);
      }         
   }         
   
   DBRSCollection1 execQuery( Connection con, boolean bConnectionCreator, String statementString )
      throws java.sql.SQLException
   {
         Statement stmt =null;
         ResultSet rs = null; 

         try
         {
            stmt = con.createStatement();
            rs = stmt.executeQuery(statementString);
            return new DBRSCollection1(con, bConnectionCreator, stmt, rs);
         }
         catch(java.sql.SQLException x)
         {
            AppObjects.log("db: closing statement and result set due to an exception ");
            if ( stmt != null) stmt.close();
            if (rs != null) rs.close();
            throw x;
         }
   }      
   RequestExecutorResponse execUpdate( Connection con, boolean bConnectionCreator, String statementString )
      throws java.sql.SQLException
   {
      Statement stmt = null;
      try 
      {
         stmt = con.createStatement();
         AppObjects.trace(this,"Executing : %1s", statementString );
         int numberOfRowsUpdated = stmt.executeUpdate(statementString);
         AppObjects.info(this,"Number of rows updated : %1s",numberOfRowsUpdated );
         return new RequestExecutorResponse(true);
      }
      finally
      {
         if (stmt != null) stmt.close();
      }         
   }      
} 

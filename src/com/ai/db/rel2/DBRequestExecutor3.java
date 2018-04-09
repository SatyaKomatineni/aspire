/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.db.rel2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import com.ai.application.interfaces.RequestExecutorResponse;
import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;

/**
 * Uses DBRSCollection2 as the returned collection
 * Will replace DBRequestExecutor1 for future references
 */
public class DBRequestExecutor3 extends DBBaseJavaProcedure3
{
   public Object executeProcedure(Connection con, 
		String requestName, 
		Hashtable arguments)
        throws DBException, SQLException
   {
      try 
      {
         String statementString
         = AppObjects.getValue(requestName + ".stmt");
         
         AppObjects.log("Arguments in to the statement are : " + arguments );
         
        String modifiedStatementString = getArgSubstitutor().substitute(statementString
                                                            ,arguments);
         AppObjects.log("statement to execute : " + modifiedStatementString );
         
         String statementType = AppObjects.getIConfig().getValue(requestName + ".query_type","");
         if (statementType.equals("update"))
         {
            return execUpdate(con, modifiedStatementString);
         }
         else
         {
            return execQuery(con, modifiedStatementString );
         }
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         throw new DBException("Config exception",x);
      }         
   }         
   
   DBRSCollection3 execQuery( Connection con, String statementString )
      throws java.sql.SQLException
   {
         Statement stmt =null;
         ResultSet rs = null; 

         try
         {
            stmt = con.createStatement();
            rs = stmt.executeQuery(statementString);
            return new DBRSCollection3(con, stmt, rs);
         }
         catch(java.sql.SQLException x)
         {
            AppObjects.log("db: closing statement and result set due to an exception ");
            if ( stmt != null) stmt.close();
            if (rs != null) rs.close();
            throw x;
         }
   }      
   RequestExecutorResponse execUpdate( Connection con, String statementString )
      throws java.sql.SQLException
   {
      Statement stmt = null;
      try 
      {
         stmt = con.createStatement();
         AppObjects.log("Executing : " + statementString );
         int numberOfRowsUpdated = stmt.executeUpdate(statementString);
         AppObjects.log("Number of rows updated : " + numberOfRowsUpdated );
         return new RequestExecutorResponse(true);
      }
      finally
      {
         if (stmt != null) stmt.close();
      }         
   }      
} 

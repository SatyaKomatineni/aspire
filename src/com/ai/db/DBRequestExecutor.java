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

public class DBRequestExecutor implements ICreator
{
   private static AArgSubstitutor ms_argSubstitutor = new CArgSubstitutor();
  public DBRequestExecutor()
  {
  }
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      Connection con = null;
      try
      {
         // got a call to do my job
         String dbName =
         AppObjects.getValue(requestName + ".db" );
         // get the sql statement
         String statementString
         = AppObjects.getValue(requestName + ".stmt");
         // transform the strings if there are any arguments to be passed
         if (args != null)
         {
            // arguments passed
            // The assumptions is that this is a vector of or a hashtable 
            if (args instanceof Vector)
            {
               statementString = ms_argSubstitutor.substitute(statementString
                                                         ,(Vector)args);
            }
            else
            {
               Hashtable parameters = (Hashtable)args;
                for (Enumeration e=parameters.keys();e.hasMoreElements();)
                {
                  String key = (String)e.nextElement();
                  AppObjects.log( key + ":" + parameters.get(key) );
                }
               // instance of hashtable
               statementString = ms_argSubstitutor.substitute(statementString
                                                            ,(Hashtable)args );
            }
         }   
         AppObjects.log("statement to execute : " + statementString );
         IConnectionManager connectionManager = 
            (IConnectionManager)AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null);
         con = connectionManager.getConnection(dbName);
         String statementType = AppObjects.getIConfig().getValue(requestName + ".query_type","");
         if (statementType.equals("update"))
         {
            return execUpdate(con, statementString);
         }
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(statementString);
         return new DBRSCollection1(con,true, stmt, rs);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         throw new RequestExecutionException("Config exception",x);
      }         
      catch(com.ai.db.DBException x)
      {
         throw new RequestExecutionException("Could not obtain a connection",x);
      }         
      catch(java.sql.SQLException x)
      {
         if (con != null )
         {
            try {con.close();}
            catch(java.sql.SQLException y) { AppObjects.log(y); }
         }
         throw new RequestExecutionException("Could not get a resultset",x);
      }
   }        
   RequestExecutorResponse execUpdate( Connection con, String statementString )
      throws java.sql.SQLException
         ,com.ai.application.interfaces.RequestExecutionException
         ,com.ai.db.DBException
   {
      Statement stmt = con.createStatement();
      AppObjects.log("Executing : " + statementString );
      int numberOfRowsUpdated = stmt.executeUpdate(statementString);
      AppObjects.log("Number of rows updated : " + numberOfRowsUpdated );
      stmt.close();
      IConnectionManager  conMgr = 
         (IConnectionManager)AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null);
      conMgr.putConnection(con);      
      return new RequestExecutorResponse(true);
   }      
} 
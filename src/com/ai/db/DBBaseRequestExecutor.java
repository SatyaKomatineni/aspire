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

public abstract class DBBaseRequestExecutor implements ICreator
{
  private static AArgSubstitutor ms_argSubstitutor = new CArgSubstitutor();

   private Vector getStatementVector(String requestName)
      throws ConfigException
   {
         Vector statementVector = new Vector();
         
         String numOfSqlStatements = AppObjects.getIConfig().getValue(requestName + ".numOfStatements","0");
         int iNumOfSqlStatements = Integer.valueOf(numOfSqlStatements).intValue();
         if (iNumOfSqlStatements == 0)
         {
            String statementString = AppObjects.getValue(requestName + ".stmt");
            statementVector.addElement(statementString);
         }
         else
         {
            for (int i=1;i<=iNumOfSqlStatements;i++)
            {
               String statementString = AppObjects.getValue(requestName + ".stmt." + i);
               statementVector.addElement(statementString);
            }
         }
         return statementVector;      
   }
   private Vector getModifiedStatementVector(Vector statementVector, Object args)
   {
         Vector modifiedStatementVector = new Vector();   
         if (args != null)
         {
            // arguments passed
            // The assumptions is that this is a vector of or a hashtable 
            if (args instanceof Vector)
            {
               for(Enumeration e=statementVector.elements();e.hasMoreElements();)
               {
                  String statementString = ms_argSubstitutor.substitute((String)e.nextElement()
                                                            ,(Vector)args);
                  modifiedStatementVector.addElement(statementString);                                                            
               }                                                            
            }
            else
            {
               Hashtable parameters = (Hashtable)args;
                for (Enumeration e=parameters.keys();e.hasMoreElements();)
                {
                  String key = (String)e.nextElement();
                  AppObjects.info(this,"db: %1s : %2s", key, parameters.get(key) );
                }
                
               for(Enumeration e=statementVector.elements();e.hasMoreElements();)
               {
                  String statementString = ms_argSubstitutor.substitute((String)e.nextElement()
                                                            ,(Hashtable)args );
                  modifiedStatementVector.addElement(statementString);                                                            
               }                                                            
            }
         }   
         return modifiedStatementVector;
   }
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      try
      {
         // got a call to do my job
         String dbName = AppObjects.getValue(requestName + ".db" );
         
         // get all the sql statements
         Vector statementVector = getStatementVector(requestName);
         Vector modifiedStatementVector = getModifiedStatementVector(statementVector,
                                                                  args);
         
         IConnectionManager connectionManager = 
            (IConnectionManager)AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null);
         Connection con = connectionManager.getConnection(dbName);
         return executeStatements(con,args,statementVector,modifiedStatementVector);
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
         throw new RequestExecutionException("SQLException",x);
      }         
      
   }        
   public abstract Object executeStatements(Connection con,
                                       Object args,
                                       Vector statementVector,
                                       Vector modifiedStatementVector )
                                       throws java.sql.SQLException;
} 

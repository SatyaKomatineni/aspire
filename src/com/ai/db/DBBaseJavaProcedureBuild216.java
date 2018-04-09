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
import com.ai.data.IDataCollection;
import com.ai.servlets.AspireConstants;

/**
 * Do not use this. This is a quick back up
 * Next build will have this class cleaned up
 * 
 * Responsibility:
 *
 * 1. Act as a base class for all database related executors
 *
 * What it does for the derived classes
 ***************************************
 * 1. Collect arguments from Aspire into a hashtable
 * 2. Retrieve a connection object for database requests
 * 3. Return the connection object to the connection pool on return
 * 4. Commit/rollback if the request is an update
 * 5. On sql exception close the connection( if this is the owner )
 *       and return it to the pool
 *
 * If this is not intended behaviour, write your own request executor by
 * deriving from ICreator.
 *
 *
 * Derived class obligations
 ****************************
 * 1.Implement the abstract method executeProcedure
 * 2. Have the following arguments passed in:
 *    1. con - Will be given a connection obtained from the database
 *    2. bConnectionCreator - Whether the connection has been created or passed through
 *    3. requestName - Name of the request in the properties file
 *    4. arguments - a hashtable of arguments to the procedure
 * 3. Do not interrupt any SQLExceptions
 * 4. Any other exception, convert it to a DataException
 * 5. The argument 'bConnectionCreator' has significance only when you want your
 *    executor to be executed by DBPreTranslateArgsMultiRequestExecutor. Otherwise this
 *    value is always going to be true.
 * 6. Should always return one of the two objects
 *    1. IDataCollection for non-updates
 *    2. RequestExecutorResponse for update types
 *
 *
 * case1 - Let the base class do the transaction management
 * *********************************************************
 * 1. If the derived procedure is only doing selects use this style
 * 2. If the derived procedure is manipulating data that can be encapsulated in
 *    one commit, use this style
 * 3. If the derived procedure is manipulating that is too large for 1 single commit
 *    don't use this style
 * 4. Mention the query type to be update if you want the base procedure to do the commits
 *
 * case2 - Take over the connection management yourself
 ********************************************************
 *
 * 1. If the derived procedure is manipulating that is too large for 1 single commit
 *    use this style
 * 2. Don't mention the query type as update
 *
 * 3. If you need to make use of any of the DBRequestExecutor1 type of support from with in
 *    this derived executor you need to pass this connection as one of the arguments to
 *    these procedures. This ensures that these other executors will execute on the same
 *    connection allowing for commits and rollbacks. Here is how this can be accomplished:
 *
 *    Hashtable args;
 *    args.put("aspire.reserved.jdbc_connection",con);
 *    args.put(...other args..)
 *    ..pass args to the other request executors, that are derived from DBBaseJavaProcedure
 *
 */
public abstract class DBBaseJavaProcedureBuild216 implements ICreator
{
   public abstract Object executeProcedure(Connection con, boolean bConnectionCreator, String requestName, Hashtable arguments)
         throws DBException, SQLException;

   private static AArgSubstitutor ms_argSubstitutor = new SQLArgSubstitutor();

   public AArgSubstitutor getArgSubstitutor() { return ms_argSubstitutor; }

   public IConnectionManager getConnectionManager()
      throws RequestExecutionException
   {
      return (IConnectionManager)AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null);
   }

   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
         boolean bConnectionCreator = true;
         boolean bCommit = false;
         Connection con = null;
         String queryType = "query";
         Object executeProcReturnObj  = null;
      try
      {
         Hashtable parameters = null;
         // transform the strings if there are any arguments to be passed
         // args is always a vector
         // args(1) is a hashtable of parameters
         // args(2) is connection if it has to be used
         if (args != null)
         {
            if (args instanceof Vector)
            {
                  Vector argumentVector = (Vector)args;
                  if (argumentVector.elementAt(0) != null)
                  {
                      parameters = (Hashtable)argumentVector.elementAt(0);
                      for (Enumeration e=parameters.keys();e.hasMoreElements();)
                      {
                        String key = (String)e.nextElement();
                        AppObjects.log( key + ":" + parameters.get(key) );
                      }
                  }
                  if (argumentVector.size() > 1)
                  {
                     con = (Connection)argumentVector.elementAt(1);
                     bConnectionCreator = false;
                  }
            } // vector arguments
            else
            {
               // Hash table of arguments
               // backward compatiability
               parameters = (Hashtable)args;
            }
         }
         if (con == null)
         {
            // Try to get it from the args first
             Object obj = null;
             if (parameters != null)
             {
               obj = parameters.get("aspire.reserved.jdbc_connection");
             }
             if (obj != null)
             {
               con = (Connection)obj;
               AppObjects.log("db: Connection received through parameters");
               bConnectionCreator = false;
             }
             else
             {
               String dbName =
               AppObjects.getValueForRequestUsingSubstitution("db",requestName,parameters);
//               AppObjects.getValue(requestName + ".db" );
               // Go and get the connection for this database
               con = getConnectionManager().getConnection(dbName);
               bConnectionCreator = true;
             }
         }
         queryType = AppObjects.getIConfig().getValue(requestName + ".query_type","query");
         if (bConnectionCreator == true )
         {
            if (queryType.equals("update"))
            {
               bCommit = true;
            }
         }
         if (bCommit == true )
         {
            AppObjects.log("db: commit recognized");
            con.setAutoCommit(false);
         }
         //************************************
         // Execute the real derived procedure
         //************************************
         udpateDefaultArguments(requestName,parameters);
         String transferOwnerShipFlag =
            (String)parameters.get(AspireConstants.CONNECTION_OWNERSHIP_TRANSFER_FLAG_PARAM_KEY);
         if (transferOwnerShipFlag != null)
         {
            if (transferOwnerShipFlag.equals(AspireConstants.BOOL_TRUE))
            {
              // Transfer ownership to the collection
              // Remove the parameter so that it won't be propagated
              parameters.remove(AspireConstants.CONNECTION_OWNERSHIP_TRANSFER_FLAG_PARAM_KEY);
              executeProcReturnObj = executeProcedure(con, true, requestName, parameters);
            }
            else
            {
               AppObjects.log("db: Transfer of connection owner ship requested");
               executeProcReturnObj = executeProcedure(con, bConnectionCreator, requestName, parameters);
            }
         }
         else
         {
            executeProcReturnObj = executeProcedure(con, bConnectionCreator, requestName, parameters);
         }
         if (bCommit == true)
         {
            AppObjects.log("db: Transaction being commited for request :" + requestName);
            con.commit();
         }
         return executeProcReturnObj;
      }
      catch(SQLException x)
      {
         if (bCommit == true)
         {
             AppObjects.log("db: Rolling back transaction because of an error");
             AppObjects.log(x);
             try {con.rollback();} catch(SQLException y){AppObjects.log(y);}
         }
         if (bConnectionCreator == true )
         {
            // close the collection only if you are the owner
            try {con.close();}
            catch(SQLException y){AppObjects.log(y);}
         }
         throw new RequestExecutionException("db: SQL exception",x);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         throw new RequestExecutionException("Config exception",x);
      }
      catch(com.ai.db.DBException x)
      {
         AppObjects.log("db: Could not execute the request for : " + requestName );
         if (bCommit == true )
         {
            AppObjects.log("db: Connection being rolled back as an exception is detected ");
            try { con.rollback(); }
            catch (SQLException y)
            {
               String msg = "db: Could not rollback transaction : " + requestName;
               AppObjects.log( msg );
               AppObjects.log(y);
            }
         }
         throw new RequestExecutionException("A database error is being reported",x);
      }
      // should be the same logic as above
      catch(RuntimeException t)
      {
         AppObjects.log("db: a runtime exception detected " + t.getClass().getName());
         if (bCommit == true )
         {
            AppObjects.log("db: Connection being rolled back as an exception is detected ");
            try { con.rollback(); }
            catch (SQLException y)
            {
               String msg = "db: Could not rollback transaction : " + requestName;
               AppObjects.log( msg );
               AppObjects.log(t);
            }
         }
         throw t;
      }
      finally
      {
         if (con != null)
         {
            if (bConnectionCreator == true )
            {
               boolean conClosed = false;
               try{conClosed = con.isClosed();} catch(java.sql.SQLException x){AppObjects.log(x);}
               if (bCommit == true && !conClosed)
               {
                  try { con.setAutoCommit(true); }
                  catch (SQLException x)
                  {
                     AppObjects.log("db: Could not set autocommit to true");
                     AppObjects.log(x);
                     // throw new RequestExecutionException ("db: Could not set autocommit to true", x);
                  }
               }
               if ((executeProcReturnObj instanceof IDataCollection) == false)
               {
                  // if what has been got is not a collection
                  // we are done with the connection
                   try { getConnectionManager().putConnection(con); }
                   catch (DBException x)
                   {
                     AppObjects.log("db: Could not return connection");
                     AppObjects.log(x);
                   }
               }
            }
         }
      } // end of finally
   }  // end of executeProcedure
   private void udpateDefaultArguments(String requestName, Hashtable params)
   {
      String defaultKeys = AppObjects.getIConfig().getValue(requestName + ".defaultFields",null);
      if (defaultKeys == null)
      {
        return;
      }
      // Default keys specified
      AppObjects.log("Info: Default keys available");
      Vector vDefKeys = Tokenizer.tokenize(defaultKeys,",");
      for(Enumeration e=vDefKeys.elements();e.hasMoreElements();)
      {
        String curKey = (String)e.nextElement();
        // see if the key exists
        String keyValue = (String)params.get(curKey.toLowerCase());

        if (keyValue == null)
        {
          AppObjects.log("Info: Applying default value for key " + curKey);
          String defaultValue=AppObjects.getIConfig().getValue(requestName + "." + curKey,"");
          params.put(curKey.toLowerCase(),defaultValue);
          continue;
        }
        if (keyValue.trim().equals(""))
        {
          AppObjects.log("Info: Applying default value for key " + curKey);
          String defaultValue=AppObjects.getIConfig().getValue(requestName + "." + curKey,"");
          params.put(curKey.toLowerCase(),defaultValue);
          continue;
        }
      }
      return;
   }
}

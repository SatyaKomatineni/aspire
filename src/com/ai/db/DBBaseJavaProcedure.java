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
 * 11/15/2015
 * ***********************
 * Uses SubstitutorUtils to get the SQLArgSubstitutor 
 * Change how this class gets the SQLArgSubstitutor.
 * Use a strategy swap approach to get a SQLArgSubstitutor at run time
 * Allows backward compatibility while allowing future variations
 * Set the following in the config file
 * 
 * request.aspire.substitutions.sqlArgSubstitutor.className=\
 * com.ai.common.SQLArgSubstitutor2WithArgValidation
 * 
 * Once tested replace the default with 
 * com.ai.common.SQLArgSubstitutor1
 * 
 * The above is default class. A future class can be used.
 * @see SQLArgSubstitutor
 * @see SQLArgSubstitutor1
 * @see SQLArgSubstitutor2Updated
 * @see SQLArgSubstitutor2WithArgValidation
 * 
 */
public abstract class DBBaseJavaProcedure implements ICreator
{
   public abstract Object executeProcedure(Connection con, boolean bConnectionCreator, String requestName, Hashtable arguments)
         throws DBException, SQLException;

   //Use SQLArgSubstitutor to strategize which version to get
   public AArgSubstitutor getArgSubstitutor() 
   { 
	   return SubstitutorUtils.getSQLArgSubstitutor();
   }

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
         boolean bExceptionDetected = false;
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
                        AppObjects.trace( this,"Argument Key %1s : %2s", key, parameters.get(key) );
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
            AppObjects.log("db: commit recognized.Setting auto commit to false");
            con.setAutoCommit(false);
         }
         AppObjects.info(this,"db: autocommit status:%1s",con.getAutoCommit());
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
      //Deal with exceptions
      catch(SQLException x)
      {
      	bExceptionDetected = true;
        throw new RequestExecutionException("db: SQL exception",x);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
      	 bExceptionDetected = true;
         throw new RequestExecutionException("Config exception",x);
      }
      catch(com.ai.db.DBException x)
      {
      	bExceptionDetected = true;
        throw new RequestExecutionException("A database error is being reported",x);
      }
      // should be the same logic as above
      catch(RuntimeException t)
      {
      	bExceptionDetected = true;
        throw t;
      }
      //just in case
      catch(RequestExecutionException x)
      {
      	bExceptionDetected = true;
        throw x;
      }
      finally
      {
         if (con != null)
         {
         	//Deal with exceptions first
         	if (bExceptionDetected == true)
         	{
         		//Exception detected
         		AppObjects.info(this,"db: Exception detected. Attempting conditional rollback.");
         		this.conditionalRollback(con,bCommit,requestName);
         	}
         	
         	//Set auto commit to true if you are the connection owner
            if (bConnectionCreator == true )
            {
               boolean conClosed = false;
               try{conClosed = con.isClosed();} catch(java.sql.SQLException x){AppObjects.log(x);}
               if (bCommit == true && !conClosed)
               {
               	  AppObjects.info(this,"db: Setting the auto commit to true on:%1s", con);
                  try { con.setAutoCommit(true); }
                  catch (SQLException x)
                  {
                     AppObjects.log("db: Could not set autocommit to true");
                     AppObjects.log(x);
                  }
               }
               
               // if what has been got is not a collection
               // we are done with the connection
               // Return it to the pool
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
               }//return to the pool
            }//if connection creator
            
            //Report on the auto commit status
           	AppObjects.info(this,"db: auto commit status leaving the finally block:%1s", getAutoCommitStatus(con));
         }//if valid connection
      }// end of finally
   }// end of executeProcedure
   
   private String getAutoCommitStatus(Connection con)
   {
   	  try
	  {
   	  	boolean b = con.getAutoCommit();
   	  	return b ? "true":"false";
	  }
   	  catch(SQLException x)
	  {
   	  	AppObjects.log("Error: Could not get auto commit",x);
   	  	return "unknown";
	  }
   }
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
          AppObjects.info(this,"Applying default value for key %1s",curKey);
          String defaultValue=AppObjects.getIConfig().getValue(requestName + "." + curKey,"");
          params.put(curKey.toLowerCase(),defaultValue);
          continue;
        }
        if (keyValue.trim().equals(""))
        {
          AppObjects.info(this,"Applying default value for key %1s",curKey);
          String defaultValue=AppObjects.getIConfig().getValue(requestName + "." + curKey,"");
          params.put(curKey.toLowerCase(),defaultValue);
          continue;
        }
      }
      return;
   }//eof-function
   /**
    * Will rollback only if commit is true
    * @param con
    * @param bCommit
    * @param requestName
    */
   private void conditionalRollback(Connection con, boolean bCommit, String requestName)
   {
	    AppObjects.info(this,"db: Could not execute the request for : %1s",requestName );
	    if (bCommit == false)
	    {
	    	AppObjects.info(this,"db: I am not the owner. No rollback.");
	    	return;
	    }
	    AppObjects.info(this,"db: Connection being rolled back as an exception is detected ");
	    try { con.rollback(); }
	    catch (SQLException y)
	    {
	      String msg = "Error: db: Could not rollback transaction : " + requestName;
	      AppObjects.log(msg,y);
	    }
   }//eof-function
   
}//eof-class

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.db.cpjndi;

//******************************************************************************
// Java based imports
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;
import com.ai.db.IConnectionManager;
//******************************************************************************
import com.ai.db.events.SWIConnectionEvents;

/**
 * See the previous managers for more docs
 * The intent
 * *************
 * Provide preloading of connections
 * 
 * External/Additional classes used
 * *********************************
 * 1. IConnectionManager - Primary interface
 * 2. ICreator  - Factory service
 * 3. IScheduler - Scheduling for regular background tasks
 * 4. IScheduleTask - Related to scheduling again
 *
 * @see com.ai.db.IConnectionManager
 * @see com.ai.application.interfaces.ICreator
 * @see com.ai.scheduler.IScheduler
 */      
public class ConnectionPoolConnectionManager5 implements IConnectionManager
 {
   // key data source name, value: data source connection pool object
   // Used to locate the connection pool object for a given data source
   // hashtable<string:datasource_name,SingleDataSourceConnectionPool:connection pool object>
   private HashMap m_connectionPools = new HashMap();
   
   /**
    * Default constructor
    */
   public ConnectionPoolConnectionManager5(){}
  
   /**
    * Get a connection from the pool specified by the data source name.
    */
   public Connection getConnection(String inDataSourceName ) 
                             throws DBException
   {
   		try
		{
   			String dataSourceName = inDataSourceName.toLowerCase();
   			// log the message
   			AppObjects.trace(this,"cp: Requesting a connection from data source %1s", dataSourceName );
          
   			// 	request the pool and get connection
   			DataSource pool = getConnectionPoolFor( dataSourceName );
   			Connection con = pool.getConnection();
   			
   	       //raise the event
   	       SWIConnectionEvents.onGetConnection(con);
   	       return con;
		}
   		catch(SQLException x)
		{
   			throw new DBException("Error: Getting a connection",x);
		}
   		catch(NamingException x)
		{
   			throw new DBException("Error: creating a data source",x);
		}
   }
                             
   /**
    * Return connection back to the pool.
    */
   public void putConnection(Connection con)
                             throws DBException
   {
   		try
		{
   	      //raise the event
   	      SWIConnectionEvents.onPutConnection(con);
          AppObjects.trace(this,"cp: Returning connection");
          con.close();
		}
   		catch(SQLException x)
		{
   			throw new DBException("Error: Problem closing a connection",x);
		}
   }
   
   /**
    * Get the connection pool for a given data source name.
    * Data source name is a symbolic name representing the jdbc driver, user name, password etc.
    */
   private  DataSource getConnectionPoolFor( String dataSourceName )
   throws DBException, NamingException
   {
       Object obj = m_connectionPools.get(dataSourceName);
       if (obj != null)
       {
          // connection pool for this data source found.
          // return it.
          return (DataSource)obj;
       }
       
       // Attempt to create a connection pool for this data source
       // issue an error saying that connection pool not found
       AppObjects.trace(this,"cp: Connection pool not found for data source :%1s", dataSourceName );

       synchronized( this )
       {
         // Some other might have created this already.
         // So do a repeat check
         obj = m_connectionPools.get(dataSourceName);
         if (obj != null) 
         {
            return (DataSource)obj;
         }

         // No other thread has created this pool                
         obj = createDataSourceFor(dataSourceName );

         // add it to the available data pools
         m_connectionPools.put(dataSourceName, obj);
       } // end of synchronized block
                
       AppObjects.trace(this,"cp: Connection pool created for data source :%1s", dataSourceName );
       return (DataSource)obj;
   }
   
   private DataSource createDataSourceFor(String dataSourceName)throws NamingException
   {
	   Context envCtx = (Context) new InitialContext().lookup("java:comp/env");
	    // Look up our data source
	   DataSource  ds = (DataSource) envCtx.lookup("jdbc/" + dataSourceName);
	   return ds;
   }
   
} // end of class ConnectionPoolConnectionManager



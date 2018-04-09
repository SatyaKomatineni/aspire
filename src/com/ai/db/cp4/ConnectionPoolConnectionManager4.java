/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.db.cp4;

//******************************************************************************
// Java based imports
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;
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
public class ConnectionPoolConnectionManager4 implements IConnectionManager, ICreator, IInitializable
 {
   // key data source name, value: data source connection pool object
   // Used to locate the connection pool object for a given data source
   // hashtable<string:datasource_name,SingleDataSourceConnectionPool:connection pool object>
   private Hashtable m_connectionPools = new Hashtable();
   
   // key : connection, value: data source connection pool
   // hashtable<Connection:sql connection
   //        ,SingleDataSourceConnectionPool:connection pool to which the connection belongs>
   // Used to locate the connection pool given a connection 
   // Used in returning the connection to the correct connection pool
   private Hashtable m_connectionToPoolMapping = new Hashtable();
   
   //loaded by initialize
   private List m_preloadDataSourceList = null;
   
   /**
    * Used by the Factory to instantiate this object from the properties file.
    * @see com.ai.application.interfaces.IFactory
    * @see com.ai.application.defaultpkg.IFactory
    * Does not expect any arguments from the factory
    */
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
   //from initializable
   public void initialize(String requestName)
   {
   		try
		{
	   		//remember the data sources
	   		String dataSourcesString = 
	   			AppObjects.getValue(requestName + ".preload-datasources",null);
	   		if (dataSourcesString == null)
	   		{
	   			AppObjects.trace(this,"No data sources to preload. Initialization complete");
	   			return;
	   		}
	   		else
	   		{
	   			AppObjects.trace(this,"the preload data source string is:%1s",dataSourcesString);
	   		}
	   		Vector dataSourceList = Tokenizer.tokenize(dataSourcesString,",");
	   		this.m_preloadDataSourceList = dataSourceList;
	   		this.preloadDataSources();
		}
   		catch(DBException x)
		{
   			throw new RuntimeException("Error:Not able to preload data source connections",x);
		}
   }
   
   
   public void preloadDataSources() throws DBException
   {
   		AppObjects.trace(this,"Going to preload for :%1s", this.m_preloadDataSourceList);
   		Iterator dsi = this.m_preloadDataSourceList.iterator();
   		while(dsi.hasNext())
   		{
   			String ds = (String)dsi.next();
   			this.preloadDataSource(ds);
   		}
   }
   private void preloadDataSource(String dsname) throws DBException
   {
	   	SingleDataSourceConnectionPool sdcp = 
	   		this.getConnectionPoolFor(dsname);
	   	sdcp.preloadDataSource();
   }
   
      
   /**
    * Default constructor
    */
   public ConnectionPoolConnectionManager4()
   {
   }
  
   /**
    * Get a connection from the pool specified by the data source name.
    */
   public Connection getConnection(String dataSourceName ) 
                             throws DBException
   {
      // log the message
       AppObjects.trace(this,"cp: Requesting a connection from data source %1s", dataSourceName );
          
       // request the pool and get connection
       SingleDataSourceConnectionPool pool = getConnectionPoolFor( dataSourceName );
       Connection con = pool.getConnection();
          
       // remember what pool does this connection belongs to
       // so that the connection can be returned to the respective pool
       m_connectionToPoolMapping.put(con,pool);
       
       //raise the event
       SWIConnectionEvents.onGetConnection(con);
       
       return con;
   }
                             
   /**
    * Return connection back to the pool.
    */
   public void putConnection(Connection con)
                             throws DBException
   {
      //raise the event
      SWIConnectionEvents.onPutConnection(con);
	   
      SingleDataSourceConnectionPool pool = 
         (SingleDataSourceConnectionPool)m_connectionToPoolMapping.remove(con);
         
      if (pool == null)
      { 
        // pool not found. Abnormal. Log and let it go.
        AppObjects.trace(this,"cp: No pool found to return the connection ");
        return;
      }
        
      AppObjects.trace(this,"cp: Returning connection for pool %1s", pool.getName());
      pool.putConnection(con);
   }
   
   /**
    * Get the connection pool for a given data source name.
    * Data source name is a symbolic name representing the jdbc driver, user name, password etc.
    */
   private SingleDataSourceConnectionPool getConnectionPoolFor( String dataSourceName )
   throws DBException
   {
       Object obj = m_connectionPools.get(dataSourceName);
       if (obj != null)
       {
          // connection pool for this data source found.
          // return it.
          return (SingleDataSourceConnectionPool)obj;
       }
       
       // Attempt to create a connection pool for this data source
       // issue an error saying that connection pool not found
       AppObjects.trace(this,"cp: Connection pool not found for data source :%1s",dataSourceName );

       synchronized( this )
       {
         // Some other might have created this already.
         // So do a repeat check
         obj = m_connectionPools.get(dataSourceName);
         if (obj != null) 
         {
            return (SingleDataSourceConnectionPool)obj;
         }

         // No other thread has created this pool                
         obj = new SingleDataSourceConnectionPool(dataSourceName );

         // add it to the available data pools
         m_connectionPools.put(dataSourceName, obj);
       } // end of synchronized block
                
       return (SingleDataSourceConnectionPool)obj;
   }
   
} // end of class ConnectionPoolConnectionManager



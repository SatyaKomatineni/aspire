/*
 * Created on Dec 20, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.cp4;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.AICalendar;
import com.ai.db.CJDBCConnectionCreator;
import com.ai.db.DBDefinition;
import com.ai.db.DBException;
import com.ai.db.events.SWIConnectionEvents;
import com.ai.scheduler.BasicScheduleTime;
import com.ai.scheduler.IScheduler;
import com.ai.scheduler.SchedulerException;

/**
 * @author Satya
 *
 */
class SingleDataSourceConnectionPool
{
   // Symbolic name for the data source
   // Passed  in during the construction time.
   private String m_dataSourceName = null;
   
   // all connections that are not closed
   // Combines free and checked out connection items.
   //  a vector of connection items 
   private Vector m_allConnections = new Vector(); 

   // vector of connection items
   private Vector   m_freeConnections = new Vector();

   //Hashtable of checked out connections 
   // key/value : <connection, connectionItem>
   private Hashtable m_coConnections = new Hashtable();

   // expiration period in milliseconds
   // In addition this will be read from the config file as well
   private long m_connectionExpirationPeriod = 10 * 60000; // 10 min
   
   //Number of preload connections
   // In addition this will be read from the config file as well
   private int m_numOfPreloadConnections = 0;
   
   public SingleDataSourceConnectionPool(String dataSourceName) throws DBException
   {
      try 
      {
        m_dataSourceName = dataSourceName;
        
        // Get the expiration for each connection
        String expirationTimeInMin = DBDefinition.getValue(dataSourceName,"expirationTimeInMin","10");
        m_connectionExpirationPeriod = Long.parseLong(expirationTimeInMin) * 60000;
        AppObjects.trace(this,"expire after %1s milliseconds", this.m_connectionExpirationPeriod);

        String numOfConnections = DBDefinition.getValue(dataSourceName,"minimumNumberOfConnections","0");
        m_numOfPreloadConnections = Integer.parseInt(numOfConnections);
        AppObjects.trace(this,"minimum number of connections:%1s",this.m_numOfPreloadConnections);
        
        // register the cleanuptask
        IScheduler scheduler = (IScheduler)(AppObjects.getIFactory().getObject(IScheduler.GET_SCHEDULER_REQUEST,null));
        scheduler.schedule(new ConnectionPoolCleanupTask(this), new BasicScheduleTime(5));
      }
      catch(RequestExecutionException x)
      {
         throw new DBException("cp: Could not obtain or create the Scheduler object named : scheduler",x);
      }
      catch(SchedulerException x)
      {
        throw new DBException("cp: Could not schedule the cleanup task",x);
      }
   }
   
   /**
    * Get method for the name of the data source representing this connection pool.
    */
   public String getName()
   {
      return m_dataSourceName;
   }

   /**
    * Get a free connection that could be used by a client.
    * 
    * if a free connection is available checkout and return it after setting the time.
    * If no free connection is available create a connection
    * and add the created connection to the checked out pool 
    * and return the connection after setting the time.
    * 
    * 1. This method is not synchronized for a reason as a whole
    * 2. Parts of it are synchronized
    * 3. Creating a new connection happens outside of any locking
    * 4. Creating a new connection takes time so that should not lock this object
    * 5. Once a connection is created checking it into the pool is a locked behavior
    */
   Connection getConnection()
                     throws com.ai.db.DBException
   {
   		//The following synchronized
       Connection con = getConnectionFromPool();
       if (con != null) return con;
       
      // Case of no free connections available
      // Create a new connection
      // This is not synchronized as this can take a while
      ConnectionPoolConnectionItem cpItem = createRealNewConnection();
      
      //Synchronized again as we are altering the counts
      // add it to all and checkout
      addNewConnectionForCheckout(cpItem);
      
      // return
      return cpItem.m_con;
   }
   
   synchronized Connection getConnectionFromPool()
   {
       if (m_freeConnections.size() != 0)
       {
          // Checkout the first item from free connections
          ConnectionPoolConnectionItem cpItem = 
             (ConnectionPoolConnectionItem)m_freeConnections.elementAt(0);
          checkoutConnectionItem(cpItem);

          // Set the time of last used so that it could be expired 
          // at the appropriate time.
          cpItem.touch();
          return cpItem.m_con;
       }
       else
       {
           return null;
       }
       
   }
   synchronized void addNewConnectionDeprecated(ConnectionPoolConnectionItem cpItem)
   {
       // Put the connection in to the checked out connections
       m_coConnections.put(cpItem.m_con,cpItem);
       
       // set the last update time
       cpItem.touch();
   }
   synchronized void addNewConnectionForCheckout(ConnectionPoolConnectionItem cpItem)
   {
   		this.m_allConnections.add(cpItem);
       // Put the connection in to the checked out connections
       m_coConnections.put(cpItem.m_con,cpItem);
       
       // set the last update time
       cpItem.touch();
   }
   synchronized void addNewConnectionForFree(ConnectionPoolConnectionItem cpItem)
   {
		this.m_allConnections.add(cpItem);
       // Put the connection in to the checked out connections
       this.m_freeConnections.add(cpItem);
       
       // set the last update time
       cpItem.touch();
   }
  /**
   * checkout a connection
   * Remove an element from free connections and place it in co_connections
   *
   * Why is this synchronized?
   *
   *  The idea of removing an element from free to checkout state
   *  can be done by only one thread at a time.
   *
   * Why am I not setting the last update time?
   *  Such an actiion is not considered an essential unit of this work.
   *  There are cases where you don't want the last update time to be set.
   *  It is left to the calling code to make that decission and make this
   *  function generic and reusable.
   * 
   */
  synchronized boolean checkoutConnectionItem( ConnectionPoolConnectionItem cpItem )
  {
      boolean bInTheFreeList = m_freeConnections.removeElement(cpItem);
      if (bInTheFreeList )
      {
         m_coConnections.put(cpItem.m_con, cpItem );
         return true;
      }
      else
      {         
         return false;
      }         
  }
  
  /**
   * Move a connection from checkout connection list to the free connection list.
   */
  synchronized boolean checkinConnection( Connection con )
  throws DBException
  {
      ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)m_coConnections.remove(con );
      if (cpItem != null)
      {
         if (isConnectionClosed(con))
         {
            AppObjects.error(this,"cp: A conection has been returned after being closed ");
            // connection might have been closed after 
            this.removeConnectionItem(cpItem);
         }
         else
         {
            // connection is ok
            m_freeConnections.addElement(cpItem);
         }            
         return true;
      }
      else
      {         
         // not in the list of checked out items 
         return false;
      }         
  }
  private boolean isConnectionClosed(Connection con)  
  {
    try 
    { 
      boolean status = con.isClosed(); 
      if (status == true )
      {
         AppObjects.trace(this,"cp: Connection returned to the pool as closed");
         AppObjects.trace(this,"cp: This connection is going to be taken out of the pool" );
      }
      return status;
    }
    catch (java.sql.SQLException x)
    {
      AppObjects.log("Error:sql exception",x);
      return true;
    }
  }
  
  /**
   * Create a new connection and add it to the all connection list.
   * Also set the creation time.
   * 1. This method should not be synchronized
   * 2. It may be ok to call at initialization inside synchronnized
   * 3. During regular operations this should not be called from a locked block
   * 4. Because  this operation takes some time
   * 5. This should not be updating any common counters or registration
   * 
   */
  private ConnectionPoolConnectionItem createRealNewConnection() 
  throws DBException
  {
      AppObjects.trace(this,"cp: Creating a new connection to %1s", m_dataSourceName);
      Connection con = CJDBCConnectionCreator.getInstance().createConnection(m_dataSourceName);
      
      ConnectionPoolConnectionItem cpItem = new ConnectionPoolConnectionItem( con, m_dataSourceName );
      cpItem.m_creationTime = System.currentTimeMillis();
      //m_allConnections.addElement(cpItem);
      
      //Raise the event that a connection is created
      SWIConnectionEvents.onCreateConnection(con);
      
      return cpItem;
  }
  /**
   * Return the connection to the pool.
   * Notice that the underlying checkinConnection method is synchronized.
   */
   void putConnection( Connection con)
   throws DBException
   {
      checkinConnection(con);
   }
   
   void printConnections()
   {
      AppObjects.trace(this,"cp: Name of the data source : \t%1s", m_dataSourceName);
      
      int allConnectionsSize = 0;
      int coConnectionsSize = 0;
      int freeConnectionsSize = 0;
      Iterator allConnections = null;
      
      //synchronized to get consistent results of internal counters
      synchronized(this)
	  {
      	 allConnectionsSize = this.m_allConnections.size();
      	 coConnectionsSize = this.m_coConnections.size();
      	 freeConnectionsSize = this.m_freeConnections.size();
         allConnections = this.cloneAllConnections().iterator();
	  }
      AppObjects.trace(this,"cp: Total number of connections: \t%1s", allConnectionsSize);
      AppObjects.trace(this,"cp: Connections that are currently out : \t%1s", coConnectionsSize);
      AppObjects.trace(this,"cp: Connections that are free :\t%1s", freeConnectionsSize );
      AppObjects.trace(this,"cp: Individual connection details ");
      
      while(allConnections.hasNext())
      {
         ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)allConnections.next();
         AppObjects.trace(this,"cp: Creation time : /t%1s" 
            , AICalendar.formatTimeInMilliSeconds(cpItem.m_creationTime));
         AppObjects.trace(this,"cp: Last checkout time : /t%1s" 
            , AICalendar.formatTimeInMilliSeconds(cpItem.m_lastCheckoutTime));
      }                                                                                 
   }


   /**
    * Remove a connection item and close the underlying connection.
    */
   private void removeConnectionItem(ConnectionPoolConnectionItem cpItem )
   throws DBException
   {
   	  synchronized(this)
	  {
   	  	// Take it out of all connections
   	  	m_allConnections.removeElement(cpItem);
   	  	this.m_freeConnections.remove(cpItem);
   	  	this.m_coConnections.remove(cpItem.m_con);
	  }

      AppObjects.trace(this,"cp: Closing connection ");
      //raise event
      SWIConnectionEvents.onPreCloseConnection(cpItem.m_con);
      
      try { cpItem.m_con.close(); }
      catch(java.sql.SQLException x)
      {
         AppObjects.log("Error: can not close connection",x);
      }
   }
   
   /**
    * returns true if the connection has not been used for a specified amount of time.
    */
   private boolean hasExpired(ConnectionPoolConnectionItem cpItem )
   {
      long curTime = System.currentTimeMillis();
      long durationSinceLastCheckedOut = curTime - cpItem.m_lastCheckoutTime;
      if (durationSinceLastCheckedOut > m_connectionExpirationPeriod)
      {
         // log to the fact that this connection has expired
         AppObjects.trace(this,"cp: The following connection has expired");
         AppObjects.trace(this,"cp: %1s", cpItem );
         return true; 
      }
      return false;
   }
   /**
    * Check each of the free connections and close them if necessary.
    *
    * Find out if there are any connections that are not valid.
    * If a connection is not valid close the connection and remove it from
    * the pool.
    * Validity is checked based on a select statement
    * 
    * Logic
    *
    *    checkout
    *    test
    *    return to the pool if good
    *    log message and close the connection if failed
    *    remove the connection from the list
    *    close the connection also if it has not been used in a while
    *    Also remove it from the list.
    *
    * This method is synchronized only in parts so that it causes minimum
    * intervention with the process of obtaining connections from the pool.
    * 
    */
   
   public void testAndCleanupConnections()
   {
	   try
	   {
   		this.releaseConnectionsIfNeeded();
	   }
	   catch(DBException x)
	   {
		   AppObjects.log("Error:Error in releasing connections",x);
	   }
   }
   
   /**
    * true if the connection is valid.
    * A connection is valid if it executes a sql statement with out an exception
    * and it has not expired since it was last used.
    */
   private boolean validateConnectionStatus( ConnectionPoolConnectionItem inCpItem )
   {
      if (hasExpired( inCpItem ) == true)
      {
          return false;
      }
      // it has not expired 
      // check the status
      // to be completed
      return true;
   }
   
   /**
    * closes all collections at the finalization time.
    */
   synchronized private void closeAllConnectionsDeprecated()
   throws DBException
   {
         Vector freeConnections = (Vector)m_freeConnections.clone();
         for (Enumeration e=freeConnections.elements();e.hasMoreElements();)
         {
            // Get a connection item
            ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)e.nextElement();
            this.removeConnectionItem(cpItem);
         } // for

         
         Vector coConnections = (Vector)m_coConnections.clone();
         for (Enumeration e=coConnections.elements();e.hasMoreElements();)
         {
            // Get a connection item
            ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)e.nextElement();
            this.removeConnectionItem(cpItem);
         } // for
   }   // end of closeAllCollections
   public void finalize() throws Throwable
   {
   	  AppObjects.error(this,"This should not be getting called");
      closeAllConnectionsDeprecated();
      super.finalize();
   }
   
   /**
    *  
    */
   public synchronized void preloadDataSource() throws DBException
   {
   		AppObjects.trace(this,"Preloading for datasource:%1s", this.m_dataSourceName);
  		int curSize = this.m_allConnections.size();
		AppObjects.trace(this,"Current pool size:%1s", curSize);
		AppObjects.trace(this,"Needed pool size:%1s", this.m_numOfPreloadConnections);
		int neededSize = this.m_numOfPreloadConnections - curSize;
		if (neededSize <= 0)
		{
			AppObjects.trace(this,"No need to create new connections as the needed size is:%1s", neededSize);
			return;
		}
		for (int i=0;i<neededSize;i++)
		{
			ConnectionPoolConnectionItem cpItem = this.createRealNewConnection();
			this.addNewConnectionForFree(cpItem);
			//this.m_freeConnections.add(cpItem);
		}
		AppObjects.trace(this,"The new pool size is:%1s", this.m_allConnections.size());
   		AppObjects.trace(this,"Completed Preloading for datasource:%1s", this.m_dataSourceName);
   }
   /**
    * Higher level goals
    * ********************
    * 1. Ensure a certain preset number of connections
    * 2. If a connection has been established for too long clean it up
    * 3. If a connection has not been used for tool clean it up
    * 
    * Secondary goals
    * *****************
    * 1. Try to clean up older connections first
    * 2. it may take more than one pass for a stable state
    * 3. Try not to block while this is happening 
    *
    */
   private void releaseConnectionsIfNeeded()
   throws DBException
   {
   		//Using a sync block clone all connections
	   	List allConnections = this.cloneAllConnections();
	   	int allConSize = allConnections.size();
	   	int minSize = this.m_numOfPreloadConnections;
	   	int numberToClose = allConSize - minSize;
	   	
	   	AppObjects.trace(this,"There are currently:%1s connections", allConSize);
	   	AppObjects.trace(this,"The minimum number of connections to be kept are:%1s connections", minSize);
	   	AppObjects.trace(this,"The number of connections to be closed are:%1s connections", numberToClose);
	   	
	   	if (numberToClose <= 0)
	   	{
	   		//There are connections to cleanup
	   		AppObjects.trace(this,"There are no connections to cleanup. Returning from the release connections cleanup task.");
	   		return;
	   	}
	   	
	   	AppObjects.trace(this,"There are connections to be cleaned up. walking through the collection to identify candidate connections to close.");
	   	Iterator allItr = allConnections.iterator();
	   	
	   	//for each connection item in all connections
	   	while(allItr.hasNext())
	   	{
	   		ConnectionPoolConnectionItem cpItem
			= (ConnectionPoolConnectionItem)allItr.next();
	   		
	   		//before proceeding see if this is needed
	   		//See in a sync block if the connections are already reduced.
	   		if (this.getCurrentNumberOfConnections() > minSize)
	   		{
	   			//There are connections to be released
	   			attemptToRelease(cpItem);
	   		}
	   		else
	   		{
	   			//No need to release anymore
	   			AppObjects.trace(this,"No need to release any more as cursize is less than or equal to min size");
	   			break;
	   		}
	   	}//eof-while
	   	int curSize = this.getCurrentNumberOfConnections();
	   	AppObjects.trace(this,"Latest size of the pool is:%1s", curSize);
   }//eof-function
   
   private boolean isThisACandidateForRelease(ConnectionPoolConnectionItem cpItem)
   {
   		boolean expired = this.hasExpired(cpItem);
   		if (expired)
   		{
   			return true;
   		}
   		else
   		{
   			return false;
   		}
   }
   private void attemptToRelease(ConnectionPoolConnectionItem cpItem)
   throws DBException
   {
   		if (!this.isThisACandidateForRelease(cpItem))
   		{
   			AppObjects.trace(this,"connection %1s is not a candidate for release", cpItem);
   			return;
   		}
		AppObjects.trace(this,"connection %1s is a candidate for release", cpItem);
		releaseConnection(cpItem);
   }
   
   /**
    * 1. Check it out first
    * 2. Remove it from the pool
    * @param cpItem
    */
   private void releaseConnection(ConnectionPoolConnectionItem cpItem)
   throws DBException
   {
	    // Try checking it out
	    boolean bCoStatus = this.checkoutConnectionItem(cpItem);
	    if (bCoStatus == false)
	    {
	       AppObjects.trace(this,"Not able to check out this connection item:%1s", cpItem);
	       return;
	    }
	    //able to check it out
	    this.removeConnectionItem(cpItem);
   }
   /**
    * The size will not be correct if it is not synchronized
    * @return
    */
   private synchronized int getCurrentNumberOfConnections()
   {
   		return this.m_allConnections.size();
   }
   
   private synchronized List cloneAllConnections()
   {
   		List newone = new ArrayList();
   		newone.addAll(this.m_allConnections);
   		return newone;
   }
   
} // end of class

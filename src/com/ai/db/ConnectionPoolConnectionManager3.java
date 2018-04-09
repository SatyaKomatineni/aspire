/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.db;

//******************************************************************************
// Java based imports
import java.util.*;                                   //Vector etc.
import java.sql.*;                                    // Connection

// AI appplication services
import com.ai.common.*;                               // Date related
import com.ai.application.utils.AppObjects;           // app services
import com.ai.application.interfaces.*;               // app services

// scheduler. Used for cleaning the pool at regular intervals
import com.ai.scheduler.*;                            // Scheduling services
//******************************************************************************

/**
 * Implements the connection pooling as defined by the IConnectionManager interface.
 * Behavior
 * ********
 * 1. Given a data source name, returns a dedicated sql connection to the client.
 * 2. The client needs to return the connection when the connection is no longer needed.
 * 3. To minimize the error of returning a connection to the wrong pool, the client 
 * does not specify the data source name that this connection belongs to.
 *
 * Strategy Used
 * **************
 * A new connection is created if all the available connections are checked out.
 * The goal is that the total number of connection will reach a plateau over the life 
 * of the program based on the program's dynamic requirements.
 *
 * Connections are checked for their expiry time period and validity regularly to prevent two 
 * problems. Expiry of connections will help with the burst traffic. Checking the validity of 
 * the connections strikes a balance between returning a connection quickly and not keeping 
 * bad connections in the pool.
 *
 * Unresolved issues
 * *****************
 * 1. If a connection does go bad, it is possible that it will be returned to an unsuspecting
 * client.  This happens because a bad connection is closed only at certain intervals.
 *
 * 2. In some cases there may be only a limited number of connections to a database.  
 * Which means one would like to set an upper limit on the connections.  The additional clients
 * would wait until one of the other clients release the connection
 *
 * Solution to unresolved issues
 * ******************************
 * 1. issue 1 can be solved, in my mind, by having the connections returned to a maintenance pool
 * which will be recycled based on their validity. Or even better, a new thread would be spawned 
 * to check the connections validity and returned to the main pool.  This approach would eliminate 
 * the unsuspecting bad connections altogether.  This functionality is slated for the next release.
 *
 * 2. It is deliberately left to the next phase to implement the upper limit pool so that 
 * the working functionality can be delivered in backward compatiable testable releases.  
 * So both 1 & 2 will be delivered as the next release of the connection manager implementation.
 *
 * Programming considerations of this pool
 * ****************************************
 *
 * 1. This class is implemented in such a way that it could be instantiated either via a factory
 * or a regular new instantiation.  This is the reason why the out of place "executeRequest" 
 * method is seen here. When instantiated through the Aspire framework factory, this class 
 * is a singleton.
 *
 * 2. Another programming goal is to minimize the synchronized code so that the benifits of 
 * multithreading are realized.
 *
 * 3. All the log messages are channeled through the aspire framework logging and messages
 * are prefixed with "cp: " so that they could be filtered on the back end if needed.
 *
 * 4. Additional classes required by the ConnectionPoolConnectionManager1 are made private 
 * so that newer versions of this class can be developed with out retiring this class itself.
 * This provides a better testing and migration strategy.
 *
 * 5. To accomplish scheduled tasks such as expiration dates for connections, it uses a generic
 * scheduler from the scheduler package to run one of it's methods at regular intervals. 
 * Documentation for the scheduler can be found in the scheduler package.
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
public class ConnectionPoolConnectionManager3 implements IConnectionManager, ICreator
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
      
   /**
    * Default constructor
    */
   public ConnectionPoolConnectionManager3()
   {
   }
  
   /**
    * Get a connection from the pool specified by the data source name.
    */
   public Connection getConnection(String dataSourceName ) 
                             throws DBException
   {
      // log the message
       AppObjects.info(this,"cp: Requesting a connection from data source %1s",dataSourceName );
          
       // request the pool and get connection
       SingleDataSourceConnectionPool pool = getConnectionPoolFor( dataSourceName );
       Connection con = pool.getConnection();
          
       // remember what pool does this connection belongs to
       // so that the connection can be returned to the respective pool
       m_connectionToPoolMapping.put(con,pool);
       return con;
   }
                             
   /**
    * Return connection back to the pool.
    */
   public void putConnection(Connection con)
                             throws DBException
   {
          SingleDataSourceConnectionPool pool = 
             (SingleDataSourceConnectionPool)m_connectionToPoolMapping.remove(con);
             
          if (pool == null)
          { 
            // pool not found. Abnormal. Log and let it go.
            AppObjects.log("cp: No pool found to return the connection ");
            return;
          }
            
          AppObjects.info(this,"cp: Returning connection for pool %1s",pool.getName());
          pool.putConnection(con);
   }
   
   /**
    * Get the connection pool for a given data source name.
    * Data source name is a symbolic name representing the jdbc driver, user name, password etc.
    */
   private SingleDataSourceConnectionPool getConnectionPoolFor( String dataSourceName )
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
       AppObjects.info(this,"cp: Connection pool not found for data source :%1s", dataSourceName );

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
   
//*************************************************************************
//* a private class
//*************************************************************************
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
   private long m_connectionExpirationPeriod = 10 * 60000; // 5 min
   
   //creation lock
   private String createLock = "createLock";

   public SingleDataSourceConnectionPool(String dataSourceName)
   {
      try 
      {
        m_dataSourceName = dataSourceName;
        
        // Get the expiration for each connection
        String expirationTimeInMin = AppObjects.getIConfig().getValue("AppObjects.ConnectionPools.expirationTimeInMin","10");
        m_connectionExpirationPeriod = Long.parseLong(expirationTimeInMin) * 60000;
        
        // register the cleanuptask
        IScheduler scheduler = (IScheduler)(AppObjects.getIFactory().getObject(IScheduler.GET_SCHEDULER_REQUEST,null));
        scheduler.schedule(new ConnectionPoolCleanupTask(), new BasicScheduleTime(5));
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log( "cp: Could not obtain or create the Scheduler object named : scheduler");
         AppObjects.log(x);
      }
      catch(SchedulerException x)
      {
         AppObjects.log( "cp: Could not schedule the cleanup task");
         AppObjects.log(x);
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
    */
   Connection getConnection()
                     throws com.ai.db.DBException
   {
       Connection con = getConnectionFromPool();
       if (con != null) return con;
      // Case of no free connections available
      // Create a new connection
      ConnectionPoolConnectionItem cpItem = createNewConnection();

      addNewConnection(cpItem);
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
   synchronized void addNewConnection(ConnectionPoolConnectionItem cpItem)
   {
       // Put the connection in to the checked out connections
       m_coConnections.put(cpItem.m_con,cpItem);
       
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
  {
      ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)m_coConnections.remove(con );
      if (cpItem != null)
      {
         if (isConnectionClosed(con))
         {
            AppObjects.log("cp: A conection has been returned after being closed ");
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
         AppObjects.log("cp: Connection returned to the pool as closed");
         AppObjects.log("cp: This connection is going to be taken out of the pool" );
      }
      return status;
    }
    catch (java.sql.SQLException x)
    {
      AppObjects.log(x);
      return true;
    }
  }
  
  /**
   * Create a new connection and add it to the all connection list.
   * Also set the creation time.
   */
  private ConnectionPoolConnectionItem createNewConnection() 
                            throws DBException
   {
        return createRealNewConnection();
   }                            
   
  private ConnectionPoolConnectionItem createRealNewConnection() 
  throws DBException
  {
      AppObjects.log("cp: Creating a new connection to " + m_dataSourceName);
      Connection con = CJDBCConnectionCreator.getInstance().createConnection(m_dataSourceName);
      
      ConnectionPoolConnectionItem cpItem = new ConnectionPoolConnectionItem( con, m_dataSourceName );
      cpItem.m_creationTime = System.currentTimeMillis();
      m_allConnections.addElement(cpItem);
      return cpItem;
  }
  /**
   * Return the connection to the pool.
   * Notice that the underlying checkinConnection method is synchronized.
   */
   void putConnection( Connection con)
   {
      checkinConnection(con);
   }
   
   void printConnections()
   {
      AppObjects.info(this,"cp: Name of the data source : \t%1s",m_dataSourceName);
      AppObjects.info(this,"cp: Total number of connections: \t%1s",m_allConnections.size());
      AppObjects.info(this,"cp: Connections that are currently out : \t%1s",m_coConnections.size());
      AppObjects.info(this,"cp: Connections that are free :\t%1s",m_freeConnections.size() );
      AppObjects.info(this,"cp: Individual connection details ");
      Vector allConnections = (Vector)m_allConnections.clone();
      for (Enumeration e=allConnections.elements();e.hasMoreElements();)
      {
         ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)e.nextElement();
         AppObjects.info(this,"cp: Creation time : /t%1s" 
            ,AICalendar.getInstance().formatTimeInMilliSeconds(cpItem.m_creationTime));
         AppObjects.info(this,"cp: Last checkout time : /t%1s" 
            ,AICalendar.getInstance().formatTimeInMilliSeconds(cpItem.m_lastCheckoutTime));
      }                                                                                 
   }


   /**
    * Remove a connection item and close the underlying connection.
    */
   private void removeConnectionItem(ConnectionPoolConnectionItem cpItem )
   {
      // Take it out of all connections
      m_allConnections.removeElement(cpItem);

      AppObjects.log("cp: Closing connection ");
      try { cpItem.m_con.close(); }
      catch(java.sql.SQLException x)
      {
         AppObjects.log("cp: Can not close connection ");
         AppObjects.log(x);
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
         AppObjects.info(this,"cp: The following connection has expired");
         AppObjects.info(this,"cp: %1s",cpItem );
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
   
   private void testAndCleanupConnections()
   {
         Vector freeConnections = (Vector)m_freeConnections.clone();
         for (Enumeration e=freeConnections.elements();e.hasMoreElements();)
         {
            // Get a connection item
            ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)e.nextElement();

            // Try checking it out
            boolean bCoStatus = this.checkoutConnectionItem(cpItem);
            if (bCoStatus == false)
            {
               // Not able to check out this connection item
               continue;
            }
            // successful checkout
            boolean bConStatus = validateConnectionStatus( cpItem );
            if (bConStatus == true )
            {
               // connection is good, check it back in and continue
               checkinConnection(cpItem.m_con );
               continue;
            }
            else
            {
               // connection is bad
               // remove it from the checked out list
               m_coConnections.remove(cpItem.m_con);
               removeConnectionItem(cpItem);
               continue;
            }
         } // for
   }   // end of function
   
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
   
  //*************************************************************************
  //* ConnectionPoolCleanUptask
  //*************************************************************************
  class ConnectionPoolCleanupTask implements com.ai.scheduler.IScheduleTask
  {
     public boolean execute()
     {
        AppObjects.info(this,"cp: Connection view before cleanup at: %1s",AICalendar.getCurTimeString());
        printConnections();
        testAndCleanupConnections();
        AppObjects.info(this,"cp: Connection view after cleanup: %1s",AICalendar.getCurTimeString());
        printConnections();
        return true;        
     }
  }
   /**
    * closes all collections at the finalization time.
    */
   synchronized private void closeAllConnections()
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
      closeAllConnections();
      super.finalize();
   }
} // end of class


//*************************************************************************
//* a private class
//*************************************************************************
class ConnectionPoolConnectionItem                                         
{
   Connection m_con;
   public String m_dataSourceName;
   
   // null implies it is free
   public long m_lastCheckoutTime;
   // creation time
   public  long m_creationTime;

   ConnectionPoolConnectionItem(Connection inCon, String inDataSourceName )
   {
      m_con = inCon;
      m_dataSourceName = inDataSourceName; 
   }
   public void touch()
   {
      m_lastCheckoutTime = System.currentTimeMillis();
   }      
   public String toString()
   {
      return new String("{datasource name: " + m_dataSourceName
                        + ", last check out time : " + AICalendar.formatTimeInMilliSeconds(m_lastCheckoutTime)
                        + ", creation time : " + AICalendar.formatTimeInMilliSeconds(m_creationTime)
                        + "}");
   }
} // end of ConnectionPoolConnectionItem  


//*************************************************************************
//* End of a private class
//*************************************************************************
} // end of class ConnectionPoolConnectionManager



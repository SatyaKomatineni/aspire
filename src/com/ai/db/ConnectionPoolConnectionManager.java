/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;
import java.sql.*;
import com.ai.application.utils.AppObjects;
import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.common.*;
      
public class ConnectionPoolConnectionManager implements IConnectionManager, ICreator
 {
   // key data source name, value: data source connection pool object
   private Hashtable m_connectionPools = new Hashtable();

   // key : connection, value: data source connection pool
   private Hashtable m_connectionToPoolMapping = new Hashtable();
   
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
      
  public ConnectionPoolConnectionManager()
  {
  }
  private SingleDataSourceConnectionPool getConnectionPoolFor( String dataSourceName )
  {
      Object obj = m_connectionPools.get(dataSourceName);
      if (obj == null)
      {
         // issue an error saying that connection pool not found
         AppObjects.log("cp: Connection pool not found for data source :" + dataSourceName );
         obj = new SingleDataSourceConnectionPool(dataSourceName );
         // add it to the available data pools
         m_connectionPools.put(dataSourceName, obj);
      }

      return (SingleDataSourceConnectionPool)obj;
  }
  public Connection getConnection(String dataSourceName ) 
                            throws DBException
  {
         AppObjects.log("cp: Requesting a connection from data source " + dataSourceName );
         SingleDataSourceConnectionPool pool = getConnectionPoolFor( dataSourceName );
         Connection con = pool.getConnection();
         // remember what pool does this connection belong to
         m_connectionToPoolMapping.put(con,pool);
         return con;
  }
                            
  public void putConnection(Connection con)
                            throws DBException
  {
         SingleDataSourceConnectionPool pool = 
            (SingleDataSourceConnectionPool)m_connectionToPoolMapping.remove(con);
         AppObjects.log("cp: Returning connection for pool " + pool.getName());
          pool.putConnection(con);
  }
} 
class SingleDataSourceConnectionPool
{
   private String m_dataSourceName = null;
   
   // all connections that are not closed
   //  connection items )
   Vector m_allConnections = new Vector(); 

   // vector of connection items
   Vector   m_freeConnections = new Vector();

   //Hashtable of checked out connections 
   // key/value connection, connectionItem
   Hashtable m_coConnections = new Hashtable();

   // expiration period in milliseconds
   long m_connectionExpirationPeriod = 5 * 60000; // 5 min

   public SingleDataSourceConnectionPool(String dataSourceName)
   {
      m_dataSourceName = dataSourceName;
   }
   public String getName()
   {
      return m_dataSourceName;
   }
   synchronized Connection getConnection()
                     throws com.ai.db.DBException
   {
         //Work on a copy of the free connections
         Vector freeConnections = (Vector)m_freeConnections.clone();
         for (Enumeration e=freeConnections.elements();e.hasMoreElements();)
         {
            // walk through each of the free connections
            // and return a valid one to the requestor
            Object obj = e.nextElement();
            ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)(obj);
            
            // make sure this object has not expired
            if (hasExpired(cpItem) == true )
            {
                // connection has expired
                removeConnectionItem( cpItem );
                continue;
            }
            else
            {
               // connection has not expired
               m_freeConnections.removeElement(cpItem);
               checkoutConnectionItem( cpItem );
               return cpItem.m_con;
            } // if
         } // for
         // outside for
         // control comes here only if there are no elements in the 
         // free connections, or all the connections in the free connections
         // have been removed due to expiry. In either case do the following
         
         ConnectionPoolConnectionItem cpItem = createNewConnection();
         checkoutConnectionItem(cpItem );
         return cpItem.m_con;
         
   }
  void checkoutConnectionItem( ConnectionPoolConnectionItem cpItem )
  {
      cpItem.m_lastCheckoutTime = System.currentTimeMillis();
      m_coConnections.put(cpItem.m_con, cpItem );
      return;
  }
  
  private ConnectionPoolConnectionItem createNewConnection() 
                            throws DBException
   {
      AppObjects.log("cp: Creating a new connection to " + m_dataSourceName);
      Connection con = CJDBCConnectionCreator.getInstance().createConnection(m_dataSourceName);
      
      ConnectionPoolConnectionItem cpItem = new ConnectionPoolConnectionItem( con, m_dataSourceName );
      cpItem.m_creationTime = System.currentTimeMillis();
      m_allConnections.addElement(cpItem);
      return cpItem;
   }                            
   
   synchronized void putConnection( Connection con)
   {
      printConnections();
      //Locate and remove the connection item
      ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)m_coConnections.remove(con);
      m_freeConnections.addElement(cpItem);
   }
   void printConnections()
   {
      AppObjects.log("cp: Name of the data source : \t" + m_dataSourceName);
      AppObjects.log("cp: Total number of connections: \t" + m_allConnections.size());
      AppObjects.log("cp: Connections that are currently out : \t" + m_coConnections.size());
      AppObjects.log("cp: Connections that are free :\t" + m_freeConnections.size() );
      AppObjects.log("cp: Individual connection details ");
      Vector allConnections = (Vector)m_allConnections.clone();
      for (Enumeration e=allConnections.elements();e.hasMoreElements();)
      {
         ConnectionPoolConnectionItem cpItem = (ConnectionPoolConnectionItem)e.nextElement();
         AppObjects.log("cp: Creation time : /t" 
            + AICalendar.getInstance().formatTimeInMilliSeconds(cpItem.m_creationTime));
         AppObjects.log("cp: Last checkout time : /t" 
            + AICalendar.getInstance().formatTimeInMilliSeconds(cpItem.m_lastCheckoutTime));
      }                                                                                 
   }


//   void cleanupConnections()
//   {
//      printConnections();
//      Connection getConnection
//   }
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
   
   private boolean hasExpired(ConnectionPoolConnectionItem cpItem )
   {
      long curTime = System.currentTimeMillis();
      long durationSinceLastCheckedOut = curTime - cpItem.m_lastCheckoutTime;
      if (durationSinceLastCheckedOut > m_connectionExpirationPeriod)
      {
         return true; 
      }
      return false;
   }
} // end of class


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
    }


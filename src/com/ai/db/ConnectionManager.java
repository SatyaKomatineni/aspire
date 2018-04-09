/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;
import java.util.*;
import java.sql.*;

import com.ai.application.utils.AppObjects;

public class ConnectionManager
{
   static private ConnectionManager m_connectionManager;
   private Hashtable m_dbDefinitions = new Hashtable();
                     // key   -> dbAlias        : string
                     // value -> dbDefinition   : DBDefinition
   private Hashtable m_dedicatedConnections = new Hashtable();
                     // key   -> dbAlias                    : string
                     // value -> Vector<jdbc_connection>    : Vector<Connection>
   private Hashtable m_sharedConnections = new Hashtable();                     
                     // key   -> dbAlias           : string
                     // value -> jdbc_connection   : Connection
   private Hashtable m_jdbcDrivers = new Hashtable();
                     // key   -> jdbcDriverName    : string
                     // value -> jdbcDriver        : Driver

   static final int CTYPE_SHARED_CONNECTION = 1;
   static final int CTYPE_DEDICATED_CONNECTION = 2;
   static final int WAITTIME_NO_WAIT = 0;
   static final int WAITTIME_INDEFINITE_WAIT = -1;

  protected ConnectionManager()
  {
  }
  static public ConnectionManager getInstance()
  {
      if (m_connectionManager == null)
      {
         m_connectionManager = new ConnectionManager();
      }
      return m_connectionManager;
  }
  public Connection getConnection(String dbProfile ) 
                            throws DBException
  {
   try 
   {
      Object sharedConnection = m_sharedConnections.get(dbProfile);
      if (sharedConnection != null)
      {
        return (Connection)sharedConnection;
      }
      // get a new shared connection
      sharedConnection = createAConnection(dbProfile);
      m_sharedConnections.put(dbProfile,sharedConnection );

      return (Connection)sharedConnection;
   }
   catch( com.ai.application.interfaces.ConfigException x)
   {
      throw new DBException("Check database definition for profile: " + dbProfile
                           ,x);
   }
  }

  private Connection createAConnection(String dbProfile)
            throws DBException, com.ai.application.interfaces.ConfigException
  {
   DBDefinition dbDef = null;
   try 
   {
    // Do I have
    dbDef = getDefinition(dbProfile);
    registerDriver(dbDef);

    return
    DriverManager.getConnection(dbDef.getConnectionString()
                        ,dbDef.getUserid()
                        ,dbDef.getPassword() );
   }
   catch(SQLException x)
   {
	  String errormsg = "Could not create a connection";
      StringBuffer msg = new StringBuffer(errormsg);
      msg.append("\n\tRequested connection details :\n\t" + dbDef );
      AppObjects.error(this,msg.toString());
      throw new DBException(errormsg,x);
   }                        
  }
  private Driver registerDriver(DBDefinition dbDef )
                  throws DBException
                  ,SQLException
  {
    Object driver = m_jdbcDrivers.get(dbDef.getJdbcDriverString());
    if (driver != null ) return (Driver)driver;

    driver = createJDBCDriver(dbDef.getJdbcDriverString());
    m_jdbcDrivers.put(dbDef.getJdbcDriverString(),(Driver)driver);
    // register the driver as well
    DriverManager.registerDriver((Driver)driver);
    return (Driver)driver;
  }
  private Driver createJDBCDriver(String jdbcDriverString ) 
            throws DBException
  {
      try
      {
         Class object = Class.forName(jdbcDriverString);
         return (Driver)object.newInstance();
      }
      catch(ClassNotFoundException x)
      {
         throw new DBException("Specified jdbc driver class " + jdbcDriverString + " not found"
                              ,x);
      }
      catch(IllegalAccessException x)
      {
         throw new DBException("Illegal access. Class name: " + jdbcDriverString,x);
      }
      catch(InstantiationException x)
      {
         throw new DBException("Could not instantiate " + jdbcDriverString, x);
      }
  }

  private DBDefinition getDefinition(String dbProfilename )
                           throws com.ai.application.interfaces.ConfigException
  {
    Object dbDef =  m_dbDefinitions.get(dbProfilename);
    if (dbDef != null) return (DBDefinition)dbDef;
    dbDef = new DBDefinition(dbProfilename);
    m_dbDefinitions.put(dbProfilename, dbDef);
    return (DBDefinition)dbDef;
  }
}

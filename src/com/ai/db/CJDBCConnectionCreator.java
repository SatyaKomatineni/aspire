/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;
import java.sql.*;
import java.util.*;
import com.ai.application.utils.AppObjects;

public class CJDBCConnectionCreator implements IJDBCConnectionCreator
{

   static private CJDBCConnectionCreator m_instance = null;
   private CJDBCConnectionCreator(){}
   
   private Hashtable m_dbDefinitions = new Hashtable();
                     // key   -> dbAlias        : string
                     // value -> dbDefinition   : DBDefinition
   private Hashtable m_jdbcDrivers = new Hashtable();
                     // key   -> jdbcDriverName    : string
                     // value -> jdbcDriver        : Driver
                     
  static synchronized public IJDBCConnectionCreator getInstance()
  {
    if (m_instance == null)
      m_instance = new CJDBCConnectionCreator();
   return m_instance;      
  }
  
  synchronized public  Connection createConnection(String dataSourceName)
                              throws DBException
  {
   DBDefinition dbDef = null;
   try 
   {
    // Do I have
    dbDef = getDefinition(dataSourceName);
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
   catch(com.ai.application.interfaces.ConfigException x)
   {
      String errormsg = "Configuration exception";
      StringBuffer msg = new StringBuffer(errormsg);
      msg.append("\n\tRequested connection details :\n\t" + dbDef );
      AppObjects.error(this,msg.toString());
      throw new DBException(errormsg.toString(),x);
   }                        
  }

  private Driver registerDriver(DBDefinition dbDef )
                  throws DBException
                  ,SQLException
  {
   // see if the driver is already registered
    Object driver = m_jdbcDrivers.get(dbDef.getJdbcDriverString());
    if (driver != null ) return (Driver)driver;

   // Create an instance of the jdbc driver
    driver = createJDBCDriver(dbDef.getJdbcDriverString());
    m_jdbcDrivers.put(dbDef.getJdbcDriverString(),(Driver)driver);

    // register the driver as well
    AppObjects.trace(this,"Registering jdbc driver: %1s",dbDef.getJdbcDriverString() );
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
 
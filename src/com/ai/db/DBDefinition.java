/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;
import com.ai.application.utils.*;
import com.ai.application.interfaces.*;

public class DBDefinition
{
  private String m_name;
  private String m_jdbcDriver;
  private String m_connectionString;
  private String m_userid;
  private String m_password;

  public String getDBName() { return m_name; }
  public String getJdbcDriverString() { return m_jdbcDriver; }
  public String getConnectionString() { return m_connectionString; }
  public String getUserid() { return m_userid; }
  public String getPassword() { return m_password; }
    
  public DBDefinition( String inDBAlias)
   throws com.ai.application.interfaces.ConfigException
  {
      IConfig cfg = AppObjects.getIConfig ();
      m_name = cfg.getValue("Database.alias." + inDBAlias );
      m_jdbcDriver = cfg.getValue("Database." + m_name + ".jdbc_driver");
      m_connectionString = cfg.getValue("Database." + m_name + ".connection_string");
      m_userid = cfg.getValue("Database." + m_name + ".userid","");
      m_password = cfg.getValue("Database." + m_name + ".password","");
  }
  public String toString()
  {
      return m_name 
            + "," + m_jdbcDriver 
            + "," + m_connectionString
            + "," + m_userid
            + "," + m_password;
  }
  public static String getValue(String dataSourceAlias, String key, String defaultValue)
  {
  		try
		{
  		String dsname = AppObjects.getValue("Database.alias." + dataSourceAlias);
  		String rvalue = AppObjects.getValue("Database." + dsname + "." + key,defaultValue);
  		return rvalue;
		}
  		catch(ConfigException x)
		{
  			throw new RuntimeException("Database alias not properly defined for " + dataSourceAlias,x);
		}
  }
} 
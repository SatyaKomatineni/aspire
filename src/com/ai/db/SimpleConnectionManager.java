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

public class SimpleConnectionManager implements IConnectionManager, ICreator
{

   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
      
  public SimpleConnectionManager()
  {
  }
  public Connection getConnection(String dataSourceName ) 
                            throws DBException
   {
      AppObjects.log("Creating a new connection to " + dataSourceName);
      return CJDBCConnectionCreator.getInstance().createConnection(dataSourceName);
   }                            
                            
  public void       putConnection(Connection inConnection)
                            throws DBException
   {
      AppObjects.log("Closing connection");
      try {inConnection.close();}
      catch(java.sql.SQLException x)
      {
         throw new DBException("Could not close connection",x);
      }
               
   }                            
} 
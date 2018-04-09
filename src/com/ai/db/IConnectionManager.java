/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
/*
 * Copyright (c) Active Intellect, Inc
 */
package com.ai.db;
import java.sql.*;

/**
 * A common place for obtaining and returning data base connections
 * Provides a way to reuse SQL Connection objects. 
 * Uses the concept of datasources.
 * Datasource is a name indicating the characteristics of database
 * such as userid, password, jdbc driver etc.
 * Access to implemented connection managers are provided to the application
 * as a singleton obtained from the factory sercie. This mechanism
 * provides a way for changing the implementation without recompiling the code.
 * @see  DBDefinition
 * @see  SimpleConnectionManager
 * @see  ConnectionPoolConnectionManager
 * @see  ConnectionPoolConnectionManager1
 */
public interface IConnectionManager
{
   // A symbolic name for retrieving the singleton that implements
   // the IConnectionManager interface.
   public static final String GET_CONNECTION_MANAGER_REQUEST = "AppObjects.connectionManager";
   
  /** Get a connection to the specified datasource
   * @param datasource_name Name of the datasource
   * @return a dedicated java.sql.Connection object
   * @exception com.ai.db.DBException if a connection can not be obtained.
   */
  public Connection getConnection(String datasource ) 
                            throws DBException;
                            
  /** puts the connection back in the pool.
   * Doesn't need to specify the datasource.
   * ConnectionManager implementation is expected to keep track of the datasource
   * that this connection is part of. This way the user doesn't make a mistake of
   * returning connections between different data sources.
   *
   * @param Connection  connection to be returned, irrespective of the datasource
   *                    that it belongs to.
   * @exception com.ai.db.DBException 
   */
   
  public void       putConnection(Connection inConnection)
                            throws DBException;
                            
} 
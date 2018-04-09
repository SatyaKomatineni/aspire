/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;
import java.sql.Connection;

public interface IJDBCConnectionCreator
{
  public Connection createConnection(String datasourceName) throws DBException;
}                                                          

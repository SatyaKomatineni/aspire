/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.db;
import com.ai.common.*;

public class DBException extends CommonException
{
  public DBException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public DBException(String msg )
  {
   this(msg,null);
  }
}             

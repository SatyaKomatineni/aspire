/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.db;

public class SQLInjectionException extends DBException
{
  public SQLInjectionException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public SQLInjectionException(String msg )
  {
   this(msg,null);
  }
}             

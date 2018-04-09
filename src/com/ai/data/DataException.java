/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;

import com.ai.common.*;
public class DataException extends CommonException
{
  public DataException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public DataException(String msg )
  {
   this(msg,null);
  }
}             
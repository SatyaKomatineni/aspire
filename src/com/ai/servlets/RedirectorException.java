/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import com.ai.common.*;
public class RedirectorException extends CommonException
{
  public RedirectorException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public RedirectorException(String msg )
  {
   this(msg,null);
  }
} 



/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.servlets;

import com.ai.common.CommonException;
// derived exceptions
// locate the request in properties files - RequestNotInPro
// Load the class -
// Execute the request -
public class AspireServletException extends CommonException 
{
  public AspireServletException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public AspireServletException(String msg )
  {
   this(msg,null);
  }
} 


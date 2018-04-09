/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.application.interfaces;

import com.ai.common.CommonException;
// derived exceptions
// locate the request in properties files - RequestNotInPro
// Load the class -
// Execute the request -
public class LogHelperException extends CommonException 
{
   private String m_thisMsg;
  public LogHelperException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
   m_thisMsg = msg;
  }
  public LogHelperException(String msg )
  {
   this(msg,null);
  }
  public String getFinalCause(){ return m_thisMsg; }
} 


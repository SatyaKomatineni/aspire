/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.application.interfaces;

public class RequestExecutorResponse {

  static public final boolean SUCCESS = true;
  static public final boolean FAILURE = false;
  private boolean m_returnCode = SUCCESS; 
  private Object m_responseObject;
  
  public RequestExecutorResponse( boolean returnCode) 
  {
   m_returnCode = returnCode;
  }
  public boolean getReturnCode()
  {
   return m_returnCode;
  }
  public void setReturnCode( boolean returnCode ){ m_returnCode = returnCode; }
  public Object getResponseObject()
  {
   return m_responseObject;
  }
  public void setResponseObject( Object inResponseObject )
  {
   m_responseObject = inResponseObject;
  }
} 
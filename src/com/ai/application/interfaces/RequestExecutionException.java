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
public class RequestExecutionException extends CommonException 
{
   static public final String PROPERTIES_FILE_NOT_FOUND = "Properties file not found";
   static public final String REQUEST_NOT_REGISTERED = "Request not registered";
   static public final String CAN_NOT_LOAD_CLASS_FOR_REQUEST = "Can not load class for request";
   static public final String LOADED_CLASS_CAN_NOT_EXECUTE_REQUEST = "Loaded class can not execute request";
   
  public RequestExecutionException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public RequestExecutionException(String msg )
  {
   this(msg,null);
  }
} 



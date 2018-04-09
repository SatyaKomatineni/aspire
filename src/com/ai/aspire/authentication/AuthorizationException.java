/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.authentication;

import com.ai.common.CommonException;
// derived exceptions
// locate the request in properties files - RequestNotInPro
// Load the class -
// Execute the request -
public class AuthorizationException extends CommonException 
{
   static public final String CANNOT_ACCESS_AUTHORIZATION_SERVERS = "Can not access authorization servers";
   static public final String GENERAL_MESSAGE = "General Authorization exception";
   
  public AuthorizationException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public AuthorizationException(String msg )
  {
   this(msg,null);
  }
} 



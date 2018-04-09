/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.authentication;
import javax.servlet.http.*;

import com.ai.servlets.AspireConstants;

public interface IAuthentication 
{
   //Use this string to get a copy of this object
   public static String NAME = AspireConstants.AUTHENTICATION_OBJECT;
   
   public boolean verifyPassword(final String userid, final String passwd ) 
      throws AuthorizationException;
      
   public boolean isAccessAllowed(final String userid, final String resource )
      throws AuthorizationException;
   
   public String getPassword(final String userid)
   throws AuthorizationException;
} 

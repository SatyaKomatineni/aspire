/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.authentication;
import javax.servlet.http.*;

public interface IAuthentication1 extends IAuthentication
{
   public boolean isAccessAllowed(final String userid
                                 , HttpServletRequest request
                                  ,HttpServletResponse response)
      throws AuthorizationException;
   
   public IHttpAuthenticationMethod getHttpAuthenticationMethod()
   throws AuthorizationException;
   
   public String getRealm()
   throws AuthorizationException;
   
   public IPersistentLoginSupport getPersistentLoginSupport()
   throws AuthorizationException;
   
} 
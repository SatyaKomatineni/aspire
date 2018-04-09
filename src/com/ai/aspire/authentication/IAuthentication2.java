/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.authentication;
import javax.servlet.http.*;
public interface IAuthentication2 extends IAuthentication1
{
   public boolean isAPublicURL(HttpServletRequest request
                                  ,HttpServletResponse response)
      throws AuthorizationException;
}

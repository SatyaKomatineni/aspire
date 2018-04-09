/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import com.ai.common.*;
import javax.servlet.http.*;

public class HttpRequestDictionary extends DDictionary
{
   private HttpServletRequest m_request = null;
   public HttpRequestDictionary(HttpServletRequest request) 
   {
      m_request = request;
   }
   /**
    * return null if you can't find the key 
    */
   public Object internalGet(Object key)
   {
      Object value = m_request.getParameter((String)key);
      return value;
   }
} 
/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import com.ai.application.interfaces.*;
import javax.servlet.http.*;
import javax.servlet.*;
import com.ai.application.utils.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import com.ai.common.Tokenizer;
import com.ai.servletutils.*;

public class ExternalRedirector implements IRedirector, ICreator
{
    public Object executeRequest(String requestName, Object args) 
        throws RequestExecutionException
    {
        return this;
    }
   public ExternalRedirector() 
   {
   }
   public void redirect(final String url
                        , HttpServletRequest request
                        , HttpServletResponse response 
                        , ServletContext  servletContext)
      throws   java.net.MalformedURLException,
               java.io.IOException,
               ServletException,
               com.ai.servlets.RedirectorException
   {
      try
      {
         response.sendRedirect(response.encodeRedirectURL(url));
      }
      catch(java.io.IOException x)
      {
         AppObjects.log(x);
         throw new RedirectorException("error.redirect: io error", x);
      }         
   }               
}   
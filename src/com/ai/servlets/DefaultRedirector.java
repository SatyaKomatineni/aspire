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
import com.ai.servlets.compatibility.ServletCompatibility;
import com.ai.servletutils.*;

public class DefaultRedirector implements IRedirector, ICreator
{
    public Object executeRequest(String requestName, Object args) 
        throws RequestExecutionException
    {
        return this;
    }
   public DefaultRedirector() 
   {
   }
   public void redirect(final String url
                        , HttpServletRequest request
                        , HttpServletResponse response 
                        , ServletContext servletContext)
      throws   java.net.MalformedURLException,
               java.io.IOException,
               ServletException,
               com.ai.servlets.RedirectorException
   {
      // figure out what the session is
      // figure out the params (if necessary )
      // Retrieve the params from the url
      // Convert param keys to lowercase
      // sync params with the session
      // Call the dispatcher

      // figure out what the session is
      AppObjects.info(this,"info.redirect: redirecting to %1s",url);
      HttpSession session = request.getSession(false);
      
      // figure out the params (if necessary )
      // Retrieve the params from the url
      String queryString = ServletUtils.getQueryString(request,url);
      Hashtable params;
      if (queryString  != null)
      {      
         //params = HttpUtils.parseQueryString(queryString);
		 params = ServletCompatibility.parseQueryString(queryString);
         params = ServletUtils.convertHashtable(params);
      }
      else
      {
         params = new Hashtable();
      }         
      
      // sync params with the session
      SessionUtils.addParametersToSession( session, params);
      SessionUtils.addParametersFromSessionToUrlParameters(session
                                                      ,params);
      AppObjects.info(this,"values into redirect are: %1s", params.toString());
      // Call the dispatcher
      com.ai.servlets.PageDispatcher pd = new com.ai.servlets.PageDispatcher();
      try{
         pd.serviceRequest( session,params,request,response,servletContext);  
      }
      catch(AspireServletException x)
      {
         throw new RedirectorException("Error from the PageDispatcher.serviceRequest",x);
      }
      return;
   }               
}   
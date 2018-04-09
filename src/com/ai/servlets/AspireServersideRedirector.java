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

import com.ai.servlets.compatibility.ServletCompatibility;
import com.ai.servletutils.*;
import java.util.Map;
import com.ai.common.*;

public class AspireServersideRedirector extends DefaultRedirector implements IRedirector1
{
   public void redirect(final String url
                        , Map params
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
      AppObjects.info(this,"redirect: redirecting to %1s", url);
      HttpSession session = request.getSession(false);

      // figure out the params (if necessary )
      // Retrieve the params from the url
      String queryString = ServletUtils.getQueryString(url);
      Hashtable urlParams;
      if (queryString  != null)
      {
         urlParams = ServletCompatibility.parseQueryString(queryString);
         urlParams = ServletUtils.convertHashtable(urlParams);
      }
      else
      {
         urlParams = new Hashtable();
      }
      //urlParams now contain the over riding params by the user
      //You are going to super impose these on the incoming params
      //urlParams are already lowercased
      //Just merge them now

      params.putAll(urlParams);

      AppObjects.info(this,"redirect: values into redirect are: %1s",params.toString());
      // Call the dispatcher
      com.ai.servlets.PageDispatcher pd = new com.ai.servlets.PageDispatcher();
      try{
         pd.serviceRequest( session,com.ai.common.Utils.getAsHashtable(params),request,response,servletContext);
      }
      catch(AspireServletException x)
      {
         throw new RedirectorException("Error from the PageDispatcher.serviceRequest",x);
      }
      return;
   }
}
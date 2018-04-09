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
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import com.ai.common.Tokenizer;
import com.ai.servlets.compatibility.ServletCompatibility;
import com.ai.servletutils.*;

public class ForwardRedirector implements IRedirector
{
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
    	  AppObjects.trace(this,"forwarding to:%1s", url);
    	  Map paramsFromUrl = this.getParamsFromUrl(url);
    	  AppObjects.trace(this, "params from url:%1s", paramsFromUrl.toString());
    	  
    	  request.setAttribute("redirectParams", paramsFromUrl);
    	  RequestDispatcher dispatcher = servletContext.getRequestDispatcher(url);
    	  dispatcher.forward(request, response);
      }
      catch(java.io.IOException x)
      {
         AppObjects.log(x);
         throw new RedirectorException("error.redirect: io error", x);
      }         
   }               
   private Map getParamsFromUrl(String url) throws MalformedURLException
   {
	      // figure out the params (if necessary )
	      // Retrieve the params from the url
	      String queryString = ServletUtils.getQueryString(url);
	      Hashtable urlParams;
	      if (queryString  != null)
	      {
	         urlParams = ServletCompatibility.parseQueryString(queryString);
	         //convert them to lower case
	         urlParams = ServletUtils.convertHashtable(urlParams);
	      }
	      else
	      {
	         urlParams = new Hashtable();
	      }
	      return urlParams;
   }
}//eof-class   
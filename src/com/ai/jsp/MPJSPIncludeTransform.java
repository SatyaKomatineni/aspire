/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.jsp;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.ai.htmlgen.*;
import javax.servlet.http.*;
import javax.servlet.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.utils.TransformUtils;
import com.ai.common.TransformException;

public class MPJSPIncludeTransform implements IAIHttpTransform, ICreator
{

   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
   public void transform( String htmlFilename
//                          ,PrintWriter writer
                          , IFormHandler formHandler
                          , HttpServletRequest request
                          , HttpServletResponse response
                          , RequestDispatcher dispatcher
                         ) throws java.io.IOException, ServletException
   {
      request.setAttribute("Aspire.formHandler",formHandler);
      dispatcher.include(request,response);
      String curUrl=formHandler.getValue("url");
      String masterPageURL = AppObjects.getValue(curUrl + ".masterPageURL",null);
      if (masterPageURL == null)
      {
    	  AppObjects.trace(this,"No master page enabled for this request");
    	  return;
      }
      try
      {
	      AppObjects.trace(this,"Going to expand the masterpage:%1s", masterPageURL);
	      PrintWriter pw = response.getWriter();
	      Map pmap = this.getParamMapFromRequest(request);
	      TransformUtils.transformHdsMp(masterPageURL,pw,formHandler,pmap);
      }
      catch(TransformException x)
      {
    	  AppObjects.log("Error:Could not transfrom master page",x);
    	  throw new ServletException("Problem transforming the output of jsp",x);
      }
   }
   private Map getParamMapFromRequest(HttpServletRequest request)
   {
	   Map omap = new HashMap();
	   Enumeration e = request.getAttributeNames();
	   while(e.hasMoreElements())
	   {
		   String aname = (String)e.nextElement();
		   Object aValue = request.getAttribute(aname);
		   //String aValue = (String)request.getAttribute(aname);
		   if (aValue instanceof String)
		   {
			   omap.put(aname.toLowerCase(), aValue);
		   }
	   }
	   return omap;
   }
      
} //eof-class

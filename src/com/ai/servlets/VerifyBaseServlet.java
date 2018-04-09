/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.Hashtable;
import java.io.PrintWriter;

import com.ai.servlets.compatibility.ServletCompatibility;
import com.ai.servletutils.*;
import java.util.Enumeration;
import com.ai.common.Utils;

public class VerifyBaseServlet extends BaseServlet
{
   static int i =0;
  public VerifyBaseServlet()
  {
  }
  public long getLastModified(HttpServletRequest request)
  {
      System.out.println("Date header : " + request.getDateHeader("If-Modified-Since"));
      System.out.println("Get Last Modified called ");
      // return 10;
      return System.currentTimeMillis();
  }
   /**
    * Virtual Method to perform service.
    * Resulting HTML or other data is written to the stream out.
   */
   public void service(String user,
                              HttpSession session,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
   {
      System.out.println("Service called :" + i++ );
      // response.setDateHeader("Expires",0);
      // response.setDateHeader("Last-Modified",2000);
      PrintUtils.writeHeader(out);     
      PrintUtils.writeMessage(out,"uri : " + uri);
      PrintUtils.writeMessage(out,"URL : " + ServletCompatibility.getRequestURL(request));
      PrintUtils.writeMessage(out,"\nquery: " + query);
      PrintUtils.writeMessage(out, "\nParameters follow<hr>");
      for (Enumeration e=parameters.keys();e.hasMoreElements();)
      {
      String key = (String)e.nextElement();
      PrintUtils.writeMessage(out, "\nkey : " + key + ", value : " + parameters.get(key) );
      }
      PrintUtils.writeFooter(out);
      // Print request headers
      PrintUtils.writeMessage(out,"<em><bold>Request headers follow</bold></em><hr>");
      PrintUtils.writeMessage(out,ServletUtils.convertToHtmlLines(Utils.convertToString(request.getHeaderNames())));;

      // Header values follow      
      PrintUtils.writeMessage(out,"<em><bold>Header values follow<bold></em><hr>");
      for(Enumeration headerNames=request.getHeaderNames();headerNames.hasMoreElements();)
      {
         String headerName = (String)headerNames.nextElement();
         String header = request.getHeader(headerName);
         PrintUtils.writeMessage(out,"\n" + headerName + ":" +  header );
      }
      
      // print session variables          
      PrintUtils.writeMessage(out,"<em><bold>Session values follow<bold></em><hr>");
      Enumeration names = ServletCompatibility.getSessionValueNames(session);
      while(names.hasMoreElements())
      {
         String key = (String)names.nextElement();
         Object obj = ServletCompatibility.getSessionValue(session,key);
         PrintUtils.writeMessage(out,"<em>" + key + "</em> :" + obj.toString());
      }
   }                                
}      
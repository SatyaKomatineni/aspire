/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.testservlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class LocateServlet extends HttpServlet
{

  //Initialize global variables
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
  }

  //Process the HTTP Get request
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter (response.getOutputStream());
    out.println("<html>");
    out.println("<head><title>LocateServlet</title></head>");
    out.println("<body>");

    // Listing of all the loaded servlets
    
    // Get the name of the servlet from the argument
    String servletName = request.getParameter("name");
    if (servletName == null)
    {
      out.println("Specify a name via name=value");
    }
    else
    {
       out.println("Looking for " + servletName );
       // obtain the servlet context
       System.out.println("Getting servlet context");
       ServletContext context = getServletContext();

       for(Enumeration e=context.getServletNames();e.hasMoreElements();)
       {
         String name = (String)e.nextElement();
         out.println("<br>" + name);
       }
       
       Servlet servlet = context.getServlet(servletName);
       if (servlet == null)
       {
         out.println("Can not find a servlet with that name");
       }
       else
       {
          out.println("Found the following object " + servlet.getClass().getName());
       }
    }    
    out.println("</body></html>");
    out.close();
  }

  //Process the HTTP Post request
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter (response.getOutputStream());
    out.println("<html>");
    out.println("<head><title>LocateServlet</title></head>");
    out.println("<body>");
    out.println("</body></html>");
    out.close();
  }

  //Get Servlet information
  public String getServletInfo()
  {
    return "com.ai.testservlets.LocateServlet Information";
  }
} 
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

public class EchoParameters extends HttpServlet
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
    out.println("<head><title>EchoParameters</title></head>");
    out.println("<body>Hello From EchoParameters doGet()");
    out.println("command = " + request.getParameter("command"));
    out.println("client_gid = " + request.getParameter("client_gid"));
    out.println("</body></html>");
    out.close();
  }
//Process the HTTP Post request
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter (response.getOutputStream());
    out.println("<html>");
    out.println("<head><title>EchoParameters</title></head>");
    out.println("<body>");
    out.println("</body></html>");
    out.close();
  }
//Get Servlet information
  
  public String getServletInfo()
  {
    return "com.ai.testservlets.EchoParameters Information";
  }
}

 
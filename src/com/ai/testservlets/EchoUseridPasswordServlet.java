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
	
public class EchoUseridPasswordServlet extends HttpServlet
{
  public void init(ServletConfig config) 
	  throws ServletException 
  {
    super.init(config);
  }
  public void doGet(HttpServletRequest request
					 ,HttpServletResponse response) 
	  throws ServletException, IOException 
  {
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter (response.getOutputStream());
    out.println("<html>");
    out.println("<head><title>Hello world</title></head>");
    out.println("<body>");
	out.println("Hello world");
    out.println("</body></html>");
    out.close();
  }
//Get Servlet information
  
  public String getServletInfo() {
    return "com.vtac.contactcase.servlets.TestServlet Information";
  }
}

 	


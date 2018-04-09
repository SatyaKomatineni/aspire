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
import sun.misc.BASE64Decoder;

public class SnoopAuthorization extends HttpServlet
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

    String auth = request.getHeader("Authorization");
    String userName = request.getRemoteUser();
    if (auth == null)
    {
      System.out.println("requesting authorization");
      // force authorization
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      response.setHeader("WWW-Authenticate","BASIC realm=\"defaultRealm\"");      
    }
    else
    {
       String authType = auth.substring(0,5);
       String userPassEncoded = auth.substring(6);
       // decode the userPass
       BASE64Decoder dec = new BASE64Decoder();
       String userPassDecoded = new String(dec.decodeBuffer(userPassEncoded));
       
       out.println("<html>");
       out.println("<head><title>SnoopAuthorization</title></head>");
       out.println("<body>");

       out.println("\nauthroization: " + auth );
       out.println("\nAuthorization type: " + authType);
       out.println("\nuserPass: " + userPassDecoded);
       out.println("\remoteuser: " + request.getRemoteUser() );

       out.println("</body></html>");
       out.close();
    }
  }

  //Process the HTTP Post request
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter (response.getOutputStream());
    out.println("<html>");
    out.println("<head><title>SnoopAuthorization</title></head>");
    out.println("<body>");
    out.println("</body></html>");
    out.close();
  }

  //Get Servlet information
  public String getServletInfo()
  {
    return "com.ai.testservlets.SnoopAuthorization Information";
  }
} 
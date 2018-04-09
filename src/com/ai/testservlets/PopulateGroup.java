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
import com.ai.servletutils.*;


public class PopulateGroup extends HttpServlet
{

  //Initialize global variables
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
//    ApplicationHolder.setDefaultConfigFile("g:\\cb\\com\\ai\\application\\test\\TestAppConfig.properties");
  }

  //Process the HTTP Get request
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter (response.getOutputStream());
    String command = request.getParameter("command");
    String client_gid =  request.getParameter("client_gid");
    out.println(writeHtml(command,client_gid));
    out.close();
  }

  //Process the HTTP Post request
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter (response.getOutputStream());
    out.println("<html>");
    out.println("<head><title>PopulateGroup</title></head>");
    out.println("<body>");
    out.println("</body></html>");
    out.close();
  }

  //Get Servlet information
  public String getServletInfo()
  {
    return "com.ai.testservlets.PopulateGroup Information";
  }
  private String writeHtml( String command, String gid )
  {
   StringBuffer buf = new StringBuffer();
   ServletUtils.appendHtmlHeader(buf);
   ServletUtils.appendJSHeader(buf);
      //function addGroup( clientGroupId, inGroupName, inServerGroupId )
   buf.append("parent.addGroup(" 
            + gid 
            + "," + ServletUtils.quote(command) 
            + "," + ServletUtils.quote("server_id") 
            + ");\n");
   buf.append("parent.addGroup(" 
            + gid 
            + "," + ServletUtils.quote(command) 
            + "," + ServletUtils.quote("server_id") 
            + ");\n");
   buf.append("parent.addGroup(" 
            + gid 
            + "," + ServletUtils.quote(command) 
            + "," + ServletUtils.quote("server_id") 
            + ");\n");
   buf.append("parent.redrawGroup(" 
            + gid 
            + ");\n");
   ServletUtils.appendJSTail(buf);            
   ServletUtils.appendHtmlTail(buf);
   return buf.toString();
  }
} 

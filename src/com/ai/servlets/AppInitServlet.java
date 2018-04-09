/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.ai.application.defaultpkg.ApplicationHolder;
import com.ai.application.utils.AppObjects;

public class AppInitServlet extends HttpServlet
{
   static String m_configFilename = null;
  private static ServletContext m_servletContext = null;
  static public ServletContext getAppServletContext(){ return m_servletContext; }
                
  //Initialize global variables
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    
    m_configFilename = config.getInitParameter("AppConfigFilename");
    if (m_configFilename == null)
      m_configFilename = "test";
    System.out.println(com.ai.aspire.AspireReleases.getCurRelease());
    System.out.println("Initializing config file " + m_configFilename );
    System.out.flush();
    ApplicationHolder.initApplication(m_configFilename,null);
    AppObjects.log(com.ai.aspire.AspireReleases.getCurRelease());
    AppObjects.log("Application Initialized with config file: " + m_configFilename );
//    System.runFinalizersOnExit(true);
    // Initialize the servlet context
    AppObjects.log("info: Initializing Servlet context for AppInitServlet");
    m_servletContext = config.getServletContext();
  }

  //Process the HTTP Post request
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter (response.getOutputStream());
    out.println("<html>");
    out.println("<head><title>AppInitServlet</title></head>");
    out.println("<body>");
    out.println(m_configFilename);
    out.println("</body></html>");
    out.close();
  }
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = new PrintWriter (response.getOutputStream());
    out.println("<html>");
    out.println("<head><title>AppInitServlet</title></head>");
    out.println("<body>");
    out.println(m_configFilename);
    try {
      out.println(AppObjects.getValue("Logging.logfile"));
    }
    catch(com.ai.application.interfaces.ConfigException x)
    {
      out.println("Can't find config info for log file");
    }
    out.println("</body></html>");
    out.close();
  }

  //Get Servlet information
  public String getServletInfo()
  {
    return "com.ai.servlets.AppInitServlet Information";
  }
} 
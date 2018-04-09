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
import com.ai.common.*;

/**
 * 
 * 11/23/15
 * **********************
 * As of now mostly appinitservlet1 is used
 * I may have to invent a AppInitServlet1Secure version
 * Where the later one will allow some config files to be
 * under web-inf sub directory.
 * 
 * 12/4/15
 * ************
 * Not necessary because the default directory definition for aspire
 * also can read from web-inf 
 *
 */
public class AppInitServlet1 extends HttpServlet
{
	static int s_majorVer = 0;
	static int s_minorVer = 0;

   static String m_configFilename = null;
  private static ServletContext m_servletContext = null;
  static public ServletContext getAppServletContext(){ return m_servletContext; }

  //Initialize global variables
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);

    // obtain the servlet context
    m_servletContext = config.getServletContext();
    ServletContextHolder.initialize(m_servletContext);

	s_majorVer = m_servletContext.getMajorVersion();
	s_minorVer = m_servletContext.getMinorVersion();
	System.out.println("Aspire: Running servlet API:" + s_majorVer + "." + s_minorVer);


    m_configFilename = getAspirePropertiesFile(config,m_servletContext);
    if (m_configFilename == null)
    {
      System.out.println("Fatal: Could not locate the aspire.properties to start.\nPlease shutdown and mention a valid properties file");
      m_configFilename = getRelativeDefaultPropertiesFilename();
    }
    System.out.println(com.ai.aspire.AspireReleases.getCurRelease());
    System.out.println("Initializing config file " + m_configFilename );
    System.out.flush();

    ApplicationHolder.initApplication(m_configFilename,getBootStrapProperties(config),null);

    AppObjects.log(com.ai.aspire.AspireReleases.getCurRelease());
    AppObjects.log("Application Initialized with config file: " + m_configFilename );
    System.out.println("\nAspire initialized");
  }

  private Properties getBootStrapProperties(ServletConfig servletConfig)
  {
     //Place the root directory
        String rootPath = m_servletContext.getRealPath("/");
        if (rootPath == null)
        {
           //This app server is not returning getRealPath
           System.out.println("Warn:AppInitServelet1 Could not determine the root path of the application");
           System.out.println("Info:Use directories.aspire in the properties file to define the root of the application");
           return new Properties();
        }
        Properties p = new Properties();
        p.put("directories.aspire",rootPath.substring(0,rootPath.length()-1));
        //Call to child classes to load their own directory aliases
        addAdditionalDirectories(p);

		//Place the version numbers
		p.put("aspire.servletapi.majorversion",Integer.toString(s_majorVer));
		p.put("aspire.servletapi.minorversion",Integer.toString(s_minorVer));

		//If the version is greater than 3 place the webapp context name
		if (s_minorVer > 2)
		{
			//Place the web app
			String webAppContext = servletConfig.getServletContext().getServletContextName();
			if (webAppContext == null)
			{
			   System.out.println("Error:Could not determine the web application context. Use aspirecontext in the properties file");
			   webAppContext="";
			}
			p.put("aspirecontext",webAppContext);
		}
        return p;
  }

   private void test()
   {
      System.out.println("Root directory for the context" + m_servletContext.getRealPath("/"));
   }

   /**
    * Preferred way specify the properties file
    *
    * 1. Don't specify at all: will be <context_path>/properties/aspire.properties
    * 2. Specify relative path without the separator: properties/aspire.propertis
    * 3. Specify absolute path
    *
    * Returns null if the path is not found
    */
   private String getAspirePropertiesFile(ServletConfig config, ServletContext context)
   {
      //Retrieve config filename
      String propertiesFilename = config.getInitParameter("AppConfigFilename");

      //Nothing specified, return the default root/properties/aspire.properties
      if (propertiesFilename == null)
      {
         System.out.println("No properties file mentioned. Going to use aspire.properties as the properties file");
         return getDefaultPropertiesFile(context);
      }

      //Config filename specified, see if it is absolute
      System.out.println("Trying for properties file at:" + propertiesFilename);
      if (FileUtils.exists(propertiesFilename))
      {
         // absolute file exists on the file system
         return propertiesFilename;
      }

      //Specified filename doesn't exist, it may be relative, let us try that

      String rootPath = context.getRealPath("/");
      System.out.println("Root path :" + rootPath);
      String sep = File.separator;

      if (rootPath == null)
      {
         System.out.println("Fatal: No properties file mentioned,and can't obtain the root context path");
         rootPath = File.separator;
      }

      // try with separator
      String fullPath = rootPath + propertiesFilename;
      System.out.println("Trying for properties file at:" + fullPath);
      if (FileUtils.exists(fullPath))
      {
         return fullPath;
      }

      // try with out separator
      fullPath = rootPath.substring(0,rootPath.length()-2) + propertiesFilename;
      System.out.println("Trying for properties file at:" + fullPath);
      if (FileUtils.exists(fullPath))
      {
         return fullPath;
      }

      // try with separator in the default directory
      fullPath = rootPath + "properties" + sep + propertiesFilename;
      System.out.println("Trying for properties file at:" + fullPath);
      if (FileUtils.exists(fullPath))
      {
         return fullPath;
      }

      // try with out separator in the default directory
      fullPath = rootPath.substring(0,rootPath.length()-2) + "properties" + sep + propertiesFilename;
      System.out.println("Trying for properties file at:" + fullPath);
      if (FileUtils.exists(fullPath))
      {
         return fullPath;
      }
      // fatal error, can not determine the properties file
      return null;
   }

   private String getDefaultPropertiesFile(ServletContext context)
   {
      String contextRootPath = context.getRealPath("/");
      if (contextRootPath == null)
      {
         System.out.println("Error: Unable to retrieve the root context path from the servlet container");
         return getRelativeDefaultPropertiesFilename();
      }
      // contextrootpath available
      return contextRootPath + getRelativeDefaultPropertiesFilename();
   }
   private String getRelativeDefaultPropertiesFilename()
   {
      String sep = File.separator;
      return "properties" + sep + "aspire.properties";
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
  
  /**
   * You can define additional directory aliases.
   * Call Parent first in derived classes.
   * 
   * @param p - properties to be filled in
   * 
   */
  protected void addAdditionalDirectories(Properties p)
  {
	  
  }
}

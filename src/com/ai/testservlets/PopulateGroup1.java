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

// ai related imports
import com.ai.servletutils.*;
import com.ai.application.utils.*;
import com.ai.application.defaultpkg.*;
import com.ai.data.*;
import com.ai.common.*;

public class PopulateGroup1 extends HttpServlet
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
    String command = request.getParameter("command");
    String client_gid =  request.getParameter("client_gid");

   try
   {
       StringBuffer buf = getJSForMainGroup();
       ServletUtils.writeOutAsJS(out,buf);
   }
   catch(Exception x)
   {       
      x.printStackTrace();
//      PrintUtils.writeCompleteMessage(out,"error");
      PrintUtils.writeHeader(out);
      PrintUtils.writeException(out,x);
      PrintUtils.writeFooter(out);
   }
   finally
   {
      out.close();
   }       
   
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
  private StringBuffer getJSForMainGroup() 
         throws com.ai.data.DataException, com.ai.application.interfaces.RequestExecutionException
  {

      ApplicationHolder.initApplication("g:\\cb\\com\\ai\\application\\test\\TestAppConfig.properties",null);
      AppObjects.log("In getJSMaingroup");
      StringBuffer buf = new StringBuffer();
      // initialize the root group
      buf.append("rootGroup = parent.initGroup(\"root\",parent.right.document);");

      // get additional expanded groups
      IDataCollection tCol = (IDataCollection)AppObjects.getIFactory().getObject("GET_TRADELANES",null);
      IIterator itr = tCol.getIIterator();      
      AppObjects.log("before for");      
      for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
      {
         String curElem = (String)itr.getCurrentElement();
         buf.append("\nparent.addGroup(rootGroup.id"
                    + "," +  ServletUtils.quote(curElem)
                    + "," +  ServletUtils.quote("GET_LOCATIONS") 
                    + ");" );
      }
      // Go ahead and redraw the group
//      buf.append("\nparent.redrawGroup(rootGroup.id);");
      return buf;      
  }
} 

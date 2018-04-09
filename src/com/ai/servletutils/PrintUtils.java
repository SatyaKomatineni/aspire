/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.servletutils;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.ai.application.utils.*;
import com.ai.aspire.authentication.AuthorizationException;

public class PrintUtils {

  public PrintUtils() {
  }
  static public void writeException(PrintWriter out, java.lang.Throwable x)
  {
    if (out == null) return;
    out.println("<p>");
    x.printStackTrace(out);
    out.println("</p>");
   return;
  }
  static public void writeAuthorizationException(PrintWriter out, AuthorizationException x)
  {
    if (out == null) return;
    out.println("<p>Authorization Denied. See the exception below.</p><hr/>");
    out.println("<p>");
    x.printStackTrace(out);
    out.println("</p>");
   return;
  }
  static public void writeMessage(PrintWriter out, String str)
  {
   if (out  == null) return;
    out.println("<p>" + str + "</p>");
   return;
  }
  static public void writeHeader(PrintWriter out)
  {
   if (out  == null) return;
    out.println("<html>");  
    out.println("<head><title>TestServlet</title></head>");
    out.println("<body>");
   return;
  }
  static public void writeFooter(PrintWriter out)
  {
   if (out  == null) return;
    out.println("</body></html>");
   return;
  }
  static public void writeCompleteMessage(PrintWriter out, String str)
  {
   if (out  == null) return;
    PrintUtils.writeHeader(out);
    out.println("<p>" + str + "</p>");
    PrintUtils.writeFooter(out);
   return;
  }
   
  static public void sendCompleteMessage(HttpServletResponse response, String str)
  {
    try
    {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    PrintUtils.writeHeader(out);
    out.println("<p>" + str + "</p>");
    PrintUtils.writeFooter(out);
    out.close();    
    }
    catch(java.io.IOException x)
    {
      AppObjects.log("error: Could not send response out");
      AppObjects.log(x);
    }
  }
  
  static public void sendException(HttpServletResponse response, java.lang.Throwable x)
  {
    try
    {
       PrintWriter out = response.getWriter();
       PrintUtils.writeHeader(out);
       PrintUtils.writeMessage(out,x.getMessage());
       out.println("<p>");
       x.printStackTrace(out);
       out.println("</p>");
       PrintUtils.writeFooter(out);
       out.close();    
    }
    catch(java.io.IOException y)
    {
      AppObjects.log("error: Could not send response out");
      AppObjects.log(y);
    }
  }
}  

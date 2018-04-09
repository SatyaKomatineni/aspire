/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import com.ai.application.interfaces.*;
import javax.servlet.http.*;
import javax.servlet.*;
import com.ai.application.utils.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import com.ai.common.*;
import com.ai.servletutils.*;

public class ClientSideRedirector1 implements IRedirector, ICreator
{
    public Object executeRequest(String requestName, Object args)
        throws RequestExecutionException
    {
        return this;
    }
   public void redirect(final String url
                        , HttpServletRequest request
                        , HttpServletResponse response
                        , ServletContext  servletContext)
      throws   java.net.MalformedURLException,
               java.io.IOException,
               ServletException,
               com.ai.servlets.RedirectorException
   {
      try
      {
         writeToOutputStream(response, url);
      }
      catch(java.io.IOException x)
      {
         AppObjects.log(x);
         throw new RedirectorException("error.redirect: io error", x);
      }
      return;
   }
   private void writeToOutputStream(HttpServletResponse response, String targetUrl)
      throws IOException
   {
      PrintWriter out = ServletUtils.prepareResponseAndGetPrintWriter(response,null);
//      targetUrl = targetUrl.replaceAll(regExp, "\\\\\"");
      targetUrl = StringUtils.encode(targetUrl,'\\','"','"');
      AppObjects.info(this,"ClientSideRedirector1: redirectURL after escaping is:%1s",targetUrl);

      out.println("<html><head>");
      out.println("<script>");
      out.println("function redirectToTarget()");
      out.println("{");
      out.println("   targetUrl = \"" + targetUrl + "\";");
      out.println("   if (targetUrl == \"\")");
      out.println("   {");
      out.println("      alert(\"No target url specified.\");");
      out.println("      return;");
      out.println("   }");
      out.println("   else");
      out.println("   {");
      out.println("      location.replace(targetUrl);");
      out.println("   }");
      out.println("}");
      out.println("</script>");
      out.println("</head>");
      out.println("<body onLoad=redirectToTarget()>");
      out.println("</body>");
      out.println("</html>");
      out.close();
   }
}

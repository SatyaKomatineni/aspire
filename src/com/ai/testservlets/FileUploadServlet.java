/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.testservlets;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.Hashtable;
import java.io.PrintWriter;
import com.ai.servletutils.*;
import java.util.Enumeration;

import com.ai.servlets.BaseServlet;
// import com.oreilly.servlet.*;

public class FileUploadServlet extends BaseServlet
{

   /**
    * Virtual Method to perform service.
    * Resulting HTML or other data is written to the stream out.
   */
   public void service(String user,
                              HttpSession session,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
   {


      try
      {
         // 
         MultipartRequest fileUploadRqst = new MultipartRequest(request
                     ,"d:\\work"
                     ,new CMultipartRequestListner());
      
          PrintUtils.writeHeader(out);
          PrintUtils.writeMessage(out,"uri : " + uri);
          PrintUtils.writeMessage(out,"\nquery: " + query);
          PrintUtils.writeMessage(out, "\nParameters follow");
          for (Enumeration e=parameters.keys();e.hasMoreElements();)
          {
            String key = (String)e.nextElement();
            PrintUtils.writeMessage(out, "\nkey : " + key + ", value : " + parameters.get(key) );
          }
          // Parameters from the multippart request guy
          PrintUtils.writeMessage(out, "\nMultipart request parameters follow");
          for (Enumeration e=fileUploadRqst.getParameterNames();e.hasMoreElements();)
          {
            String key = (String)e.nextElement();
            String value = fileUploadRqst.getParameter(key);
            PrintUtils.writeMessage(out, "\nkey : " + key + ", value : " + value );
          }
          PrintUtils.writeFooter(out);
      }
      catch(java.io.IOException x)
      {  
         com.ai.application.utils.AppObjects.log(x);
         x.printStackTrace();
      }          
   }                                
}      
class CMultipartRequestListner implements IMultipartRequestListner
{
   public boolean beginProcess(HttpServletRequest request, MultipartInputStreamHandler stream)
   {
      return true;
   }
   public boolean newParameter(String name, String value )
   {
      System.out.println("New parameter : " + name + ": " + value);
      return true;
   }
   public boolean newFile(String filename)
   {
      return true;
   }
   public String suggestAFilename(String filename )
   {
      System.out.println("Filename detected : " + filename );
      return filename;
   }
}

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.StringTokenizer;
import com.ai.application.utils.AppObjects;
import com.ai.servletutils.*;

public abstract class DemuxServlet extends HttpServlet
{
   String name = null;

  //Initialize global variables
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    AppObjects.log("Initializing " + getClass().getName() );
    String servletGivenName = getName();
    if (servletGivenName != null)
    {
      // register with the repository
      AppObjects.log(servletGivenName + " is being registered.");
      CSimpleServletRepository.getInstance().setObject(servletGivenName, this);
    }
    
  }

  //Process the HTTP Get request
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
      run(request,response);
  }

  //Process the HTTP Post request
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
      run(request,response);
  }

  private String getUser(HttpServletRequest request )
  {
    AppObjects.log("Inside get user");
    String auth = request.getHeader("Authorization");
    AppObjects.log("Authorization :" + auth);
    if (auth == null) return null;
    
//    String userName = request.getRemoteUser();
//    String authType = auth.substring(0,5);
   try
    {
      String userPassEncoded = auth.substring(6); 
    // decode the userPass
       sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
       String userPassDecoded = new String(dec.decodeBuffer(userPassEncoded));
       AppObjects.log( "Userid + password :" + userPassDecoded);
       StringTokenizer tokenizer = new StringTokenizer(userPassDecoded,":");
       if (!tokenizer.hasMoreTokens()){ return null; }
       return tokenizer.nextToken();
    }
    catch(IOException x)
    {
      log(x.toString());
      return null;
    }
    catch (IndexOutOfBoundsException x)
    {
      log(x.toString());
      return null;
    }
  }   
   /**
    * Service Routine
    * called by WAI on a new thread for each request
   */
   public int run(HttpServletRequest request,
                  HttpServletResponse response)
   {
    
      try
      {
      response.setContentType("text/html");
      AppObjects.log("Received a run request for servlet : " + getName() );
         // build buffered output stream for WAI request
         //      AlsWaiOutputStream rOut = new AlsWaiOutputStream(request);
      PrintWriter out = new PrintWriter (response.getOutputStream());
         
      String user = getUser(request);
      AppObjects.log("user :" + user );
      if ( user == null)
      {
         AppObjects.log("Forcing authentication as user is null in servlet: " + getName() );
         // Force authentication
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.setHeader("WWW-authenticate","Basic realm=\"AI\"");
         return 0;
      }

      String uri = request.getRequestURI();
      AppObjects.log("uri :" + uri );
      String urlQuery = request.getQueryString();
      AppObjects.log("urlQuery :" + urlQuery );
      
      Hashtable parameters = ServletUtils.parseQueryString(urlQuery);
      
      AppObjects.log(urlQuery+" for:"+user);

      // set up for long processing
//      request.addResponseHeader("Connection", "keep-alive");
//      request.StartResponse();

      AppObjects.log("user : " + user );
      AppObjects.log( "uri : " + uri );
      AppObjects.log( "urlQuery : " + urlQuery );
      // process request
      service(user,uri,urlQuery,parameters,out,request,response);
      
      // ,request);

         // done, flush stream
         out.flush();
         out.close();
      }
      catch(Throwable t)
      {
         AppObjects.log(t);
		}

      return 0;
   }


   /**
    * Virtual Method to perform service.
    * Resulting HTML or other data is written to the stream out.
   */
   public abstract void service(String user,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response );

   /**
    * Introduced to locate servllets 
    * based on their name. Incase of WAI this name could be used to 
    * register the servlet with WAI.
    * Incase of servlets, this name is used to register them in to a context.
   */
   public String getName()
   {
      try 
      {
        // Default implementation of this method would use
        // the property file if available
        String thisClassname = this.getClass().getName();
        //Locate the ordained name for this from
        return AppObjects.getValue("servlets." + thisClassname);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         AppObjects.log(x);
         return null;
      }
   }

   /**
    * Log data to the output log in a standard format
   */

   public void log(String msg)
   {
      AppObjects.log(getName() + ":" +msg);
   }



   /**
    * decode encoded URL string
   */
   public static String URLDecode(String s)
   {
      String result = "";
      String remaining = new String(s);

      int index = remaining.indexOf('%');
      while (-1 != index)
      {
         result = result.concat(remaining.substring(0,index));
         String trans = remaining.substring(index+1,index+3);
         remaining = remaining.substring(index+3);
         byte b[] = new byte[1];
         b[0] = Byte.parseByte(trans,16);
         result = result + new String(b);

         index = remaining.indexOf('%');
      }
      result = result.concat(remaining);

      return result;
   }
}



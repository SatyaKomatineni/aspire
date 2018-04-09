/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.authentication.AuthorizationException;
import com.ai.servlets.compatibility.ServletCompatibility;
import com.ai.servletutils.*;

public abstract class BaseServlet extends HttpServlet implements  IDerivedServletConfigHooks
{
   String name = null;

  //Initialize global variables
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    AppObjects.info(this,"Initializing %1s",getClass().getName() );
    String servletGivenName = getName();
    if (servletGivenName != null)
    {
      // register with the repository
      AppObjects.info(this,"%1s is being registered.",servletGivenName);
      CSimpleServletRepository.getInstance().setObject(servletGivenName, this);
    }
  }

  //Process the HTTP Get request
  public void doGet(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
  {
      run(request,response);
  }

  //Process the HTTP Post request
  public void doPost(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
  {
      run(request,response);
  }

   private Hashtable convertToLowerCase(Hashtable inHashtable)
   {
      Hashtable table = new Hashtable();
      if (inHashtable == null) return table;
      
      for(Enumeration e=inHashtable.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         table.put(key.toLowerCase(),inHashtable.get(key));
      }
      return table;
   }

   public int run(HttpServletRequest request,
                  HttpServletResponse response)
      throws ServletException, IOException
   {

      PrintWriter out = null;
      HttpSession session = null;
      Hashtable parameters = null;
      String user = null;
      try
      {
      AppObjects.info(this,"BaseServlet servicing: %1s",ServletCompatibility.getRequestURL(request));

      // Get the session object
      // Create the session object if it doesn't exist
      session = SessionUtils.getSession(request,response);
      
      //9/22/13: The following code will establish a session
      //if not a challenge would have been set on the response
      //returning here if session is null does that.
      //in other words if session is null request authorization
      //for public urls there is always a session
      //for urls that require a login but not logged in this will return 0
      //See DefaultSessionSupportX classes to see how this is implemented
      if (session == null){   return 0;    }
      SessionUtils.initializeSession(session,request,response);

      if (SWIHttpEvents.beginRequest(request,response) == false)
      {
         AppObjects.warn(this,"BaseServlet: Begin request returned a false. Request will be terminated.");
         return 0;
      }

      // get user, uri, query
      //String user = (String)session.getValue("profile_user");
      user = ServletUtils.getUser(request);

      String uri = request.getRequestURI();
      String urlQuery = request.getQueryString();

      // Report the above
      AppObjects.info(this,"uri :%1s", uri );
      AppObjects.info(this,"urlQuery :%1s",urlQuery );

      parameters = ServletUtils.getParameters(request);
      // If user has no access return
      boolean bAccessAllowed = ServletUtils.isAccessAllowed(user
                                 ,request
                                 ,response);
      if (!bAccessAllowed)  
      {
      	AppObjects.warn(this,"Access denied to this url");
      	throw new AuthorizationException("Authorization Denied");
      }

      // ServletUtils.parseQueryString(urlQuery);
      // Determine content type and obtain the stream
      // response.setContentType(getContentType(parameters));
      // out = new PrintWriter (response.getOutputStream());
      out = qhdGetPrintWriter(request,response);
      if (out!=null)
      {
         // Register the printwriter for future retrievals
         request.setAttribute(AspireConstants.PER_REQUEST_PRINT_WRITER,out);
      }
      // process request
      service(user
            ,session
            ,uri
            ,urlQuery
            ,convertToLowerCase(parameters)
            ,out
            ,request
            ,response);

      // ,request);

      }
      catch(IOException iox)
      {
    	  AppObjects.log("Error: io exception",iox);
          IOException x = GlobalExceptionHandler.dealWithIOException(iox, user, session
							            ,convertToLowerCase(parameters)
							            ,out, request, response);
          if (x != null) throw iox;
      }
      catch(ServletException sx)
      {
    	  AppObjects.log("Error: servlet exception",sx);
          ServletException x = GlobalExceptionHandler.dealWithServletException(sx, user, session
		            ,convertToLowerCase(parameters)
		            ,out, request, response);
          if (x != null) throw sx;
      }
      catch(Throwable t)
      {
    	  AppObjects.log("Throwable in the base servlet", t);
          Throwable rt = GlobalExceptionHandler.dealWithThrowable(t, user, session
		            ,convertToLowerCase(parameters)
		            ,out, request, response);
          if (rt != null) 
          {
              throw new ServletException("Wrapped throwable in base servlet",rt);
          }
      }
      finally
      {
         try
         {
            SWIHttpEvents.endRequest(request,response);
         }
         catch(Throwable t)
         {
            AppObjects.log("Error:EndRequest exception",t);
         }
         // done, flush stream if needed
         this.flushAndCloseIfNeeded(out);
      }

      return 0;
   }

   private void flushAndCloseIfNeeded(PrintWriter out) throws IOException
   {
       String closeStreamOption = AppObjects.getValue("aspire.servlets.closestream","yes");
       AppObjects.trace(this,"closestream option is:%1s", closeStreamOption);
       if (closeStreamOption.equalsIgnoreCase("no"))
       {
           AppObjects.trace(this,"Not flusing or closing the stream");
           return;
       }
       //Requested to flush and close the stream
       if (out != null)
       {
          AppObjects.trace(this,"Going to flush the stream");
          out.flush();
          out.close();
       }
       return;
   }

   /**
    * Virtual Method to perform service.
    * Resulting HTML or other data is written to the stream out.
    * Take in to account the fact that the user object could be null
    * Use the session object to store the parameters
    * Remove the dependency on the user profiles
    * UserProfile will effectively become the session
    *
    * There is a problem with passing PrintWriter
    * Some times it is too early to work with it
    * Servlets go into an invalid state
    *
    * I am going to try and see if I don't pass it.
   */
   public abstract void service(String user
                                ,HttpSession session
                                ,String uri
                                ,String query
                                ,Hashtable parameters
                                ,PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
   throws ServletException, IOException;

   /**
    * Introduced to locate servllets
    * based on their name. Incase of WAI this name could be used to
    * register the servlet with WAI.
    * Incase of servlets, this name is used to register them in to a context.
   */
   public String getName()
   {
        // Default implementation of this method would use
        // the property file if available
        String thisClassname = this.getClass().getName();
        //Locate the ordained name for this from
        return AppObjects.getIConfig().getValue("servlets." + thisClassname,null);
   }
   private String getContentType( Hashtable parameters )
   {
      String url = (String)parameters.get("url");
      if (url == null)
      {
         return "text/html";
      }
      String contentType = AppObjects.getIConfig().getValue(url + ".contentType", new String("text/html"));
      return contentType;
   }
   /**************************************************************
    * Default Implementation for hooks
    **************************************************************
    */
   public PrintWriter qhdGetPrintWriter(HttpServletRequest request, HttpServletResponse response)
      throws IOException
   {
      return ServletUtils.prepareResponseAndGetPrintWriter(response,qhdGetContentType(request,response));
   }
   public String qhdGetContentType(HttpServletRequest request, HttpServletResponse response)
   {
      return "text/html";
   }
}




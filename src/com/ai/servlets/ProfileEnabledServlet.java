/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.htmlgen.ISecureVariables;
import com.ai.servlets.compatibility.ServletCompatibility;

public abstract class ProfileEnabledServlet extends BaseServlet 
{

   private ISecureVariables m_secureVariables = null;
   
  //Initialize global variables

  /**
   * Cache the SecureVariables Object
   */
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    try
    {
       m_secureVariables = (ISecureVariables)AppObjects.getIFactory().getObject(ISecureVariables.NAME,null);
    }
    catch (RequestExecutionException x)
    {
      AppObjects.log("Warn: Could not get a secure variable object",x);
    }       
    
  }
  /**
   * Determine if a variable is secure
   */
  private boolean isASecureVariable(final String variable)
  {
   if (m_secureVariables == null)
   {
      return false;
   }
   else
   {
      return m_secureVariables.isASecureVariable(variable);
   }
  }
  /**
   * Overriden method from BaseServlet
   */
   public abstract void serviceRequest(String user,
                                HttpSession session,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
         throws ServletException, IOException;                                
                                
   public void service(String user,
                               HttpSession session,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
        throws ServletException, IOException
   {
         if (user != null)
         {
            ServletCompatibility.putSessionValue(session,"profile_user",user);
         }
         addParametersToSession(session,parameters);
         addParametersFromSessionToUrlParameters(session,parameters);
         // add request, response, and session to parameters
         parameters.put("aspire_session",session);
         parameters.put("aspire_request",request);
         parameters.put("aspire_response",response);
         //serviceRequest(user,session,uri,query,parameters,out,request,response);
         invokeServiceRequestInAnEventSandwich(user,session,uri,query,parameters,out,request,response);
   }                                
   private void invokeServiceRequestInAnEventSandwich(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
           ,PrintWriter out, HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
   {
	   try 
	   {
		   boolean shouldIContinue =
			   SWIHttpEvents.beginAspireRequest(coreuser, session,uri,query,parameters,out,request,response);
		   if (shouldIContinue == false)
		   {
			   AppObjects.warn(this,"Begin aspire request chain returned false. Returning");
			   return;
		   }
           serviceRequest(coreuser,session,uri,query,parameters,out,request,response);
		   SWIHttpEvents.endAspireRequest(coreuser, session,uri,query,parameters,out,request,response);
	   }
	   catch(AspireServletException x)
	   {
		   throw new ServletException("Failure detected in invoking service request sandwich",x);
	   }
   }
   private void addParametersToSession( HttpSession session, Hashtable urlParameters )
   {
      // scan incoming url parameters for profile.
      for (Enumeration e=urlParameters.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         String lowerCaseKey = key.toLowerCase();
         if (lowerCaseKey.startsWith("profile_") == true)
         {
            // Add to the session if this is a non-secure variable
            if (isASecureVariable(lowerCaseKey))
            {
               // Yes a secure variable
               AppObjects.warn(this,"An attempt at passing a secure variable from the client");
               AppObjects.warn(this,"The variable is : %1s",lowerCaseKey);
            }
            else
            {
               // No, not a secure variable
               ServletCompatibility.putSessionValue(session,key,urlParameters.get(key));
            }                          
         }            
      }// end of for
   }
   private void addParametersFromSessionToUrlParameters(HttpSession session
                                                      , Hashtable urlParameters)
   {
   
//      Hashtable profileParameters = profile.getParameters();
      Enumeration sessionKeys = ServletCompatibility.getSessionValueNames(session);
      while (sessionKeys.hasMoreElements())
      {
         String key = (String)sessionKeys.nextElement();
         String lowerCaseKey = key.toLowerCase();
         if (lowerCaseKey.startsWith("profile_") == true )
         {
            urlParameters.put(key,ServletCompatibility.getSessionValue(session,key));
         }
      }
   }
} 

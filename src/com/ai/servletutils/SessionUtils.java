/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servletutils;
import javax.servlet.*;
import javax.servlet.http.*;

import com.ai.application.utils.*;
import com.ai.servlets.AspireConstants;
import com.ai.servlets.*;
import java.util.*;
import com.ai.aspire.authentication.*;
import com.ai.application.interfaces.*;
import com.ai.aspire.context.*;

public class SessionUtils
{
   private static String SESSION_INIT_CHECK_LOCK = "true";
   /**
    * Retrieves a session if a session exists and null if the session has terminated
    * If null session the caller will redirect the user to a redirector
    */
   static public HttpSession getSession(HttpServletRequest request
                                       , HttpServletResponse response)
         throws AspireServletException
   {
      String userAuth =
      AppObjects.getIConfig().getValue(AspireConstants.USER_AUTHORIZATION,"no");
      String sessionMgmt =
      AppObjects.getIConfig().getValue(AspireConstants.APPLY_SESSION_MANAGEMENT,"no");

      if(userAuth.equals("no") && sessionMgmt.equals("no"))
      {
         return request.getSession(true);
      }
      try
      {
         Object obj =
         AppObjects.getIFactory().getObject(AspireConstants.SESSION_SUPPORT_OBJECT,null);
         ISessionSupport sessionSupport = (ISessionSupport)obj;
         return sessionSupport.getSession(request,response);
      }
      catch(com.ai.application.interfaces.RequestExecutionException x)
      {
         AppObjects.log("warn: session: Could not get the session support object",x);
         return null;
      }
   }
   static public boolean initializeSession(HttpSession session
                                           ,HttpServletRequest request
                                           ,HttpServletResponse response) throws AspireServletException
   {
      if (isInitialized(session))
      {
         return true;
      }
      // session not initialized
      synchronized(session)
      {
         if (isInitialized(session)) return true;

         IFactory1 fact = (IFactory1)AppObjects.getIFactory();
         ISessionInit sessionInit = (ISessionInit)fact.getObjectWithDefault(ISessionInit.NAME,null,null);
         if (sessionInit != null)
         {
            sessionInit.initialize(session,request,response);
         }
         markSessionInitialized(session);
         SWIHttpEvents.sessionStart(session,request,response);

         return true;
      }
   }
   static private boolean isInitialized(HttpSession session)
   {
      Object init = session.getValue("aspire_session_initialized");
      return (init == null) ? false:true;
   }
   static private void markSessionInitialized(HttpSession session)
   {
      session.putValue("aspire_session_initialized","true");
   }
/***************************************************************************
 * Parameters & session & profiles
 ***************************************************************************
 */
   static public void addParametersToSession( HttpSession session, Hashtable urlParameters )
   {
      // scan incoming url parameters for profile.
      for (Enumeration e=urlParameters.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         String lowerCaseKey = key.toLowerCase();
         if (lowerCaseKey.startsWith("profile_") == true)
         {
            session.putValue(key,urlParameters.get(key));
         }
      }// end of for
   }
   static public void addParametersFromSessionToUrlParameters(HttpSession session
                                                      , Hashtable urlParameters)
   {

//      Hashtable profileParameters = profile.getParameters();
      String sessionKeys[] = session.getValueNames();
      for (int i=0;i<sessionKeys.length;i++)
      {
         String key = sessionKeys[i];
         String lowerCaseKey = key.toLowerCase();
         if (lowerCaseKey.startsWith("profile_") == true )
         {
            urlParameters.put(key,session.getValue(key));
         }
      }
   }
   static public AspireContextRegistry getAspireContextRegistry()
      throws RequestExecutionException
   {
      Object obj = AppObjects.getIFactory().getObject(AspireContextRegistry.NAME,null);
      return (AspireContextRegistry)obj;
   }
   /**
    *  Locate session
    *
    *  If an object is found in session return it
    *
    *  Otherwise get it from the factory, save it in the session and return it
    */
   static public Object getObject(String requestName, Object params)
      throws RequestExecutionException
   {
      // get session
      HttpSession session = getSession();
      if (session == null)
      {
         return AppObjects.getIFactory().getObject(requestName, params);
      }
      // session exists
      Object obj = session.getValue("Aspire.SessionObjects." + requestName);
      if (obj == null)
      {
         obj = AppObjects.getIFactory().getObject(requestName, params);
         session.putValue("Aspire.SessionObjects." + requestName,obj);
      }
      return obj;
   }
   static public HttpSession getSession() throws RequestExecutionException
   {
      AspireContextRegistry reg = getAspireContextRegistry();
      AspireContext ctx = reg.get();
      return ctx.getSession();
   }
}

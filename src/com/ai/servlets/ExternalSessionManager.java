/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.ai.servletutils.*;
import com.ai.aspire.authentication.*;
import com.ai.common.*;
/**
 * @see SessionUtils.getsession()
 * @see BaseServlet
 *
 * This gets a call if
 * 1. user authorization is enabled
 * 2. or session management is allowed
 *
 * aspire.authentication.userAuthorization=(no:yes)
 * aspire.sessionsupport.applySessionManagement=(no:yes)
 *
 * This doesn't get called if none of the above are enabled
 */
public class ExternalSessionManager implements ISessionSupport
{
   public  HttpSession getSession(HttpServletRequest request
                                 , HttpServletResponse response)
         throws AspireServletException
   {
      try
      {
         return internalGetSession(request,response);
      }
      catch(RequestExecutionException x)
      {
         throw new AspireServletException("Error:" + x.getRootCause(),x);
      }
   }

   public  HttpSession internalGetSession(HttpServletRequest request
                                 , HttpServletResponse response)
         throws RequestExecutionException, AspireServletException
   {
      // If a session exists return it
      HttpSession curSession = request.getSession(false);
      if (curSession != null)
      {
         //Session is there
         return curSession;
      }
      // session is not there
      AppObjects.log("Warn:Session does not exist");
      redirectUserToMainPage(response);
      return null; // request.getSession(false);
   } // end-getsession

/*******************************************************************************
 * redirectUserToMainPage
 *******************************************************************************
 */
   private void redirectUserToMainPage(HttpServletResponse response)
   {
      // get the redirect page
      String mainPage =
      AppObjects.getIConfig().getValue(AspireConstants.SESSION_SUPPORT_MAIN_PAGE,null);
      if (mainPage == null)
      {
         AppObjects.log("Error:ssup: You need to specify a starting main page for the application");
         return;
      }
      ServletUtils.getSubstitutedURL(mainPage, new Hashtable());
      // main page available, redirect the user to the main page
      try { response.sendRedirect(response.encodeRedirectURL(mainPage)); }
      catch (java.io.IOException x){
         AppObjects.log("Error:session: Could not redirect the user to the main page:" + mainPage,x);
      }
      return;
   }
/*******************************************************************************
 * end of class
 *******************************************************************************
 */
}


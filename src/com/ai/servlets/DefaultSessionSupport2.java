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

import com.ai.servlets.compatibility.ServletCompatibility;
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
 * 
 * 5/23/13
 * This is based on DefaultSessionSupport1
 * This class is a bit flawed.
 * this needs to be revisited
 * 
 * For now the main goal is to externalize the authorization scheme.
 * I have used it with HttpDigest and Persistent logins
 * 
 * See the following 2 links for more documentation
 * http://satyakomatineni.com/item/4559
 * http://satyakomatineni.com/item/4565
 * 
 */
public class DefaultSessionSupport2 implements ISessionSupport, ICreator
{
   private Vector m_loginPageURLs = null;
   private IAuthentication2 authHandler = null;
   private IHttpCookieEnabledAuthenticationMethod authMethodHandler = null;
   /*******************************************************************************
    * executeRequest - Required by the factory and returns itself
    * multiplicity - Singleton
    * args - none
    * Return code - itself
    *******************************************************************************
    */
   public DefaultSessionSupport2()
   {
      AppObjects.log("Info:ssup: DefaultSessionSupport is beging constructed");
      String loginPageURLs =
      AppObjects.getIConfig().getValue(AspireConstants.LOGIN_PAGE_URLS,null);
      if (loginPageURLs != null)
      {
         m_loginPageURLs = Tokenizer.tokenize(loginPageURLs,",");
      }
      try
      {
    	  authHandler = (IAuthentication2)AppObjects.getObject(IAuthentication.NAME, null);
    	  authMethodHandler = (IHttpCookieEnabledAuthenticationMethod)authHandler.getHttpAuthenticationMethod();
      }
      catch(Exception x)
      {
    	  throw new RuntimeException("Cannot get the authentication object",x);
      }

   }
   public Object executeRequest(String requestName, Object args)
         throws RequestExecutionException
   {
      return this;
   }

   /**
    * 9/22/13
    * This appears to be called mostly internally by getSession
    * I wonder if this should be private if no one is calling it 
    */
   private HttpSession getPublicSession(HttpServletRequest request
                                 , HttpServletResponse response)
   {
      HttpSession curSession = request.getSession(true);
      return curSession;
   }
   /*******************************************************************************
    * getSession - Retrieves a session if a session exists and null if the session has terminated
    * If null session the caller will redirect the user to a redirector
    *
    * 9/22/13
    * This could return a null if the http user authorization is enabled
    * and user is not valid.
    * It will challenge the response and return a null.
    * check to see what the baseservelet does if this is null!!
    * The BaseServlet will return the response rightaway if the session is null
    *******************************************************************************
    */
   public  HttpSession getSession(HttpServletRequest request
                                 ,HttpServletResponse response)
   throws AspireServletException
   {
      try  {
         return internalGetSession(request,response);
      }
      catch(RequestExecutionException x)  {
         throw new AspireServletException("Error:" + x.getRootCause(),x);
      }
   }

   public  HttpSession internalGetSession(HttpServletRequest request
                                 , HttpServletResponse response)
   throws RequestExecutionException, AspireServletException
   {
      HttpSession curSession = request.getSession(false);
      
      //*******************************************************
      //Case: Session exists and the user has logged in
      //*******************************************************
      if (curSession != null)
      {
    	 //Handle if a session exists and return
         String loggedInStatus = 
         	(String)ServletCompatibility.getSessionValue(
         		curSession,
				AspireConstants.ASPIRE_LOGGEDIN_STATUS_KEY);
         		
         if (loggedInStatus != null)
         {
            // user has logged in so return the session
            return curSession;
         }
         //user has not logged in!!
      }
      
      //*******************************************************
      // Case: Session is null or 
      //       session is there but a user has not logged in
      //*******************************************************
      
      //See if there is a persistent login is in place
      try
      {
    	  String cookieBasedUser = authMethodHandler.getUserFromCookieIfValid(request, response);
    	  if (cookieBasedUser != null)
    	  {
    		  //a cookie based user is available
    		  //join this user to the session and return the session
    		  //a new cookie would have been issued by the method handler
    		  AppObjects.info(this,"Joinging cookie based user %1s to the session",cookieBasedUser);
    	      return joinUserToTheSession1(cookieBasedUser,request,response);
    	  }
      }
      catch(AuthorizationException x)
      {
    	  //if there is an error make an assumption
    	  //assume that the user is null
    	  //proceed to the next step
          AppObjects.log("Error:Getting user from the cookie:" + x.getRootCause(), x);
      }
      
      //*******************************************************
      // Case: Session is null or 
      //       session is there but a user has not logged in
      //       cookie based user is not available
      //*******************************************************
      // public case for public URLs
      //
      
      try
      {
        if (ServletUtils.isAPublicURL(request,response) == true)
        {
           return getPublicSession(request,response);
        }
      }
      catch(com.ai.aspire.authentication.AuthorizationException x)
      {
        AppObjects.log("Error:" + x.getRootCause(), x);
      }
      //If there is an error knowing about a public url
      //consider the url private and move on
      
      // private case
      // If not asked to enforce session management
      // create a session and return it if necessary.
      String applySessionManagement =
      AppObjects.getIConfig().getValue(AspireConstants.APPLY_SESSION_MANAGEMENT,"no");
      if (applySessionManagement.equalsIgnoreCase("no"))
      {
         // no session management, meaning any page can establish a session
         // But you may want the user validation
    	 // Return null if no valid user is found
         String user = getUserIfValid(request,response);
         if (user == null) return null;
         
         //Most likely user will never be null
         //it may be annonymous in this scheme
         HttpSession session = request.getSession(true);
         joinUserToTheSession(session,user,request,response);
         return session;
      }
      
      //The following code will not be executed if session managment
      //is set to no. 
      //That means the following code happens if session management = yes
      //9/22/13: it probably needs to be deprecated!!
      //This path is useful a session is established only from a 
      //particular page!
      
      AppObjects.log("Info:ssup: Session management requested");
      if (!isLoginPage(request))
      {
         redirectUserToMainPage(response);
         return null; // request.getSession(false);
      }
      // This is a login page request.

      // User authorization requested
      AppObjects.log("Info:ssup: UserAuthorization requested");
      String user = getUserIfValid(request,response);
      if (user == null)
      {
         // Invalid user, re-authenticate
         AppObjects.log("Info:ssup: null Invalid user");
         // Force authentication
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.setHeader("WWW-authenticate","Basic realm=\"" + getRealm() + "\"");
         return null;
      }

      // See if I have the authority to establish the session
      // By default I assume Ihave
      String sessionCreateAuthority =
      AppObjects.getIConfig().getValue(AspireConstants.SESSION_CREATE_AUTHORITY,"yes");
      if (sessionCreateAuthority.equals("no"))
      {
         // Can't create session
         // Redirect user
         redirectUserToMainPage(response);
         return null;
      }
      // save the user and return the session
      HttpSession session = request.getSession(true);
      joinUserToTheSession(session,user,request,response);
      return session;
   } // end-getsession

   private HttpSession joinUserToTheSession1(String user, HttpServletRequest request, HttpServletResponse response)
   throws RequestExecutionException, AspireServletException
   {
	      HttpSession session = request.getSession(true);
	      joinUserToTheSession(session,user,request,response);
	      return session;
   }
   private void joinUserToTheSession(HttpSession session, String user, HttpServletRequest request, HttpServletResponse response)
         throws RequestExecutionException, AspireServletException
   {
      AppObjects.log("Info:ssup:joining user to the session");
      ServletCompatibility.putSessionValue(session,"profile_user",user);
	  ServletCompatibility.putSessionValue(session,AspireConstants.ASPIRE_LOGGEDIN_STATUS_KEY,"true");
      SWIHttpEvents.userLogin(user,session,request,response);
      // IHttpEvents events = (IHttpEvents)AppObjects.getObject(IHttpEvents.NAME,null);
      // events.userLogin(user,session,request,response);
   }

/*******************************************************************************
 * getRealm()
 *******************************************************************************
 */
   private String  getRealm()
   {
      return AppObjects.getIConfig().getValue(AspireConstants.REALM,"AI");
   }
/*******************************************************************************
 * loginPage
 *******************************************************************************
 */
   /**
   * Check to see if this request constitutes a login page
   * Logic:
   *    1. Hand over the request to a handler if you find one
   *    2. if not see if the uri matches( return false otherwise)
   *    3. See if a param string is specified( if not return true )
   *    4. See if the param string matches ( if not return false )
   *    5. return true
   */
   private boolean isLoginPage(HttpServletRequest request)
   {
      try
      {
         Object reply =
         AppObjects.getIFactory().getObject(AspireConstants.LOGIN_PAGE_VALIDATION_REQUEST,request);
         if (reply instanceof Boolean)
         {
         return ((Boolean)reply).booleanValue();
         }
         AppObjects.log("Error:ssup: Login page validation object returned other than a boolean" );
      }
      catch( com.ai.application.interfaces.RequestExecutionException x)
      {
         AppObjects.log("Info:ssup: No login page validation object specified");
      }

      // There is no login handler
      // See if there are any URLs specified
      // String sLoginPageURI = AppObjects.getIConfig().getValue(AspireConstants.LOGIN_PAGE_URI,null);
      if (m_loginPageURLs == null)
      {
         // no uri specified
         AppObjects.log("Error:ssup: No loginPageURLs specified");
         return false;
      }
      AppObjects.log("Info:ssup: Validating through the specified urls" );
      for( Enumeration loginPagesItr=m_loginPageURLs.elements();loginPagesItr.hasMoreElements();)
      {
         // see the request matches one of the URLs
         String loginPageURL = (String)loginPagesItr.nextElement();
         if (doesThisMatchALoginPage(request, loginPageURL))
         {
            AppObjects.info(this,"ssup: Login page match for: %1s", loginPageURL );
            return true;
         }
      }

      AppObjects.log("Info:ssup: This is not a login page");
      return false;
   } // end-loginPage

/*******************************************************************************
 * String getUserIfValid(HttpServletRequest request )
 * return null if the user authorization fails
 * if no authorization is indicated then the user is returned as annonymous
 * 
 * 5/23/13
 * This is the deviation from the previous version
 * we will use a authroization handler to do this
 *******************************************************************************
 */
   private String getUserIfValid(HttpServletRequest request,
                                 HttpServletResponse response )
   {
      AppObjects.info(this,"Inside get user");
      // Proceed with http authentication only if enabled
      String userAuthorization =
      AppObjects.getIConfig().getValue(AspireConstants.USER_AUTHORIZATION,"no");
      if (userAuthorization.equals("no"))
      {
            return AspireConstants.ANNONYMOUS_USER;
      }
      // http authentication in place
      // means userauthroization is set to yes
      AppObjects.info(this,"Info:ssup: Http authentication active");
      try {
    	  return authMethodHandler.getUserIfValid(request, response);
      }
      catch(AuthorizationException x)
      {
    	  //this probably won't happen as the handler is expected to return null
    	  AppObjects.error(this,"Http Authorization Method threw an exception",x);
    	  return null;
      }
   }   // end-getUserIfValid

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
      // main page available, redirect the user to the main page
      try { response.sendRedirect(response.encodeRedirectURL(mainPage)); }
      catch (java.io.IOException x){
         AppObjects.log("error.session: Could not redirect the user to the main page");
         AppObjects.log(x);
      }
      return;
   }
/*******************************************************************************
 * isThisALoginPage of class
 *******************************************************************************
 */

 private boolean doesThisMatchALoginPage(HttpServletRequest request, String loginPageURL)
 {
   AppObjects.log("Info:ssup: Matching " 
   		+ ServletCompatibility.getRequestURL(request) 
   		+ " with " 
   		+ loginPageURL);
   if (loginPageURL == null)
   {
     return false;
   }
   StringTokenizer t = new StringTokenizer(loginPageURL,"?");
   String uri = t.nextToken();
   if (!request.getRequestURI().equals(uri))
   {
      return false;
   }
   // uri matches
   if (!t.hasMoreTokens())
   {
      // no more tokens
      return true;
   }
   // parameters available
   AppObjects.log("Info:ssup: Parameters available for login page check");
   String queryString = t.nextToken();
   Hashtable params = ServletUtils.parseQueryString(queryString);
   AppObjects.secure(this,"The params are %1s",params.toString());
   for(Enumeration paramItr = params.keys();paramItr.hasMoreElements();)
   {
      // Get the specified parameter key from file
      String thisParamKey = (String)paramItr.nextElement();
      AppObjects.secure(this,"Parame key=%1s",thisParamKey);

      // Get the corresponding parameter from the httpRequest
      String httpValue = request.getParameter(thisParamKey);
      if (httpValue == null)
      {
         AppObjects.secure(this,"Could not find value for %1s", thisParamKey);
         return false;
      }
      String paramValueInFile = (String)params.get(thisParamKey);
      AppObjects.secure(this,"ssup: httpValue=%1s;paramValueInfile=%2s", httpValue, paramValueInFile);

      if (!httpValue.equals(params.get(thisParamKey)))
      {
         return false;
      }
   }
   return true;
 }
/*******************************************************************************
 * end of class
 *******************************************************************************
 */
}


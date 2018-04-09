/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
/*
 * 3/2/2012
 * ********
 * A newer version exists.
 * 
 */
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
public class DefaultSessionSupport implements ISessionSupport, ICreator
{
   private Vector m_loginPageURLs = null;
   /*******************************************************************************
    * executeRequest - Required by the factory and returns itself
    * multiplicity - Singleton
    * args - none
    * Return code - itself
    *******************************************************************************
    */            
   public DefaultSessionSupport()
   {
      AppObjects.log("session: DefaultSessionSupport is beging constructed");
      String loginPageURLs = 
      AppObjects.getIConfig().getValue(AspireConstants.LOGIN_PAGE_URLS,null);
      if (loginPageURLs != null)
      {
         m_loginPageURLs = Tokenizer.tokenize(loginPageURLs,",");
      }
      
   }     
   public Object executeRequest(String requestName, Object args)
         throws RequestExecutionException
   {
      return this;
   }   
   /*******************************************************************************
    * getSession - Retrieves a session if a session exists and null if the session has terminated
    * If null session the caller will redirect the user to a redirector
    *
    *******************************************************************************
    */                 
   public  HttpSession getSession(HttpServletRequest request
                                 , HttpServletResponse response)
   {
      // If a session exists return it
      HttpSession curSession = request.getSession(false);
      if (curSession != null)
      {
         AppObjects.log("returning the existing session");
         return curSession;
      }
      // If not asked to enforce session management
      // create a session and return it if necessary.
      String applySessionManagement = 
      AppObjects.getIConfig().getValue(AspireConstants.APPLY_SESSION_MANAGEMENT,"no");
      if (applySessionManagement.equals("no"))
      {
         // no session management
         // But you may want the user validation
         String user = getUserIfValid(request,response);
         if (user == null) return null;
         HttpSession session = request.getSession(true);
         //session.putValue("profile_user",user);
         ServletCompatibility.putSessionValue(session,"profile_user",user);
         return session;
      }
      AppObjects.log("session: Session management requested");
      if (!isLoginPage(request))
      {
         redirectUserToMainPage(response);
         return null; // request.getSession(false); 
      }
      // This is a login page request.
      
      // User authorization requested
      AppObjects.log("info: UserAuthorization requested");
      String user = getUserIfValid(request,response);
      if (user == null)
      {
         // Invalid user, re-authenticate
         AppObjects.log("session: null Invalid user");
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
      //session.putValue("profile_user",user);
      ServletCompatibility.putSessionValue(session,"profile_user",user);
      return session;
   } // end-getsession

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
         AppObjects.log("error.session: Login page validation object returned other than a boolean" );
      }
      catch( com.ai.application.interfaces.RequestExecutionException x)
      {
         AppObjects.log("info.session: No login page validation object specified");
      }         

      // There is no login handler
      // See if there are any URLs specified
      // String sLoginPageURI = AppObjects.getIConfig().getValue(AspireConstants.LOGIN_PAGE_URI,null);
      if (m_loginPageURLs == null)
      {
         // no uri specified
         AppObjects.log("error.session: No loginPageURLs specified");
         return false;
      }
      AppObjects.log("info.session: Validating through the specified urls" );      
      for( Enumeration loginPagesItr=m_loginPageURLs.elements();loginPagesItr.hasMoreElements();)
      {
         // see the request matches one of the URLs
         String loginPageURL = (String)loginPagesItr.nextElement();
         if (doesThisMatchALoginPage(request, loginPageURL))
         {
            AppObjects.log("session: Login page match for: " + loginPageURL );
            return true;
         }
      }
      
      AppObjects.log("info.session: This is not a login page");
      return false;
   } // end-loginPage

/*******************************************************************************
 * String getUserIfValid(HttpServletRequest request )
 *******************************************************************************
 */
   private String getUserIfValid(HttpServletRequest request,
                                 HttpServletResponse response )
   {
      AppObjects.log("Inside get user");
      // Proceed with http authentication only if enabled
      String userAuthorization =
      AppObjects.getIConfig().getValue(AspireConstants.USER_AUTHORIZATION,"no");
      if (userAuthorization.equals("no"))
      {
         String user = request.getParameter("profile_user");
         if (user == null)
         {
            return AspireConstants.ANNONYMOUS_USER;
         }
         else
         {
            return user;
         }
      }
      // http authentication in place
      AppObjects.log("auth: Http authentication active");
      String auth = request.getHeader("Authorization");
      AppObjects.log("Authorization :" + auth);
      if (auth == null) 
      {
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.setHeader("WWW-authenticate","Basic realm=\"" + getRealm() + "\"");
         return null;
      }
    
      //    String userName = request.getRemoteUser();
      //    String authType = auth.substring(0,5);
      String user = null;
      boolean valid = false;
      try
      {
         String userPassEncoded = auth.substring(6); 
         // decode the userPass
         sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
         String userPassDecoded = new String(dec.decodeBuffer(userPassEncoded));
         AppObjects.log( "Userid + password :" + userPassDecoded);

         // Separate userid,  password
         StringTokenizer tokenizer = new StringTokenizer(userPassDecoded,":");
         if (!tokenizer.hasMoreTokens()){ return null; }
         user = tokenizer.nextToken();
         String password = tokenizer.nextToken();

         // See if this is the valid user
         valid = ServletUtils.verifyPassword(user,password);
      }
      catch(IOException x)
      {
         AppObjects.log(x);
      }
      catch (IndexOutOfBoundsException x)
      {
         AppObjects.log(x);
      }
      catch(AuthorizationException x)
      {
         AppObjects.log("session: Could not authorize user");
         AppObjects.log(x);
      }
      finally
      {
         // valid user
         if (valid) return user;
         // Invalid user
         AppObjects.log("auth: Invalid user");
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.setHeader("WWW-authenticate","Basic realm=\"" + getRealm() + "\"");
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
         AppObjects.log("error.session: You need to specify a starting main page for the application");
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
   AppObjects.log("info.session: Matching " 
   	+ ServletCompatibility.getRequestURL(request) 
   	+ " with " + loginPageURL);
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
   AppObjects.log("info.session: Parameters available for login page check");
   String queryString = t.nextToken();
   Hashtable params = ServletUtils.parseQueryString(queryString);
   AppObjects.log("info.session: The params are " + params.toString());
   for(Enumeration paramItr = params.keys();paramItr.hasMoreElements();)
   {
      // Get the specified parameter key from file
      String thisParamKey = (String)paramItr.nextElement();
      AppObjects.log("info.session: Parame key=" + thisParamKey);

      // Get the corresponding parameter from the httpRequest
      String httpValue = request.getParameter(thisParamKey);
      if (httpValue == null)
      {
         AppObjects.log("info.session: Could not find value for " + thisParamKey);
         return false;
      }
      String paramValueInFile = (String)params.get(thisParamKey);
      AppObjects.log("info.session: httpValue=" + httpValue + ";paramValueInfile=" + paramValueInFile);
      
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

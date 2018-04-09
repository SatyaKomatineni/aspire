package com.ai.servlets;

import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import javax.servlet.http.*;
import java.util.*;
import com.ai.servletutils.*;

/**
 * Deprecation Note:
 * There is a new version of this file
 * Try using that one.
 * 
 * How does this work
 * if you are logged in allow to forward
 * If it is a public url allow to go forward
 * otherwise
 * redirec to a login page
 */
public class LoginValidator extends DefaultHttpEvents implements IInitializable
{
   private String m_loginPageURL = null;
   public void initialize(String requestName)
   {
      m_loginPageURL = AppObjects.getValue(requestName + ".loginPageURL",null);
      AppObjects.log("Info:LoginPageURL:" + m_loginPageURL);
   }
   public boolean beginRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      try
      {
         HttpSession session = request.getSession(false);
         if (isLoggedIn(session))
         {
            //Session is logged in
            //Allow this request to go through
            return true;
         }
         //Not logged in
         AppObjects.log("Warn:LV: Not logged in");
         if (ServletUtils.isAPublicURL(request,response))
         {
            //public url, allow it to go through
            AppObjects.log("Info:LV: This is a public url");
            return true;
         }
         //Private url and not logged in
         AppObjects.log("Info:LV: This is a private url, and you are not logged in");

         //Redirect to the user
         redirectToLoginPage(request,response,m_loginPageURL);

         //ask the requet to discontinue
         return false;
      }
      catch(com.ai.aspire.authentication.AuthorizationException x)
      {
         throw new AspireServletException("Error:Error with deciding a public url",x);
      }
   }//eof-function

/*******************************************************************************
 * redirectUserToMainPage
 *******************************************************************************
 */
      private void redirectToLoginPage(HttpServletRequest request,
            HttpServletResponse response,
            String loginPageURL)
            throws AspireServletException
      {
         if (loginPageURL == null)
         {
            throw new AspireServletException("Error:LV: LoginPageURL is null. It should be specified");
         }
         String targetURI = request.getRequestURI();
         AppObjects.log("Info:LV:target uri:" + targetURI);

         Hashtable t = new Hashtable();
         t.put("aspire_login_targeturi",targetURI);
         t.put("aspirecontext",request.getContextPath().substring(1));

         String newURL = ServletUtils.getSubstitutedURL(loginPageURL,t);

         AppObjects.log("Info:LV: Redirecting to " + newURL);

         try
         {
            response.sendRedirect(response.encodeRedirectURL(newURL));
         }
         catch(java.io.IOException x)
         {
            throw new AspireServletException("Error:LV: could not redirect using encode redirect",x);
         }
         return;
      }

/*******************************************************************************
 * isLoggedIn
 *******************************************************************************
 */
   private boolean isLoggedIn(HttpSession session)
   {
      if (session == null)
         return false;
      //session is good
      String loggedInStatus = (String)session.getAttribute(AspireConstants.ASPIRE_LOGGEDIN_STATUS_KEY);
      if (loggedInStatus == null)
      {
         return false;
      }
      //Logged in status availabe
      if (loggedInStatus.equals("false"))
      {
         return false;
      }
      return true;
   }
}//eof-class

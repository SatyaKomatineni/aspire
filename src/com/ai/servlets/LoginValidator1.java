package com.ai.servlets;

import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import com.ai.common.StringUtils;

import javax.servlet.http.*;

import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import com.ai.servletutils.*;

/**
 * How does this work
 * if you are logged in allow to forward
 * If it is a public url allow to go forward
 * otherwise
 * redirec to a login page
 */
public class LoginValidator1 extends DefaultHttpEvents implements IInitializable
{
   private String m_loginPageURL = null;
   public void initialize(String requestName)
   {
      m_loginPageURL = AppObjects.getValue(requestName + ".loginPageURL",null);
      AppObjects.info(this,"LoginPageURL:%1s",m_loginPageURL);
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
         AppObjects.info(this,"LV:target uri:%1s",targetURI);

         Hashtable t = new Hashtable();
         t.put("aspire_login_targeturi",targetURI);
         t.put("aspirecontext",request.getContextPath().substring(1));

         String newURL = ServletUtils.getSubstitutedURL(loginPageURL,t);

         //See what the target url is
         String uri = request.getRequestURI();
         String paramstring = request.getQueryString();

         String targetUrl = uri;

         //Add parameters if they are available
         if (paramstring != null)
         {
             targetUrl = uri + "?" + paramstring;
         }

         //escape the target url as it will http encoded in it
         String escapedTargetUrl = URLEncoder.encode(targetUrl);
         AppObjects.info(this,"Escaped target url is:%1s",escapedTargetUrl);

         String finalNewUrl = newURL + "&aspire_target_url=" + escapedTargetUrl;
         AppObjects.info(this,"Redirecting to :%1s",finalNewUrl);

         try
         {
            response.sendRedirect(response.encodeRedirectURL(finalNewUrl));
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

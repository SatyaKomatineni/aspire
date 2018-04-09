package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import javax.servlet.http.*;
import java.util.Map;
import com.ai.servlets.AspireConstants;
import com.ai.aspire.authentication.*;
import com.ai.filters.FilterUtils;
import com.ai.servlets.SWIHttpEvents;
import com.ai.servlets.AspireServletException;
import com.ai.servlets.compatibility.ServletCompatibility;
import com.ai.servletutils.ServletUtils;

/**
 * Input arguments
 *     username
 *     password
 *     aspire_target_url (optional)
 * 
 * State changes
 *     profile_user is set to username
 *     profile_aspire_loggedin_status is set to true
 *
 * Exceptions identifiers raised
 *		INVALID_PASSWORD
 * 
 * aspire_target_url_key (if a target url is present)
 *		LOGIN_TARGET_PRESENT
 *     
 * @author Satya
 *
 */
public class AspireLoginPart extends AHttpPart
{
   protected Object executeRequestForHttpPart(String requestName
         ,HttpServletRequest request
         ,HttpServletResponse response
         ,HttpSession session
         ,Map inArgs)
         throws RequestExecutionException
   {
      try
      {
         String username = (String)inArgs.get("username");
         String password = (String)inArgs.get("password");
         boolean rvalue = login(username,password,request,response,session);
         if (rvalue == true)
         {
             //successful login
             AppObjects.info(this,"Succesful login");
             //see if a target key is specified and valid
             String aspire_target_url = (String)inArgs.get("aspire_target_url");
             if (aspire_target_url != null)
             {
                 AppObjects.info(this,"Target url present:%1s", aspire_target_url);
                 aspire_target_url = aspire_target_url.trim();
                 if (!(aspire_target_url.equals("")))
                 {
                     inArgs.put("aspire_target_url_key","LOGIN_TARGET_PRESENT");
                 }
             }
         }
         return new Boolean(rvalue);
      }
      catch(AuthorizationException x)
      {
         throw new RequestExecutionException("Error:Invalid user",x);
      }
      catch(AspireServletException x)
      {
         throw new RequestExecutionException("Error:userlogin event error",x);
      }
   }
   private boolean login(String username, String password, HttpServletRequest request, HttpServletResponse response, HttpSession session)
         throws RequestExecutionException, AuthorizationException, AspireServletException
   {
      boolean validPassword = yourLogin(username,password);
      if (validPassword == false)
      {
         //Invalid password
         throw new AuthorizationException("INVALID_PASSWORD:userid or password is wrong");
      }
      //Good password
      ServletCompatibility.putSessionValue(session,AspireConstants.ASPIRE_USER_NAME_KEY,username);
      SWIHttpEvents.userLogin(username,session,request,response);
      
      ServletCompatibility.putSessionValue(session,AspireConstants.ASPIRE_LOGGEDIN_STATUS_KEY, "true");
      
      return true;
   }
   protected boolean yourLogin(String username, String password)
         throws AuthorizationException
   {
      try
      {
         IAuthentication auth =
               (IAuthentication)AppObjects.getObject(AspireConstants.AUTHENTICATION_OBJECT,null);
         return auth.verifyPassword(username,password);
      }
      catch(RequestExecutionException x)
      {
         throw new AuthorizationException("Error:Could not get the authorization object",x);
      }
   }//eof-function
   
   public static void logout(HttpSession session)
   {
   		//Remove login key
		session.removeAttribute(AspireConstants.ASPIRE_LOGGEDIN_STATUS_KEY);
		
		//Set user to annonymous
		session.setAttribute(
			AspireConstants.ASPIRE_USER_NAME_KEY
			,AspireConstants.ASPIRE_ANNONYMOUS_USER_NAME);   		
   }
   public static boolean isLoggedIn(HttpSession session)
   {
   		String loggedInStatus = 
   			(String)session.getAttribute(AspireConstants.ASPIRE_LOGGEDIN_STATUS_KEY);
   		if (loggedInStatus == null)
   		{
   			return false;
   		}
   		if (loggedInStatus.equalsIgnoreCase("true"))
   		{
   			return true;
   		}
   		return false;
   }
   public static String loggedInUser(HttpSession session)
   {
   		String username = 
   			(String)session.getAttribute(AspireConstants.ASPIRE_USER_NAME_KEY);
   		return username;
   }
}//eof-class
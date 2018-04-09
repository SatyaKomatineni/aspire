package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import javax.servlet.http.*;
import java.util.Map;
import com.ai.servlets.AspireConstants;
import com.ai.aspire.authentication.*;
import com.ai.data.DataUtils;
import com.ai.filters.FilterUtils;
import com.ai.servlets.SWIHttpEvents;
import com.ai.servlets.AspireServletException;
import com.ai.servlets.compatibility.ServletCompatibility;
import com.ai.servletutils.ServletUtils;

/**
 * Input arguments
 *     userid: (subuser)
 *     password (subuser password)
 *     aspire_target_url (where should you go after wards)
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
 * Logic 2014
 * **************************************
 * 0. Extend from httppart
 * 0. because it has no parameters to read
 * 1. see if the userid is the same
 * 2. if not return to the same screen
 * 3. it is the same
 * 4. set the login flags (tbd)
 * 5. redirect
 * 6. you should see the login indicator turned on
 * 
 * Login flags
 * ********************************
 * profile_aspire_app_sat_login_user
 * profile_aspire_app_sat_login_flag
 *
 * LoginRequestName
 * ***********************************
 * AspireAccountUserLoginRequest
 * (Put it in aspire constants)
 * (Move these constants to aspire constants)
 *
 */
public class SubUserAspireLoginPart extends AHttpPart
{
   public static String ASPIRE_SUB_USER_LOGIN_FLAG_KEY = "profile_aspire_app_sat_login_flag"; 
   public static String ASPIRE_SUB_USER_LOGIN_USERID_KEY = "profile_aspire_app_sat_login_user";
   
   protected Object executeRequestForHttpPart(String requestName
         ,HttpServletRequest request
         ,HttpServletResponse response
         ,HttpSession session
         ,Map inArgs)
         throws RequestExecutionException
   {
      try
      {
         String username = (String)inArgs.get("userid");
         String password = (String)inArgs.get("password");
         boolean rvalue = login(username,password,request,response,session,inArgs);
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
   private boolean login(String username, String password, HttpServletRequest request, 
		   HttpServletResponse response, HttpSession session, Map inArgs)
         throws RequestExecutionException, AuthorizationException, AspireServletException
   {
      boolean validPassword = yourLogin(username,password,inArgs);
      if (validPassword == false)
      {
         //Invalid password
         throw new AuthorizationException("INVALID_PASSWORD:userid or password is wrong");
      }
      AppObjects.info(this,"Setting aspire account user login values in session");
      //Good password
      ServletCompatibility.putSessionValue(session,ASPIRE_SUB_USER_LOGIN_USERID_KEY,username);
      ServletCompatibility.putSessionValue(session,ASPIRE_SUB_USER_LOGIN_FLAG_KEY, "true");
      inArgs.put(ASPIRE_SUB_USER_LOGIN_USERID_KEY,username);
      
      //SWIHttpEvents.userLogin(username,session,request,response);
      return true;
   }
   protected boolean yourLogin(String username, String password, Map inArgs)
         throws AuthorizationException
   {
      try
      {
    	  if (DataUtils.dataExists("AspireAccountUserLoginRequest", inArgs))
    	  {
    		  //this userid, password row exists
    		  return true;
    	  }
    	  AppObjects.secure(this, "Invalid userid/password:%s/%s",username,password);
    	  return false;
      }
      catch(RequestExecutionException x)
      {
         throw new AuthorizationException("Error:Could not get the authorization object",x);
      }
   }//eof-function
   
   public static void logout(HttpSession session)
   {
   		//Remove login key
		session.removeAttribute(ASPIRE_SUB_USER_LOGIN_FLAG_KEY);
		
		//Set user to annonymous
		session.setAttribute(
				ASPIRE_SUB_USER_LOGIN_USERID_KEY
			,AspireConstants.ASPIRE_ANNONYMOUS_USER_NAME);   		
   }
   public static boolean isLoggedIn(HttpSession session)
   {
   		String loggedInStatus = 
   			(String)session.getAttribute(ASPIRE_SUB_USER_LOGIN_FLAG_KEY);
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
   			(String)session.getAttribute(ASPIRE_SUB_USER_LOGIN_USERID_KEY);
   		return username;
   }
}//eof-class
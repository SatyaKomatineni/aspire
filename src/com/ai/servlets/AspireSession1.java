/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import java.util.*;
import javax.servlet.http.*;

import com.ai.common.StringUtils;
import com.ai.servlets.compatibility.ServletCompatibility;

/**
 * @See AspireSession which is deprecated
 *
 * Goal: To provide a typesafe way to access session contents
 */
public class AspireSession1 
{
   public static String ASPIRE_SESSION_NAME =     "AspireSession";

   private HttpSession session;
   /**
    * Constructor
    */
   public AspireSession1(HttpSession inSession) 
   {
	   session = inSession;
   }

/*
 * See if a session object is in the session.
 * if bCrate == true create the session and return
 * if not just return what you have including null
 */
   static public AspireSession1
      getAspireSessionFromHttpSession(HttpSession session, boolean bCreate)
   {
      Object obj = ServletCompatibility.getSessionValue(session,AspireSession1.ASPIRE_SESSION_NAME);
      if (bCreate == false)
      {
         return (AspireSession1)obj;
      }      
      // create one and add it and return it
      obj = new AspireSession1(session);
	  ServletCompatibility.putSessionValue(session,AspireSession1.ASPIRE_SESSION_NAME,obj);
      return (AspireSession1)obj;
   }

   public String getLoggedInUserId()
   {
	   return (String)session.getAttribute(AspireConstants.ASPIRE_USER_NAME_KEY);
   }
   
   void setLoggedInUserId(String username)
   {
	      session.setAttribute(AspireConstants.ASPIRE_USER_NAME_KEY,username);
	      session.setAttribute(AspireConstants.ASPIRE_LOGGEDIN_STATUS_KEY, "true");
   }
   public boolean getLoggedInStatus()
   {
	  String stringStatus = (String)session.getAttribute(AspireConstants.ASPIRE_LOGGEDIN_STATUS_KEY);
	  return StringUtils.ConvertStringToBoolean(stringStatus, false);
   }
}//eof-clas 

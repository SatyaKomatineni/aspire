package com.ai.aspire.authentication;

import com.ai.aspire.authentication.*;
import com.ai.application.utils.*;
import com.ai.servlets.AspireConstants;
import java.util.*;
import com.ai.filters.*;
import com.ai.application.interfaces.*;
import javax.servlet.http.*;
/**
 * Given a user disallow  certain urls
 * uses a map of user vs list of urls
 * Preliminary implementation
 * Optimize it later
 */

 public class UserURLMapAuthorization extends SimpleDBAuthentication1
 {
   protected static String m_requestName = null;
   public UserURLMapAuthorization()
   {
   }
   public void initialize(String requestName)
   {
      super.initialize(requestName);
      m_requestName = requestName;
   }

   public boolean isAccessAllowed(final String userid
                                 , HttpServletRequest request
                                  ,HttpServletResponse response)
      throws AuthorizationException
   {
      String key = m_requestName + ".user." + userid + ".excludeURLs";
      String excludeURLs = AppObjects.getValue(key,null);
      if (excludeURLs == null)
      {
         return true;
      }
      //there are exclude urls
      AppObjects.info(this,"Exclude urls for this user are: %1s", excludeURLs);
      String authKey = getAuthKey(request);
      if (authKey == null)
      {
         return true;
      }
      //authKey avaialble
      AppObjects.info(this,"auth: verifying access for resource:%1s", authKey);
      if (excludeURLs.indexOf(authKey) == -1)
      {
         //authkey is not an excluded url
         return true;
      }
      //it is an excluded url
      AppObjects.log("Warn:auth:" + authKey + " is an excluded url");
      try
      {
         response.sendError(HttpServletResponse.SC_FORBIDDEN,"The userid " + userid + " is not allowed access to this page");
         return false;
      }
      catch(java.io.IOException x)
      {
         AppObjects.log("Error:auth: io error",x);
         throw new AuthorizationException("Error:auth: io error",x);
      }

   }

   String getAuthKey(HttpServletRequest request)
   {
      String requestName = request.getParameter("request_name");
      if (requestName != null) return requestName;

      requestName = request.getParameter("url");
      if (requestName == null)
      {
         AppObjects.log("Warn:Auth: No request name or url found");
         return null;
      }
      return requestName;
   }

}




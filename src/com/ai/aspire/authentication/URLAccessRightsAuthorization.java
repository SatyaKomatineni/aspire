package com.ai.aspire.authentication;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.servletutils.ServletUtils;
/**
 * Given a user disallow  certain urls
 * uses a map of user vs list of urls
 * Preliminary implementation
 * Optimize it later
 */

 public class URLAccessRightsAuthorization extends SimpleDBAuthentication1
 {
   protected static String m_requestName = null;
   public URLAccessRightsAuthorization()
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
      String authKey = getAuthKey(request);
      if (excludeURLs == null)
      {
      	return this.isAuthorized(userid, request,response, authKey);
      }
      //there are exclude urls
      AppObjects.info(this,"Exclude urls for this user are: %1s", excludeURLs);
      
      //authKey avaialble
      AppObjects.info(this,"auth: verifying access for resource:%1s", authKey);
      if (excludeURLs.indexOf(authKey) == -1)
      {
         //authkey is not an excluded url
      	return this.isAuthorized(userid, request,response, authKey);
      }
      //it is an excluded url
      AppObjects.warn(this,"auth:" + authKey + " is an excluded url");
      try
      {
         response.sendError(HttpServletResponse.SC_FORBIDDEN,
         		"The userid " + userid + " is not allowed access to this page");
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
         AppObjects.warn(this,"Auth: No request name or url found");
         return null;
      }
      return requestName;
   }
   private boolean isAuthorized(final String userid
         ,HttpServletRequest request
         ,HttpServletResponse response
		 ,String urlname)
   throws AuthorizationException
   {
   		if (urlname == null) return true;
   		//a valide urlname or request name exists
   		AppObjects.info(this,"Checking authorization for:%1s", urlname);
   		String authRequestname = AppObjects.getValue(urlname + ".authRequestName", null);
   		if (authRequestname == null)
   		{
   			//this is a purely public url
   			AppObjects.info(this,"This is a purely public url:%1s",request.getRequestURI());
   			return true;
   		}
   		//we have the authrequestname
   		try
		{
   			Boolean reply = (Boolean)AppObjects.getObject(
			        authRequestname,
			        getParameters(userid,request,response));
   			return reply.booleanValue();
		}
   		catch(RequestExecutionException x)
		{
   			AppObjects.log("Error:could not eval the request:" + authRequestname, x);
   			return false;
		}
   }
   private Hashtable getParameters(String user,HttpServletRequest request
   							,HttpServletResponse response )
   {
   		Hashtable parameters = ServletUtils.getParameters(request);
   		parameters = ServletUtils.convertToLowerCase(parameters);
        parameters.put("aspire_session",request.getSession());
        parameters.put("aspire_request",request);
        parameters.put("aspire_response",response);
        parameters.put("profile_user",user);
   		
   		return parameters;
   }

}//eof-class




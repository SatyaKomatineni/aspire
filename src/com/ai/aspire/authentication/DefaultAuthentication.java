/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.authentication;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.servlets.AspireConstants;

public class DefaultAuthentication 
implements IAuthentication1,ICreator
{
  public Object executeRequest(String requestName, Object args)
     throws RequestExecutionException
  {
      return this;
  }
   public DefaultAuthentication()
   {
   }
   public boolean verifyPassword(final String userid, final String passwd )
      throws AuthorizationException
   {
      return true;
   }
   public boolean isAccessAllowed(final String userid, final String resource )
      throws AuthorizationException
   {
      return true;
   }
   public boolean isAccessAllowed(final String userid
                                 , HttpServletRequest request
                                  ,HttpServletResponse response)
      throws AuthorizationException
  {
    return true;
  }
   public IHttpAuthenticationMethod getHttpAuthenticationMethod()
   throws AuthorizationException
   {
	   try 
	   {
		   return 
		   (IHttpAuthenticationMethod)
		   AppObjects.getObject(IHttpAuthenticationMethod.SELF, null);
	   }
	   catch(RequestExecutionException x)
	   {
		   throw new AuthorizationException("Cannot locate the http authentication method!",x);
	   }
   }
   public String getRealm()
   throws AuthorizationException
   {
	      return AppObjects.getValue(AspireConstants.REALM,"AI");
   }
   /**
    * Make sure the object responding to this request has a
    * filter to return a string from an underlying SQL
    * Or have the object directly return a string if that is appropriate
    */
	@Override
	public String getPassword(String userid) throws AuthorizationException 
	{
		AppObjects.info(this,"Going to get password for %1s",userid);
		try
		{
			Hashtable map = new Hashtable();
			map.put("userid", userid);
			String password = (String)
			  AppObjects.getObject(AspireConstants.AC_GET_PASSWORD_REQUEST,map);
			AppObjects.info(this,"password successfully gottenf for %1s",userid);
			return password;
		}
		catch(Throwable x)
		{
			AppObjects.error(this,"Not able to get password for user:%1s", userid);
			throw new AuthorizationException("Not able to get password for user:" + userid);
		}
	}
   
   public IPersistentLoginSupport getPersistentLoginSupport()
   throws AuthorizationException
   {
	   try 
	   {
		   return 
		   (IPersistentLoginSupport)
		   AppObjects.getObject(IPersistentLoginSupport.SELF, null);
	   }
	   catch(RequestExecutionException x)
	   {
		   throw new AuthorizationException("Cannot locate the persisten login support object!",x);
	   }
   }
   
   static public IAuthentication1 getAuthenticationObject()
   throws RequestExecutionException
   {
       IAuthentication1 auth =
           (IAuthentication1)AppObjects.getObject(AspireConstants.AUTHENTICATION_OBJECT,null);
       return auth;
	   
   }
}//eof-class

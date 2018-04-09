package com.ai.akc.paramfilters;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.authentication.AuthorizationException;
import com.ai.aspire.authentication.IAuthentication;
import com.ai.aspire.authentication.IAuthentication2;
import com.ai.servlets.AspireConstants;
import com.ai.servlets.paramfilters.AParamFilterRequest;
/*
 * This may not be required.
 * see the corresponding param filter
 */
public class AKCShortURLParamFilterRequest extends AParamFilterRequest
{
	
	public AKCShortURLParamFilterRequest(HttpServletRequest inRequest)
	{
		super(inRequest);
	}
	
	/**
	 * Override this method to specialize parameter overloading
	 * @param request
	 * @param params
	 * @throws RequestExecutionException
	 */
	protected void qhFillDerivedParameters(HttpServletRequest request, Map params)
	throws RequestExecutionException
	{
		//The parent has already filled the params
		String ownerUserId = (String)params.get("owneruserid");
		if (ownerUserId != null)
		{
			//owner userid exists
			return;
		}

		//If it is a private url don't mess with it
		boolean publicurl = this.isAPublicURL(request);
		if (publicurl == false)
		{
			return;
		}
		//owner userid is not there
		//it is a public url
		String hostname = request.getServerName();
		String userid = AppObjects.getValue("aspire.multiweb." + hostname + ".userid",null);
		if (userid != null)
		{
			//domain exists
			params.put("owneruserid",userid);
			return;
		}
		//owneruserid is not there
		//domain user is not there
		//see if "downeruserid" is there
		String downeruserid=(String)params.get("downeruserid");
		if (downeruserid != null)
		{
			//downer is there
			params.put("owneruserid",downeruserid);
		}
		//downer is not there
		//let it go
		return;
	}
	protected boolean isAPublicURL(HttpServletRequest request)
	throws RequestExecutionException
	{
		return true;
	}
}//eof-class
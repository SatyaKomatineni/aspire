package com.ai.akc.paramfilters;

import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.authentication.AuthorizationException;
import com.ai.aspire.authentication.IAuthentication2;
import com.ai.servlets.AspireConstants;
import com.ai.servlets.paramfilters.AParamFilterRequest;

public class AKCDisplayItemParamFilterRequest 
extends AKCDisplayParamFilterRequest
{
	
	public AKCDisplayItemParamFilterRequest(HttpServletRequest inRequest)
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
		//have the parent fill the parameters
		super.qhFillDerivedParameters(request, params);
		//fill the item now
		fillItemDetails(request,params);
	}
	
	protected void fillItemDetails(HttpServletRequest request, Map params)
	throws RequestExecutionException
	{
		String itemid = this.quGetFirstPathElement(request);
		if (itemid == null)
		{
			throw new RuntimeException("No id specified");
		}
		params.put("url", "DisplayNoteIMPURL");
		params.put("reportid",itemid);
	}
	
	protected void fillItemDetails1(HttpServletRequest request, Map params)
	throws RequestExecutionException
	{
		Vector v = this.quGetPathElementVector(request);
		if (v == null) return;
		if (v.size() < 2)
		{
			//the path segment has less than two segments
			return;
		}
		//the path has 2 or more segments
		String seg1 = (String)v.get(0);
		String seg2 = (String)v.get(1);
		if (!seg1.equalsIgnoreCase("item"))
		{
			//the first segment is not "item"
			return;
		}
		//the first segment is item
		AppObjects.info(this, "Dealing with item url");
		params.put("url", "DisplayNoteIMPURL");
		params.put("reportid",seg2);
	}
	//override the base class implementation
	protected boolean isAPublicURL(HttpServletRequest request)
	throws RequestExecutionException
	{
		return true;
	}
}//eof-class
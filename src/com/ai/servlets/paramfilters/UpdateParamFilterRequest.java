package com.ai.servlets.paramfilters;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;

public class UpdateParamFilterRequest extends AParamFilterRequest
{
	
	public UpdateParamFilterRequest(HttpServletRequest inRequest)
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
		//If extra pathinfo exists
		//record the first segment as request_name
		String requestIfAvailable =
			this.quGetFirstPathElement(request);
		if (requestIfAvailable == null)
		{
			//there is no additional path
			return;
		}
		//there is additional path
		AppObjects.info(this,
				"Dropping request_name into the hash table:" 
				+ requestIfAvailable);		
		params.put("request_name",requestIfAvailable);
	}
}//eof-class
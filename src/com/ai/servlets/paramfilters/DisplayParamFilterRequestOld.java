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

/*
 * 3/2/2012
 * **********
 * Probably old.
 * Not sure where I use it.
 * 
 */
public class DisplayParamFilterRequestOld extends HttpServletRequestWrapper
{
	
	HttpServletRequest m_request = null;
	Map paramMap = null;
	
	public DisplayParamFilterRequestOld(HttpServletRequest inRequest)
	{
		super(inRequest);
		try
		{
			m_request = inRequest;
			paramMap = getParamMap(inRequest);
		}	
		catch(RequestExecutionException x)
		{
			AppObjects.log("Problem in DisplayParamFileter",x);
			throw new RuntimeException(x);
		}
	}
	private Map getParamMap(HttpServletRequest request)
	throws RequestExecutionException
	{
		Map args = new Hashtable();
		this.fillParametersForDisplay(request, args);
		return args;
	}
	
	private void fillParametersForDisplay(
			HttpServletRequest request
			,Map inArgs)
			throws RequestExecutionException
	{
		//fill the usual params
		fillParameters(request,inArgs);
		
		//If extra pathinfo exists
		//record the first segment as request_name
		String urlIfAvailable =
			this.getFirstPathElement(request);
		if (urlIfAvailable == null)
		{
			//there is no additional path
			return;
		}
		//there is additional path
		AppObjects.info(this,
				"Dropping url into the hash table:" 
				+ urlIfAvailable);		
		inArgs.put("url",urlIfAvailable);
	}
	
	private String getFirstPathElement(HttpServletRequest request)
	{
		//it will have /stuff1/stuff2
		String path = request.getPathInfo();
		if (path == null)
		{
			//there is no additional path information
			return null;
		}
		AppObjects.info(this,"there is additional pathinfo:" + path);
		//get the first argument
		Vector v = Tokenizer.tokenize(path, "/");
		String firstelement = (String)v.get(0);
		return firstelement;
	}
   
	   public void fillParameters(HttpServletRequest inRequest, Map parms )
	   {
	      for (Enumeration e=inRequest.getParameterNames();e.hasMoreElements();)
	      {
	         String key = (String)e.nextElement();
	         String value = inRequest.getParameter(key);
	         parms.put(key,value);
	      }
	      String aspireContext = inRequest.getContextPath();
	      if (aspireContext != null)
	      {
	      	AppObjects.trace("ServletUtils","Retrived appcontex:" + aspireContext);
	      	if (aspireContext.equals(""))
	      	{
	      		parms.put("aspirecontext","");
	      	}
	      	else if (aspireContext.length() == 1)
	      	{
	      		parms.put("aspirecontext","");
	      	}
	      	else
	      	{
	      		parms.put("aspirecontext",aspireContext.substring(1));
	      	}
	      }
	      else
	      {
	         AppObjects.log("Warn:Unexpected result. context path not in the request");
	      }
	   }//eof-function
	   
		/**
		 * Overriden method from base servlet
		 */
		public String getParameter(String name)
		{
			return (String)(paramMap.get(name));
		}
		public java.util.Enumeration getParameterNames()
		{
			//Enumeration e = this.paramMap.keySet().iterator();
			return Collections.enumeration(this.paramMap.keySet());
		}
		public java.util.Map getParameterMap()
		{
			return this.paramMap;
		}
}//eof-class
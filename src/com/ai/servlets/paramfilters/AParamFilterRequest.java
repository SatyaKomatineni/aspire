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

public abstract class AParamFilterRequest extends HttpServletRequestWrapper
{
	
	HttpServletRequest m_request = null;
	Map paramMap = new Hashtable();
	
	public AParamFilterRequest(HttpServletRequest inRequest)
	{
		super(inRequest);
		m_request = inRequest;
	}
	/**
	 * template method for fill param map.You must call this method.
	 * @param request
	 * @throws RequestExecutionException
	 */
	public void tFillParamMap()
	throws RequestExecutionException
	{
		//fill the usual params into the param map
		fillParameters(m_request,this.paramMap);
		this.qhFillDerivedParameters(m_request, this.paramMap);
	}
	/**
	 * Override this method to specialize parameter overloading
	 * @param request
	 * @param params
	 * @throws RequestExecutionException
	 */
	protected abstract void qhFillDerivedParameters(HttpServletRequest request, Map params)
	throws RequestExecutionException;

	/**
	 * A method to fill basic parameters from http request
	 * @param inRequest
	 * @param parms
	 */
	   public void fillParameters(HttpServletRequest inRequest, Map parms )
	   {
	      for (Enumeration e=inRequest.getParameterNames();e.hasMoreElements();)
	      {
	         String key = (String)e.nextElement();
	         String value = inRequest.getParameter(key);
	         parms.put(key.toLowerCase(),value);
	      }
	      String aspireContext = inRequest.getContextPath();
	      if (aspireContext != null)
	      {
	      	AppObjects.trace("ServletUtils","Retrived appcontex:%1s",aspireContext);
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
	   
		protected String quGetFirstPathElement(HttpServletRequest request)
		{
			//it will have /stuff1/stuff2
			String path = request.getPathInfo();
			if (path == null)
			{
				//there is no additional path information
				return null;
			}
			AppObjects.info(this,"there is additional pathinfo:%1s", path);
			//get the first argument
			Vector v = Tokenizer.tokenize(path, "/");
			String firstelement = (String)v.get(0);
			return firstelement;
		}
	   
	   
		protected Vector quGetPathElementVector(HttpServletRequest request)
		{
			//it will have /stuff1/stuff2
			String path = request.getPathInfo();
			if (path == null)
			{
				//there is no additional path information
				return null;
			}
			AppObjects.info(this,"there is additional pathinfo:%1s", path);
			//get the first argument
			Vector v = Tokenizer.tokenize(path, "/");
			return v;
		}
	   
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
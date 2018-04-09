package com.ai.servlets.paramfilters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;

public class DisplayParamFilter implements Filter
{
	   public void init(FilterConfig filterConfig) 
	   throws ServletException
	   {
		   //AppObjects.info(this,"DisplayParamFilter initialized");
	   }

	   public void destroy()
	   {   
		   //AppObjects.info(this,"DisplayParamFilter destroyed");
	   }

	   public void doFilter(ServletRequest request
	               , ServletResponse response
	               , FilterChain chain)
	   throws IOException, ServletException
	   {
		   try
		   {
			   //Get the request
			   DisplayParamFilterRequest dpfr 
			   		= new DisplayParamFilterRequest((HttpServletRequest)request);
			   
			   //prime it
			   dpfr.tFillParamMap();
			   
			   //continue the request
			   chain.doFilter(dpfr,response);
		   }
		   catch(RequestExecutionException x)
		   {
			   AppObjects.log("Error:Exception from DisplayParamFilter",x);
			   throw new RuntimeException(x);
		   }
	   }
}


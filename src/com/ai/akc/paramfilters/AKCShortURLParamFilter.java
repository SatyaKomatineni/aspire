package com.ai.akc.paramfilters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.ai.application.utils.AppObjects;
import com.ai.servletutils.ServletUtils;

/*
 * Significant problems with 
 * filters and dispatch.
 * Dispatch does not invoke other filters.
 * I am forcing the owneruserid as an additional parameter
 */
public class AKCShortURLParamFilter implements Filter
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
			   ServletUtils.logRequestDetails(this, (HttpServletRequest)request);
			   //get pathinfo
			   String pathinfo = this.getShortURLPath((HttpServletRequest)request);
			   AppObjects.trace(this,"pathinfo:%1s",pathinfo);
			   if (pathinfo == null)
			   {
				   chain.doFilter(request,response);
				   return;
			   }
			   //look it up
			   //displatch
			   String owner = this.getOwnerUserId(request);
			   if (owner == null)
			   {
				   AppObjects.error(this,"Unanticipated:owner is null");
				   chain.doFilter(request,response);
				   return;
			   }
				   
			   String targeturl = ShortURLMapRegistry.getUrl(owner, pathinfo);
			   if (targeturl == null)
			   {
				   chain.doFilter(request,response);
				   return;
			   }
			   targeturl = targeturl + "&ownerUserId=" + owner;
			   AppObjects.trace(this, "Going to url:%1s",targeturl);
			   this.dispatchToNewTarget(request, response, targeturl);
		   }
		   catch(Throwable t)
		   {
			   AppObjects.log("Error:Exception from DisplayParamFilter",t);
			   throw new ServletException("filter exception",t);
		   }
	   }
	   //internal implementation
	   private String getShortURLPath(HttpServletRequest request)
	   {
		   String uri = request.getRequestURI();
		   if (!uri.startsWith("/akc"))
		   {
			   //the uri doesn't start with akc
			   //the whole uri is subjected to look up
			   return uri;
		   }
		   //the uri starts with akc
		   return null;
	   }
	   private String getOwnerUserId(ServletRequest request)
	   {
			//owner userid is not there
			//it is a public url
			String hostname = request.getServerName();
			String userid = AppObjects.getValue("aspire.multiweb." + hostname + ".userid",null);
			return userid;
	   }
	   private void dispatchToNewTarget(ServletRequest request, 
			   ServletResponse response,
			   String targeturl)
	   throws IOException, ServletException
	   {
			//HttpServletRequest nr = 
			//	new AKCShortURLParamFilterRequest((HttpServletRequest)request);
			RequestDispatcher dispatcher = request.getRequestDispatcher(targeturl);
			dispatcher.forward(request, response);
	   }
}


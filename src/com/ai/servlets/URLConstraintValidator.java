package com.ai.servlets;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.RequestExecutorResponse;
import com.ai.application.utils.AppObjects;

/**
 * Validate if constraints are met
 * 
 * Examples
 * *********
 * url1.constraintRequestNames=AppLoginConstraint1,Consraint2, ec
 * or
 * request.requestname.classname=blah
 * request.requestname.constraintrequestnames=AppLoginConstraint1,Consraint2, ec
 * 
 * where
 * 
 * request.AppLoginConstraint1.classname=ValidateSubUserLogin
 * request.AppLoginConstraint1.appname=Sat
 * 
 * request.constraint2.classname=DBRequestExecuor
 * .db
 * .statemnt
 * .boolean filter
 * 
 * May 2014
 * *********
 * Based on LoginValidator1
 * 
 * Multiplicity
 * ****************
 * This is a singleton
 */
public class URLConstraintValidator extends DefaultHttpEvents implements IInitializable
{
   public void initialize(String requestName)
   {
      AppObjects.info(this,"Initialzied URLConstraintValidator");
   }
   public boolean beginAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
           ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
	      AppObjects.info(this,"Begin aspire request with proper args");
	      AspireURL aurl = AspireURL.createAppropriateAspireURL(request);
	      if (aurl instanceof NonAspireURL)
	      {
	    	  AppObjects.warn(this,"This is a nonaspire URL");
	    	  return true;
	      }
	      //it is one of the URLs
	      List<String> constraintRequestnames = aurl.getConstraintRequestNamesAsAList();
	      if (constraintRequestnames == null)
	      {
	    	  AppObjects.info(this,"No constraint names for this url:%s",aurl.getBaseConfigString());
	    	  return true;
	      }
	      //constraints exist. Execute one by one
	      for(String requestname: constraintRequestnames)
	      {
	    	  AppObjects.trace(this,"Executing constraint: %s",requestname);
	    	  boolean b = executeRequest(requestname,parameters);
	    	  if (b == false)
	    	  {
	    		  AppObjects.warn(this, "Terminating the request as %s returned false",requestname);
	    	  }
	      }
	      return true;	   
   }
   
   private boolean executeRequest(String requestName, Hashtable args)
   throws AspireServletException
   {
	   try
	   {
		   Object response = AppObjects.getObject(requestName, args);
		   if (response == null)
		   {
			   AppObjects.error(this,"factory returned an empty object for:%s",requestName);
			   return true;
		   }
		   if (response instanceof RequestExecutorResponse)
		   {
			   return ((RequestExecutorResponse)response).getReturnCode();
		   }
		   if (response instanceof Boolean)
		   {
			   return ((Boolean) response).booleanValue();
		   }
		   AppObjects.error(this,"Unexpected type returned from request:%s",requestName);
		   return true;
	   }
	   catch(RequestExecutionException e)
	   {
		   throw new AspireServletException("Exception from " + requestName, e);
	   }
   }

   public boolean endAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
     ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException   
   {
	      AppObjects.info(this,"Not planning to do anything");
	      return true;	   
   }
}//eof-class

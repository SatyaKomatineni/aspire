package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.Hashtable;
import java.util.Map;
import com.ai.servlets.AspireConstants;
/**
 * Todo: Do I need this???? 
 * @deprecated
 */
public abstract class AHttpParamCollectorPart extends AFactoryPart
{
   public static String PARAM_COLLECTOR_REQUEST_NAME="aspire.web.paramcollector"; 
   public static String ASPIRE_SERVLET_TYPE_PARAM_NAME="aspireservlettype"; 

   protected Object executeRequestForPart(String requestName, Map inArgs)
       throws RequestExecutionException
   {
      HttpServletRequest request   = 
    	  (HttpServletRequest)inArgs.get(AspireConstants.ASPIRE_HTTP_REQUEST_KEY);
      String aspireServletType   = 
    	  (String)inArgs.get(ASPIRE_SERVLET_TYPE_PARAM_NAME);
      return executeRequestForHPCPart(requestName, request, aspireServletType, inArgs);
   }
   protected abstract Object executeRequestForHPCPart(String requestName
         ,HttpServletRequest request
         ,String aspireServletType
         ,Map inArgs)
         throws RequestExecutionException;
   
   public static Hashtable collectParametersViaPipeline(HttpServletRequest request
		   ,String aspireServletType)
   throws RequestExecutionException
   {
	   if (AHttpParamCollectorPart.isPipelineAvailable() == false)
	   {
		   AppObjects.info("AHttpParamCollectorPart","No pipeline for parameter collection defined");
		   return null;
	   }
	   AppObjects.info("AHttpParamCollectorPart","Pipeline for parameter collection defined");
	   Hashtable parameters = new Hashtable();
	   parameters.put(AspireConstants.ASPIRE_HTTP_REQUEST_KEY, request);
	   parameters.put(ASPIRE_SERVLET_TYPE_PARAM_NAME, aspireServletType);
	   AppObjects.getObject(PARAM_COLLECTOR_REQUEST_NAME,parameters);
	   return parameters;
   }
   
   private static boolean isPipelineAvailable()
   {
	   String value = AppObjects.getValue("request." 
			   + PARAM_COLLECTOR_REQUEST_NAME
			   + ".classname"
			   ,null);
	   
	   if (value == null) return false;
	   return true;
   }

}//eof-class
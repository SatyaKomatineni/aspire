/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.StringTokenizer;
import com.ai.application.utils.AppObjects;
import com.ai.servletutils.*;
import com.ai.htmlgen.*;
import com.ai.common.*;
import com.ai.application.interfaces.*;
import com.ai.data.IDataCollection;
import com.ai.data.IIterator;

/**
 * See previous classes for documentation.
 */
public class RequestExecutorServletT2 extends ProfileEnabledServlet
{

  private EncodeArgSubstitutor m_substitutor = new EncodeArgSubstitutor();

  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
  }
   public void serviceRequest(String user,
                              HttpSession session,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
      throws ServletException,IOException
   {
      // set the response header so that it is always expired
      response.setDateHeader("Expires",0);

      // For the given URL get the html template file
      String request_name = (String)parameters.get("request_name");
      if (request_name == null )
      {
         PrintUtils.sendCompleteMessage(response,"Parameter called 'request_name' is required");
         return;
      }
      try
      {
         // Execute the request and get back a request response object
         // Gather arguments in to a vector
         Object obj = AppObjects.getIFactory().getObject(request_name,parameters);

         //String redirectURL = AppObjects.getIConfig().getValue("request." + request_name + ".redirectURL");
         RedirectURL redirectURL = getSuccessRedirectURL1(request_name,parameters);
         if (redirectURL == null)
         {
            AppObjects.warn(this,"Could not find a redirectURL for request:" + request_name);
            sendSuccess(out,request_name,request,response);
            return;
         }

         //RedirectURL exists
         RedirectURL.redirect(redirectURL,parameters,request,response,getServletConfig().getServletContext());

      }
      catch( com.ai.common.CommonException x)
      {
         AppObjects.log("Info:Redirect request threw an exception",x);
         sendFailure(out,request_name,request,response,parameters,x);
      }
   }
   void sendSuccess(PrintWriter out, String requestName, HttpServletRequest request, HttpServletResponse response )
   {
      try
      {
        // get the page name
        String successPage = FileUtils.translateFileIdentifier("RequestExecutor.success_page");
        StringBuffer buf = FileUtils.convertToStringBuffer(successPage);
        Hashtable args = new Hashtable();
        args.put("request_name",requestName);
        String message = com.ai.jawk.StringProcessor.substitute(buf.toString(),args);
        ServletUtils.getSuitablePrintWriter(request,response,out).print(message);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         PrintUtils.writeException(ServletUtils.getSuitablePrintWriter(request,response,out),x);
         AppObjects.log("Error: ConfigException",x);
      }
      catch(java.io.IOException x)
      {
         PrintUtils.writeException(ServletUtils.getSuitablePrintWriter(request,response,out),x);
         AppObjects.log("Error: IOException",x);
      }

   }
   void sendFailure(PrintWriter out
                     , String requestName
                     , HttpServletRequest  request
                     , HttpServletResponse response
                     , Hashtable params
                     , Throwable t ) throws ServletException
   {
      try
      {
         // Try to redirect it to success page
         params.put("aspire.return_code","failure");
         params.put("aspire.requst_name",requestName);
         params.put("aspire.error_message",t.getMessage());


         IServletRequestUpdateFailure frHandler = getFailureResponseHandler(requestName);
         if (frHandler == null)
         {
            PrintUtils.sendCompleteMessage(response,"Could not locate failure response handler");
            return;
         }
         // Found the failure response handler
         frHandler.respondToFailure(request
                                    ,response
                                    ,getServletConfig().getServletContext()
                                    ,t
                                    ,params
                                    ,requestName);
      }
      catch(AspireServletException x)
      {
         AppObjects.log(x);
         PrintUtils.writeException(ServletUtils.getSuitablePrintWriter(request,response,null)
                                 ,x);
      }
      finally
      {
         // nothing to do
      }
   }
   private String getSuccessRedirectURL(String requestName, Hashtable args)
   {
        String redirectURL = AppObjects.getIConfig().getValue("request." + requestName + ".successRedirectURL", null);
        if (redirectURL == null) return null;
        String substitutedRedirectURL = m_substitutor.substitute(redirectURL,args);
        AppObjects.trace(this,"res: successRedirectURL :%1s",substitutedRedirectURL);
        return substitutedRedirectURL;
   }

   /**
    * Will return null if there is no redirectURL specified
    * @param requestName
    * @param args
    * @return
    */
   private RedirectURL getSuccessRedirectURL1(String requestName, Hashtable args)
   {
         //Look for a key
         String targetUrlKey = (String)(args.get("aspire_target_url_key"));
         if (targetUrlKey != null)
         {
            //target url key exists
            String redirectURL = AppObjects.getValue("request." + requestName + ".redirectURL." + targetUrlKey, null);
            if (redirectURL != null)
            {
               //target redirect url found
               String redirectType = AppObjects.getValue("request." + requestName + ".redirectURL." + targetUrlKey + ".redirectType", "default");
               String substRedirectURL = ServletUtils.getSubstitutedURL(redirectURL,args);
               return new RedirectURL(substRedirectURL,redirectType);
            }
         }
         //key is not there
         //mandatory key
         String redirectURL = AppObjects.getIConfig().getValue("request." + requestName + ".redirectURL",null);
         if (redirectURL == null) return null;

         //Good redirectURL exists
         String redirectType = AppObjects.getIConfig().getValue("request." + requestName + ".redirectURL" + ".redirectType","default");
         String substRedirectURL = ServletUtils.getSubstitutedURL(redirectURL,args);
         return new RedirectURL(substRedirectURL,redirectType);
   }
   private IServletRequestUpdateFailure getFailureResponseHandler(String requestName)
   {
      IFactory1 factory = (IFactory1)AppObjects.getIFactory();

      Object failureResponseObj = factory.getObjectWithDefault(
                                    requestName + ".failureResponseHandler"
                                    ,null // args
                                    ,factory.getObjectWithDefault(
                                       IServletRequestUpdateFailure.NAME
                                       ,null
                                       ,null));
      if (failureResponseObj == null)
      {
         return new DUpdateServletRequestFailureResponse();
      }
      else
      {
         return (IServletRequestUpdateFailure)failureResponseObj;
      }

   }
   /*****************************************************************
    * PrintWriter related hooks
    *****************************************************************
    */
   /**
    * GetContentType hook for presetting the content type of the reply
    * Currently there is no presetting happening anywhere
    */
   public String qhdGetContentType( HttpServletRequest request, HttpServletResponse response)
   {
      return super.qhdGetContentType(request,response);
   }

   /**
    * This function can return null, if it doesn't want disturb the
    * printwriter from the response object.
    * This is important so that the downstream objects can retrieve
    * the PrintWriter when appropriate.
    */
   public PrintWriter qhdGetPrintWriter(HttpServletRequest request, HttpServletResponse response)
      throws IOException
   {
      return null;
   }
}//end of class

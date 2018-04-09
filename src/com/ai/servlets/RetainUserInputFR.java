/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.Hashtable;
import com.ai.servletutils.*;
import com.ai.common.*;
import com.ai.application.utils.*;
import com.ai.application.interfaces.*;

/**
 * Feb 2012
 * *********
 * Not sure if I use this class anymore
 * 
 * From 2004
 * ***********
 * Responds to update failures.
 * Locate a failure redirect url
 * Redirect the user to that url
 * Whatever redirect handler is in place, will be used
 * This may be default(internal),external or client side
 */
public class RetainUserInputFR
            implements IServletRequestUpdateFailure
                     ,ICreator
{

    public Object executeRequest(String requestName, Object args)
        throws RequestExecutionException
    {
        return this;
    }
   public void respondToFailure(HttpServletRequest request
                                ,HttpServletResponse response
                                ,ServletContext servletContext
                                ,Throwable t
                                ,Hashtable params
                                ,String requestName)
               throws AspireServletException, ServletException
   {

      try
      {
         // Try to redirect it to success page
         params.put("aspire.return_code","failure");
         params.put("aspire.requst_name",requestName);
         params.put("aspire.error_message",t.getMessage());
         request.setAttribute(AspireConstants.REQUEST_OVERRIDE_DICTIONARY,
               new HttpRequestDictionary(request));
         String redirectURL = getFailureRedirectURL(requestName,params);
         if (redirectURL != null)
         {
         		//response.sendRedirect(response.encodeRedirectUrl(redirectURL));
               IRedirector redirector = new DefaultRedirector();
               redirector.redirect(redirectURL
                                    ,request
                                    ,response
                                    ,servletContext);
               return;
         }
          // get the page name
          String failurePage = FileUtils.translateFileIdentifier("RequestExecutor.failure_page");
          StringBuffer buf = FileUtils.convertToStringBuffer(failurePage);
          Hashtable args = new Hashtable();
          args.put("request_name",requestName);
          if (t != null)
          {
             args.put("exception_details",ServletUtils.convertToHtmlLines(t.getMessage()));
          }
          String message = com.ai.jawk.StringProcessor.substitute(buf.toString(),args);
          PrintUtils.sendCompleteMessage(response,message);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         throw new AspireServletException("Config Exception",x);
      }
      catch(java.io.IOException x)
      {
         throw new AspireServletException("IOException",x);
      }
      catch(RedirectorException x)
      {
         throw new AspireServletException("Redirector Exception",x);
      }
   }// end of function

   /**
    * Get a url to be redirected to on failure
    * Will be available in the properties file as "request.requestName.failureRedirectURL"
    * Will substitute any substitutable arguments from the param list
    */
   private String getFailureRedirectURL(String requestName, Hashtable args)
   {
        String redirectURL = AppObjects.getIConfig().getValue("request." + requestName + ".failureRedirectURL", null);
        if (redirectURL == null)
        {
         AppObjects.log("res: can not find : " + "request." + requestName + ".failureRedirectURL" );
         return null;
        }
        String substitutedRedirectURL = com.ai.jawk.StringProcessor.substitute(redirectURL,args);
        AppObjects.log("res: failureRedirectURL :" + substitutedRedirectURL);
        return substitutedRedirectURL;
   }

} // end of class

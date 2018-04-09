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
import java.util.Vector;

/**
 * Responds to update failures.
 * Locate a failure redirect url
 * Redirect the user to that url
 * Whatever redirect handler is in place, will be used
 * This may be default(internal),external or client side
 *
 * Uses redirection type
 */
public class DUpdateServletRequestFailureResponse1
            implements IServletRequestUpdateFailure
{
   private IExceptionAnalyzer m_exceptionAnalyzer = null;
//   private EncodeArgSubstitutor m_substitutor = new EncodeArgSubstitutor();

   public DUpdateServletRequestFailureResponse1()
   {
      // will be set to null if there is no support
      m_exceptionAnalyzer = (IExceptionAnalyzer)AppObjects.getObject(IExceptionAnalyzer.NAME,null,null);
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

         String rootCauseCode=getRootCauseCode(t);
         String rootCause = getRootCause(t);

         if (rootCause != null)
         {
            AppObjects.info(this,"info: root cause - %1s",rootCause);
            params.put("aspire.root_cause",rootCause);
         }
         if (rootCauseCode != null)
         {
            AppObjects.info(this,"root cause code - %1s",rootCauseCode);
            params.put("aspire.root_cause_code",rootCauseCode);
         }

         // Get the url for a specific exception first
         RedirectURL redirectURL = null;
         redirectURL = getFailureRedirectURLForException(rootCauseCode, requestName,params);

         // go for the match based urls
         if (redirectURL == null)
         {
            redirectURL = getFailureRedirectURLForExceptionUsingMatch(t, requestName,params);
         }

         // If the url for exception doesn't exist then go for generic failure redirect url
         if (redirectURL == null)
         {
            redirectURL = getFailureRedirectURL(requestName,params);
         }
         if (redirectURL != null)
         {
                 //response.sendRedirect(response.encodeRedirectUrl(redirectURL));
               RedirectURL.redirect(redirectURL
                                    ,params
                                    ,request
                                    ,response
                                    ,servletContext);
               return;
         }
         // If the failureRedirectURL doesn't exist then go for the global
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
   private RedirectURL getFailureRedirectURL(String requestName, Hashtable args)
   {
        String redirectURL = AppObjects.getIConfig().getValue("request." + requestName + ".failureRedirectURL", null);
        if (redirectURL == null)
        {
         AppObjects.info(this,"res: can not find : request.%1s.failureRedirectURL", requestName);
         return null;
        }
//        String substitutedRedirectURL = m_substitutor.substitute(redirectURL,args);
        String substitutedRedirectURL = ServletUtils.getSubstitutedURL(redirectURL,args);
        AppObjects.info(this,"res: failureRedirectURL :%1s",substitutedRedirectURL);

        String redirectType =
              AppObjects.getIConfig().getValue("request." + requestName + ".failureRedirectURL.redirectType", RedirectURL.DEFAULT);
        return new RedirectURL(substitutedRedirectURL,redirectType);
   }
   private RedirectURL getFailureRedirectURLForException(String rootCauseCode, String requestName, Hashtable args)
   {
         AppObjects.info(this,"retrieve failureredirecturl for %1s", rootCauseCode);
         if (rootCauseCode  == null) return null;

        String redirectURL =
         AppObjects.getIConfig().getValue("request."
                                          + requestName
                                          + ".failureRedirectURL."
                                          + rootCauseCode
                                          , null);
        if (redirectURL == null)
        {
         AppObjects.log("res: can not find : "
               + "request."
               + requestName
               + ".failureRedirectURL."
               + rootCauseCode);
         return null;
        }
//        String substitutedRedirectURL = m_substitutor.substitute(redirectURL,args);
        String substitutedRedirectURL = ServletUtils.getSubstitutedURL(redirectURL,args);
        AppObjects.info(this,"res: failureRedirectURL :%1s",substitutedRedirectURL);

        String redirectType =
         AppObjects.getIConfig().getValue("request."
                                          + requestName
                                          + ".failureRedirectURL."
                                          + rootCauseCode
                                          + ".redirectType"
                                          , RedirectURL.DEFAULT);

        return new RedirectURL(substitutedRedirectURL,redirectType);
   }


   private RedirectURL getFailureRedirectURLForExceptionUsingMatch(Throwable t
                                                      , String requestName
                                                      , Hashtable args)
   {
         //
        AppObjects.log("info: Looking for matchstrings");
        String matchStrings =
         AppObjects.getIConfig().getValue("request."
                                          + requestName
                                          + ".failureRedirectURL.matchStrings"
                                          , null);
        if (matchStrings == null)
        {
         AppObjects.log("info: can not find matchstrings for: "
               + "request."
               + requestName
               + ".failureRedirectURL.matchStrings" );
         return null;
        }

        // The following matchstrings specified
        Vector matchVector = Tokenizer.tokenize(matchStrings,"|");
        String curMatch = null;
        boolean matchFound = false;
        for(java.util.Enumeration e=matchVector.elements();e.hasMoreElements();)
        {
            curMatch = (String)e.nextElement();
            if (m_exceptionAnalyzer.doYouMatch(t,curMatch))
            {
               matchFound = true;
               break;
            }
        }
        if (matchFound == false)
        {
            // no match found
            return null;
        }
        // match found
        String redirectURL =
         AppObjects.getIConfig().getValue("request."
                                          + requestName
                                          + ".failureRedirectURL."
                                          + curMatch
                                          , null);
        if (redirectURL == null)
        {
         AppObjects.log("res: can not find : "
               + "request."
               + requestName
               + ".failureRedirectURL."
               + curMatch);
         return null;
        }
//        String substitutedRedirectURL = m_substitutor.substitute(redirectURL,args);
        String substitutedRedirectURL = ServletUtils.getSubstitutedURL(redirectURL,args);
        AppObjects.info(this,"failureRedirectURL :%1s", substitutedRedirectURL);

        String redirectType =
         AppObjects.getIConfig().getValue("request."
                                          + requestName
                                          + ".failureRedirectURL."
                                          + curMatch
                                          + ".redirectType"
                                          , RedirectURL.DEFAULT);
        return new RedirectURL(substitutedRedirectURL,redirectType);
   }



   private String getSubstitutedURL(String urlString, Hashtable args)
   {
      IDictionary paramDictionary = new HashtableDictionary(args);
      paramDictionary.addChild(ConfigDictionary.self);
      return SubstitutorUtils.urlencodeSubstitute(urlString,paramDictionary);
   }

   private String getRootCauseCode(Throwable t)
   {
      if (m_exceptionAnalyzer == null) return null;
      return m_exceptionAnalyzer.getRootCauseCode(t);
   }

   private String getRootCause(Throwable t)
   {
         if (t instanceof CommonException)
         {
            return ((CommonException)t).getRootCause();
         }
         else
         {
            return t.getMessage();
         }
   }


} // end of class


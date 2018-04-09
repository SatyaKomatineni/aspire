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
 * Deprecation Notes:
 * Consider using later versions.
 * 
 * Provide support for updates via the framework.
 * The executed request could return an IDataCollection
 * or a RequestExecutorResponse or an exception.
 * So the three cases are:
 *
 * 1. IDataCollection
 * 2. RequestExecutorResponse
 * 3. Exception
 *
 * The responses by this servlet for each one is as follows:
 *
 * 1. IDataCollection
 *    This response is expected when the request has been validated
 *    and a set of errors returned to the servlet.
 *    These errors are printed to an html page. The name of the page
 *    is defined in the properties file under the key:
 *
 *    RequestExecutor.validationFailurePage=<filename>
 *
 *    If there are no errors then this error set should be null.
 *    A null set is considered a successful execution.
 *
 *    If you are using one of the StoredProcedureExecutors mention
 *    the query type as non-update.
 *
 * 2. RequestExecutorResponse
 *
 *    This happens when the request is tagged as update and no validation
 *    is indicated.  If the response comes back as a success, then the
 *    processing for a successful page continues.
 *
 *    Currently there is no processing for a bad return code. It is expected
 *    that in that case an exception is returned.
 *
 * 3. Case of Exception
 *    A failure page is displayed with the exception related messages.
 *    The entry for this page in the properties file is:
 *
 *    RequestExecutor.failure_page=<filename>
 *
 * How to use in the properties file
 * **********************************
 *
 * 1. Make sure there are entries for the following
 *    RequestExecutor.validationFailurePage=<filename>
 *    RequestExecutor.failure_page=<filename>
 * 2. Make sure that the request has 'update' mentioned as the query type
 *    request.request_name.className=com.ai.db.DBRequestExecutor1
 *    request.request_name.query_type=update
 *    request.request_name.stmt=<update/insert/delete statement>
 *
 * 3. Case of a stored procedure as the request executor
 *
 *    request.request_name.className=com.ai.db.StoredProcedureExecutor
 *    request.request_name.query_type=update
 *    request.request_name.stmt=<stored procedure name>
 *
 *    Make sure the stored procedure takes a refcursor in
 *    Stored procedure is also responsible for doing the validation
 *    If the validation fails return atleast one row via the refcursor
 *    The column names of these fields are:
 *
 *    field_name, field_value, error_message
 *
 *    If validation is sucessful, simply do not open the refcursor
 *
 * 4. Also you might want to use the AITransform as the main transform
 *    Otherwise you have to modify the validation template to use the
 *    RLF replace tags as opposed to the {}.
 *
 */
public class RequestExecutorServlet extends ProfileEnabledServlet
{

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
      throws ServletException, IOException
   {
      // set the response header so that it is always expired
      response.setDateHeader("Expires",0);

      // For the given URL get the html template file
      String request_name = (String)parameters.get("request_name");
      if (request_name == null )
      {
         PrintUtils.writeCompleteMessage(out,"Parameter called 'request_name' is required");
         return;
      }
      try
      {
        // validate the request if necessary
        String validateReq = AppObjects.getIConfig().getValue("request." + request_name + ".validateRequest",null);
        if (validateReq != null)
        {
            // validation requested
             IFormHandler errorDataSet = validateAndGetErrorDataSet(validateReq, parameters );
             if (errorDataSet != null)
             {
                // Validation came up with errors
                AppObjects.log("RequestExecutorServlet:Validation failed");
                sendValidationFailure(out, request_name, response,errorDataSet);
                return;
             }
         }
         // Execute the request and get back a request response object
         // Gather arguments in to a vector
         Object obj = AppObjects.getIFactory().getObject(request_name,parameters);
         boolean bSuccess = false;

         if (obj instanceof IDataCollection )
         {
            IDataCollection col = (IDataCollection)obj;
            IIterator colItr = col.getIIterator();
            colItr.moveToFirst();
            if (colItr.isAtTheEnd())
            {
              // No error rows
              // Considered a success
              col.closeCollection();
              bSuccess = true;
            }
            else
            {
                ConstructableFormHandler dataSet = new ConstructableFormHandler("ErrorSetForm",null);
                dataSet.addArgument("request_name",request_name);
                dataSet.addControlHandler("ErrorSet"
                                   , new GenericTableHandler1("ErrorSet", dataSet, col ));
                sendValidationFailure(out, request_name, response, dataSet );
            }
            return;
         }
         else
         {
            RequestExecutorResponse execResponse = (RequestExecutorResponse)obj;
            bSuccess = (execResponse.getReturnCode() == RequestExecutorResponse.SUCCESS) ? true : false;
         }
         if (bSuccess)
         {
            // get the redirect url
            try
            {
               String redirectURL = AppObjects.getIConfig().getValue("request." + request_name + ".redirectURL");
               String substitutedRedirectURL = com.ai.jawk.StringProcessor.substitute(redirectURL,parameters);
         		//response.sendRedirect(response.encodeRedirectUrl(substitutedRedirectURL));
               ServletUtils.redirect(substitutedRedirectURL
                                    ,request
                                    ,response
                                    ,getServletConfig().getServletContext());
            }
            catch(com.ai.application.interfaces.ConfigException x)
            {
               AppObjects.log("Warn: Failed to obtain a redirect URL ",x);
               sendSuccess(out,request_name);
            }
            catch(java.io.IOException x)
            {
               AppObjects.log("Warn: Redirect failed ",x);
               sendSuccess(out,request_name);
            }

         }
      }
      catch( com.ai.common.CommonException x)
      {
         AppObjects.log(x);
         sendFailure(out,request_name,request,response,parameters,x);
      }
   }
   void sendSuccess(PrintWriter out, String requestName )
   {
      try
      {
        // get the page name
        String successPage = FileUtils.translateFileIdentifier("RequestExecutor.success_page");
        StringBuffer buf = FileUtils.convertToStringBuffer(successPage);
        Hashtable args = new Hashtable();
        args.put("request_name",requestName);
        String message = com.ai.jawk.StringProcessor.substitute(buf.toString(),args);
        out.print(message);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         AppObjects.log("Error: ConfigException",x);
         PrintUtils.writeException(out,x);
      }
      catch(java.io.IOException x)
      {
         AppObjects.log("Error: IOException",x);
         PrintUtils.writeException(out,x);
      }

   }
   void sendFailure(PrintWriter out
                     , String requestName
                     , HttpServletRequest  request
                     , HttpServletResponse response
                     , Hashtable params
                     , Throwable t )
                     throws ServletException
   {
      try
      {
         // Try to redirect it to success page
         params.put("aspire.return_code","failure");
         params.put("aspire.requst_name",requestName);
         params.put("aspire.error_message",t.getMessage());
         String redirectURL = getFailureRedirectURL(requestName,params);
         if (redirectURL != null)
         {
         		//response.sendRedirect(response.encodeRedirectUrl(redirectURL));
               ServletUtils.redirect(redirectURL
                                    ,request
                                    ,response
                                    ,getServletConfig().getServletContext());
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
          out.print(message);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         AppObjects.log("Error: ConfigException",x);
         PrintUtils.writeException(out,x);
      }
      catch(java.io.IOException x)
      {
         AppObjects.log("Error: IOException",x);
         PrintUtils.writeException(out,x);
      }
      catch(RedirectorException x)
      {
         AppObjects.log("Error: RedirectorException",x);
         PrintUtils.writeException(out,x);
      }
   }
   private String getSuccessRedirectURL(String requestName, Hashtable args)
   {
        String redirectURL = AppObjects.getIConfig().getValue("request." + requestName + ".successRedirectURL", null);
        if (redirectURL == null) return null;
        String substitutedRedirectURL = com.ai.jawk.StringProcessor.substitute(redirectURL,args);
        AppObjects.log("res: successRedirectURL :" + substitutedRedirectURL);
        return substitutedRedirectURL;
   }
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
   /**
    * Validation failure
    */
   void sendValidationFailure(PrintWriter out
                     , String requestName
                     , HttpServletResponse response
                     , IFormHandler errorDataSet)
   {
      try {
          // get the page name
          String failurePage = FileUtils.translateFileIdentifier("RequestExecutor.validationFailurePage");
          if (errorDataSet != null)
          {
            ((IAITransform)PageDispatcherServlet.getTransformObject("NoURL")).transform(failurePage
                                                                 ,out
                                                                 ,errorDataSet );
          }
          else
          {
            AppObjects.log("re_s: No error data set specified");
          }
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         AppObjects.log("re_s: No validation failure page mentioned");
         PrintUtils.sendException(response,x);
      }
      catch(java.io.IOException x)
      {
         PrintUtils.sendException(response,x);
      }
   }

   /**
    * Return null if there are no errors
    */
   private IFormHandler validateAndGetErrorDataSet(String validateReq, Hashtable parms )
         throws com.ai.common.CommonException, com.ai.application.interfaces.ConfigException
   {
        Object obj = AppObjects.getIFactory().getObject(validateReq, parms);
        IDataCollection col = (IDataCollection)obj;
        if (col.getIIterator().isAtTheEnd())
        {
          col.closeCollection();
          return null;
        }
        ConstructableFormHandler dataSet = new ConstructableFormHandler("ErrorSetForm",null);
        dataSet.addArgument("request_name",validateReq);
        dataSet.addControlHandler("ErrorSet"
                           , new GenericTableHandler1("ErrorSet", dataSet, col ));
        return dataSet;
   }

}


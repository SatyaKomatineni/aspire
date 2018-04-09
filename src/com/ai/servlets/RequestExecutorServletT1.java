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
 * Consider using the newer version of this file
 * Feb 2012
 * 
 * August 2004
 * The following documentation is suspect
 * This documentation is not updated for sometime
 *
 * The following documentation is kept only as a history
 * And will be deleted soon
 *
 * The said old documentation follows
 * ************************************
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
public class RequestExecutorServletT1 extends ProfileEnabledServlet
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
                response.setContentType("text/html");
                sendValidationFailure(response.getWriter(), request_name, errorDataSet);
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
                response.setContentType("text/html");
                sendValidationFailure(response.getWriter(), request_name, dataSet );
            }
            return;
         }
         else if (obj instanceof RequestExecutorResponse)
         {
            // The request executor has returned a non collection
            // Assumption is that it is a request executor response
            RequestExecutorResponse execResponse = (RequestExecutorResponse)obj;
            bSuccess = (execResponse.getReturnCode() == RequestExecutorResponse.SUCCESS) ? true : false;
         }
         else
         {
            AppObjects.log("Warn:rest1 Returned neither a collection nor a requestexecutorresponse. Assuming a successful execution");
            bSuccess = true;
         }
         if (bSuccess)
         {
            // get the redirect url
            try
            {
               //String redirectURL = AppObjects.getIConfig().getValue("request." + request_name + ".redirectURL");
               String redirectURL = getSuccessRedirectURL1(request_name,parameters);

               String substitutedRedirectURL = ServletUtils.getSubstitutedURL(redirectURL,parameters);

         		//response.sendRedirect(response.encodeRedirectUrl(substitutedRedirectURL));
               ServletUtils.redirect(substitutedRedirectURL
                                    ,request
                                    ,response
                                    ,getServletConfig().getServletContext());
            }
            catch(com.ai.application.interfaces.ConfigException x)
            {
               AppObjects.log("Warn: Failed to obtain a redirect URL ",x);
               sendSuccess(out,request_name,request,response);
            }
            catch(java.io.IOException x)
            {
               AppObjects.log("Warn: Redirect failed ",x);
               sendSuccess(out,request_name,request,response);
            }

         }
      }
      catch( com.ai.common.CommonException x)
      {
         AppObjects.log(x);
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
        AppObjects.log("res: successRedirectURL :" + substitutedRedirectURL);
        return substitutedRedirectURL;
   }

   private String getSuccessRedirectURL1(String requestName, Hashtable args)
         throws ConfigException
   {
         //Look for a key
         String targetUrlKey = (String)(args.get("aspire_target_url_key"));
         if (targetUrlKey != null)
         {
            //target url key exists
            String redirectURL = AppObjects.getIConfig().getValue("request." + requestName + ".redirectURL." + targetUrlKey, null);
            if (redirectURL != null)
               return redirectURL;
         }
         //key is not there
         String redirectURL = AppObjects.getIConfig().getValue("request." + requestName + ".redirectURL");
         return redirectURL;
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
         return new DUpdateServletRequestFailureResponse1();
      }
      else
      {
         return (IServletRequestUpdateFailure)failureResponseObj;
      }

   }
   /**
    * Validation failure
    */
   void sendValidationFailure(PrintWriter out
                     , String requestName
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
         PrintUtils.writeException(out,x);
      }
      catch(java.io.IOException x)
      {
         PrintUtils.writeException(out,x);
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

}

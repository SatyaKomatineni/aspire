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
import com.ai.testservlets.IMultipartRequestListner;
import com.ai.testservlets.MultipartRequest;
import com.ai.testservlets.MultipartInputStreamHandler;

public class MultipartRequestExecutorServlet extends ProfileEnabledServlet
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
      String request_name = "uninitialized";
      try
      {
         // parameters may not be valid being a multipart request
         MultipartRequest fileUploadRqst = new MultipartRequest(request
                       ,getFileStagingDirectory()
                        ,new CMultipartRequestListner(parameters));

         // parameters would have been collected by now
         // Add the multipart request as an additional param
         parameters.put(com.ai.servlets.AspireConstants.MULTIPART_REQUEST_OBJ_NAME
                     ,fileUploadRqst);

         // For the given URL get the html template file
         request_name = (String)parameters.get("request_name");
         if (request_name == null )
         {
            PrintUtils.writeCompleteMessage(out,"Parameter called 'request_name' is required");
            return;
         }
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
                sendValidationFailure(out, request_name, errorDataSet);
                return;
             }
         }
         // Execute the request and get back a request response object
         // Gather arguments in to a vector
         parameters.put("aspire.httpRequest",request);
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
                sendValidationFailure(out, request_name, dataSet );
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
//         		response.sendRedirect(response.encodeRedirectUrl(substitutedRedirectURL));
               ServletUtils.redirect(substitutedRedirectURL
                                    ,request,response,getServletConfig().getServletContext());
            }
            catch(com.ai.application.interfaces.ConfigException x)
            {
               AppObjects.log("Failed to obtain a redirect URL ");
               AppObjects.log(x);
               sendSuccess(out,request_name,request,response,parameters);
            }
            catch(java.io.IOException x)
            {
               AppObjects.log("redirect failed ");
               AppObjects.log(x);
               sendSuccess(out,request_name,request,response,parameters);
            }

         }
      }
      catch( com.ai.common.CommonException x)
      {
         AppObjects.log(x);
         sendFailure(out,request_name,request,response,parameters,x);
      }
      catch( java.io.IOException x)
      {
         AppObjects.log(x);
         sendFailure(out,request_name,request,response,parameters,x);
      }
   }
   void sendSuccess(PrintWriter out
                  , String requestName
                  , HttpServletRequest request
                  , HttpServletResponse response
                  , Hashtable params) throws ServletException
   {
      try
      {
         // Try to redirect it to success page
         params.put("aspire.return_code","success");
         params.put("asipre.request_name",requestName);
         String redirectURL = getSuccessRedirectURL(requestName, params);
         if ( redirectURL != null)
         {
//         		response.sendRedirect(response.encodeRedirectUrl(redirectURL));
               ServletUtils.redirect(redirectURL
                                    ,request,response
                                    ,getServletConfig().getServletContext()
                                    );
               return;
         }
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
         PrintUtils.writeException(out,x);
      }
      catch(java.io.IOException x)
      {
         PrintUtils.writeException(out,x);
      }
      catch(RedirectorException x)
      {
         PrintUtils.writeException(out,x);
      }
   }
   private String getSuccessRedirectURL(String requestName, Hashtable args)
   {
        String redirectURL = AppObjects.getIConfig().getValue("request." + requestName + ".successRedirectURL", null);
        if (redirectURL == null) return null;
        String substitutedRedirectURL = com.ai.jawk.StringProcessor.substitute(redirectURL,args);
        AppObjects.trace(this,"mpre: successRedirectURL :%1s",substitutedRedirectURL);
        return substitutedRedirectURL;
   }
   private String getFailureRedirectURL(String requestName, Hashtable args)
   {
        String redirectURL = AppObjects.getIConfig().getValue("request." + requestName + ".failureRedirectURL", null);
        if (redirectURL == null) return null;
        String substitutedRedirectURL = com.ai.jawk.StringProcessor.substitute(redirectURL,args);
        AppObjects.trace(this,"mpre: failureRedirectURL :%1s",substitutedRedirectURL);
        return substitutedRedirectURL;
   }

   private void sendFailure(PrintWriter out
                     , String requestName
                     , HttpServletRequest request
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
         String redirectURL = getFailureRedirectURL(requestName,params);
         if (redirectURL != null)
         {
//         		response.sendRedirect(response.encodeRedirectUrl(redirectURL));
               ServletUtils.redirect(redirectURL
                                    ,request,response
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
          PrintUtils.sendCompleteMessage(response,message);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         PrintUtils.writeException(out,x);
      }
      catch(java.io.IOException x)
      {
         PrintUtils.writeException(out,x);
      }
      catch(com.ai.servlets.RedirectorException x)
      {
         PrintUtils.writeException(out,x);
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
   private String getFileStagingDirectory()
      throws ConfigException
   {
      String stagingDir =
         AppObjects.getIConfig().getValue(AspireConstants.STAGING_DIRECTORY_NAME);
      return stagingDir;
   }
}
class CMultipartRequestListner implements IMultipartRequestListner
{
   Hashtable m_parameters;
   Hashtable m_files;

   CMultipartRequestListner(Hashtable parameters)
   {
      m_parameters = parameters;
   }
   public boolean beginProcess(HttpServletRequest request, MultipartInputStreamHandler stream)
   {
      return true;
   }
   public boolean newParameter(String name, String value )
   {

      AppObjects.trace(this,"New parameter : %1s:%2s",name.toLowerCase(),value);
      if ((name != null) && (value != null))
      {
         m_parameters.put(name.toLowerCase(),value);
      }
      return true;
   }
   public boolean newFile(String filename)
   {
      return true;
   }
   public String suggestAFilename(String filename )
   {
      AppObjects.log("Filename detected : " + filename );
      return filename;
   }
}

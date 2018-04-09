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
import com.ai.servlets.compatibility.ServletCompatibility;
import com.ai.servletutils.*;
import com.ai.htmlgen.*;
import com.ai.data.IIterator;
import com.ai.data.IDataCollection;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.common.*;

public class PageDispatcher
{
   public void serviceRequest(  HttpSession session,
                                Hashtable parameters,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                ServletContext servletContext)
      throws ServletException, IOException, AspireServletException
   {
          String templateHtml = null;
      try
      {
          // For the given URL get the html template file
          String url = (String)parameters.get("url");
          String pre_execute_request = (String)parameters.get("pre_execute_request");

          if (url == null )
          {
             PrintUtils.sendCompleteMessage(response,"Parameter called 'url' is required");
             return;
          }
          if (pre_execute_request != null)
          {
             preExecuteRequest(pre_execute_request, parameters );
             AppObjects.trace(this,"Parameters after pre-execute :%1s", parameters );
          }
          // Get html template file for this url
          templateHtml = FileUtils.translateFileIdentifier(url);
          // Get a form handler that can handle this form
          //String formHandlerName = AppObjects.getValue(url+".formHandlerName");
          //IFormHandler formHandler =  FormUtils.getFormHandlerFor(formHandlerName,parameters);

          Object formHandlerObject = FormUtils.getDataObjectFor(url,parameters);
          IFormHandler updatedFormHandler = null;
          if (formHandlerObject instanceof IFormHandler)
          {
             // Incase if the user data needs to be preserved on the form
             IDictionary userFormData = (IDictionary)request.getAttribute(
                   AspireConstants.REQUEST_OVERRIDE_DICTIONARY);
             {
                IFormHandler formHandler = (IFormHandler)formHandlerObject;
                if (userFormData != null)
                {
                   updatedFormHandler = new UpdateFormHandlerAdapter(userFormData
                         ,(IFormHandler1)formHandler);
                }
                else
                {
                   updatedFormHandler = formHandler;
                }
             }
             // retrieve any profile arguments in to the user profile
             IIterator parmItr = updatedFormHandler.getKeys();
             if (parmItr != null)
             {
                for(parmItr.moveToFirst();!parmItr.isAtTheEnd();parmItr.moveToNext())
                {
                   String key = (String)parmItr.getCurrentElement();
                   String lowerCaseKey = key.toLowerCase();
                   if (key.startsWith("profile_"))
                   {
                      AppObjects.trace(this,"Profile key found :%1s",key );
                      ServletCompatibility.putSessionValue(session,key,updatedFormHandler.getValue(key));
                   }
                }
             }
          }
          Object trObj = PageDispatcherServlet.getTransformObject(url);
          if (trObj instanceof IAITransform)
          {
            ((IAITransform)trObj).transform(templateHtml
                                          ,ServletUtils.getSuitablePrintWriter(request,response,null)
                                          ,updatedFormHandler);
          }
          else
          {
             if (updatedFormHandler != null)
             {
                //The passed object is an IFormHandler
                //From this an updatedform handler is constrcuted
                //So call the regular transform
                PageDispatcherServlet.performJSPTransform((IAIHttpTransform)trObj
                      ,templateHtml
                      ,updatedFormHandler
                      ,request
                      ,response
                      ,servletContext);
                //Object has been transformed
                //No further work
                return;
             }

             //The passed object is not an IFormHandler
             //It is an open object that got passed
             //Only the receiving transform will know what it is

             PageDispatcherServlet.performJSPTransform((IAIHttpTransform)trObj
                   ,templateHtml
                   ,formHandlerObject
                   ,request
                   ,response
                   ,servletContext);
          }
      }
      catch( com.ai.application.interfaces.ConfigException x)
      {
         throw new AspireServletException("Config Exception",x);
      }
      catch( com.ai.application.interfaces.RequestExecutionException x)
      {
         throw new AspireServletException("RequestExecutionException",x);
      }
      catch(com.ai.data.DataException x)
      {
         throw new AspireServletException("Data exception",x);
      }
   }
   private void preExecuteRequest(String requestName, Hashtable arguments )
      throws RequestExecutionException, com.ai.data.DataException
   {
      Vector v = new Vector();
      v.addElement(arguments);
      Object obj = AppObjects.getIFactory().getObject(requestName, v );
      if (obj instanceof IDataCollection)
      {
         IDataCollection dataCol = (IDataCollection)obj;
         dataCol.closeCollection();
      }
      AppObjects.log("The parameters in the hashtable after preexecution are:");
      AppObjects.log(arguments.toString());
   }

   /**
    * @deprecated. Use PageDispatcherServlet.getTransformObject
    * @param url
    * @return
    */
   private static IAITransform getTransformObject(String url)
   {

      try
      {
         // See if there is a special transform for this object
         Object pageLevelTransform = AppObjects.getIFactory().getObject(url + ".transform", null);
         return (IAITransform)pageLevelTransform;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.warn("PageDispatcher","pd: Page level transformation not available");
         AppObjects.warn("PageDispatcher","pd: Continuing with Application level transformation");
      }
      try
      {
         //See if you can locate a transformation object
         Object obj = AppObjects.getIFactory().getObject(IAITransform.GET_TRANSFORM_OBJECT,null);
         return (IAITransform)obj;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.warn("PageDispatcher","pd: Could not obtain the transform from the config file");
         AppObjects.warn("PageDispatcher","pd: Using the default HtmlParser as the transformation object");
//         AppObjects.log(x);
         return new HtmlParser();
      }
   }

}

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.testservlets;

import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.StringTokenizer;
import com.ai.application.utils.AppObjects;
import com.ai.servletutils.*;
import com.ai.htmlgen.*;
import com.ai.data.IIterator;
import com.ai.data.IDataCollection;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.common.*;

import com.ai.servlets.ProfileEnabledServlet;
import com.ai.servlets.AspireSession;

// public class PageDispatcherServlet extends BaseServlet
public class PageDispatcherServlet extends ProfileEnabledServlet
{

  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
  }
  public long getLastModified(HttpServletRequest request)
  {
      System.out.println("Get Last Modified called ");
      System.out.println("Date header : " + request.getDateHeader("If-Modified-Since"));

  // let's write the logic first
  // Remove any pages from the cache that needs to be removed
  // See if the current url is still in the cache
  // If in the cache return the appropriate time
  // If it is not in the cache, return current time

     HttpSession session = request.getSession(true);
     AspireSession aspireSession
      = AspireSession.getAspireSessionFromHttpSession(session,true);
     Hashtable sentPageRegistry = aspireSession.getSentPageRegistry();

     /*
      * Remove any cached objects from the cache
      */
     Vector pagesToBeRemoved = new Vector();
     // Determine the pages to be removed
     for (Enumeration e=sentPageRegistry.keys();e.hasMoreElements();)
     {
         SentPage thisPage = (SentPage)e.nextElement();
         if (!thisPage.doYouWantToBeInCache(request))
         {
            pagesToBeRemoved.addElement(thisPage);
         }
     }
     // Remove the pages
     for(Enumeration e=pagesToBeRemoved.elements();e.hasMoreElements();)
     {
         sentPageRegistry.remove(e.nextElement());
     }

     /*
      * Given URL may or may not exist in the cache
      */
     Object urlObj = request.getParameter("url");
     if (urlObj != null)
     {
         // url exists
        SentPage sentPage = (SentPage)sentPageRegistry.get(urlObj);
        return sentPage.getTime();
     }
     else
     {
         //not exists
         return aspireSession.getLastModifiedTimeForThisURL(true);
     }

  }
   public void serviceRequest(String user,
                              HttpSession session,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
   {
        // set the response header so that it is always expired
        response.setDateHeader("Expires",0);
          String templateHtml = null;
      try
      {
          // For the given URL get the html template file
          String url = (String)parameters.get("url");
          String pre_execute_request = (String)parameters.get("pre_execute_request");

          if (url == null )
          {
             PrintUtils.writeCompleteMessage(out,"Parameter called 'url' is required");
             return;
          }
          if (pre_execute_request != null)
          {
             preExecuteRequest(pre_execute_request, parameters );
             AppObjects.log("Parameters after pre-execute :" + parameters );
          }
          // Get html template file for this url
          templateHtml = FileUtils.translateFileIdentifier(url);
          // Get a form handler that can handle this form
          String formHandlerName = AppObjects.getValue(url+".formHandlerName");
          IFormHandler formHandler =  FormUtils.getFormHandlerFor(formHandlerName
                                                                  ,parameters);
          // retrieve any profile arguments in to the user profile
          IIterator parmItr = formHandler.getKeys();
          if (parmItr != null)
          {
            for(parmItr.moveToFirst();!parmItr.isAtTheEnd();parmItr.moveToNext())
            {
               String key = (String)parmItr.getCurrentElement();
               String lowerCaseKey = key.toLowerCase();
               if (key.startsWith("profile_"))
               {
                  AppObjects.log("Profile key found :" + key );
                  session.putValue(key,formHandler.getValue(key));
               }
            }
          }
          getTransformObject(url).transform(templateHtml
                                          ,out
                                          ,formHandler);
         /*
          * Add the page to cache if necessary
          */
          AspireSession aspireSession = AspireSession.getAspireSessionFromHttpSession(session,false);
          aspireSession.getSentPageRegistry().put(url
                ,new SentPage(url,aspireSession.getLastModifiedTimeForThisURL(false)));
      }
      catch( com.ai.application.interfaces.ConfigException x)
      {
         PrintUtils.writeException(out,x);
         AppObjects.log(x);
      }
      catch( com.ai.application.interfaces.RequestExecutionException x)
      {
         PrintUtils.writeException(out,x);
         AppObjects.log(x);
      }
      catch(java.io.IOException x)
      {
         PrintUtils.writeCompleteMessage(out,templateHtml + "not found");
         AppObjects.log(x);
      }
      catch(com.ai.data.DataException x)
      {
         PrintUtils.writeException(out,x);
         AppObjects.log(x);
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
   public static IAITransform getTransformObject(String url)
   {

      try
      {
         // See if there is a special transform for this object
         Object pageLevelTransform = AppObjects.getIFactory().getObject(url + ".transform", null);
         return (IAITransform)pageLevelTransform;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("pd: Page level transformation not available");
         AppObjects.log("pd: Continuing with Application level transformation");
      }
      try
      {
         //See if you can locate a transformation object
         Object obj = AppObjects.getIFactory().getObject(IAITransform.GET_TRANSFORM_OBJECT,null);
         return (IAITransform)obj;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("pd: Could not obtain the transform from the config file");
         AppObjects.log("pd: Using the default HtmlParser as the transformation object");
//         AppObjects.log(x);
         return new HtmlParser();
      }
   }
// end of main class
}

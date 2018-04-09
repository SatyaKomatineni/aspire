/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.htmlgen.test;
import com.ai.htmlgen.*;
import java.io.*;
import com.ai.application.defaultpkg.ApplicationHolder;

import com.ai.test.*;
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
import com.ai.common.FileUtils;
import com.ai.common.Utils;

public class TestHtmlGen
{
   static private String  PROPERTIES_FILE = "g:\\secondary\\ai\\aspire_samples\\ptr\\properties\\aspire.properties";
   static private String  OUTPUT_FILE = "q:\\work\\work\\test.html";
   static private String  URL_TO_TEST = "TransferOrdersJSPURL";

/*
   static private String  PROPERTIES_FILE = "q:\\ai\\aspire_samples\\aspire_samples\\test\\properties\\aspire.properties";
   static private String  OUTPUT_FILE = "q:\\work\\work\\test.html";
   static private String  URL_TO_TEST = "TestReportLoopsURL";
*/
  public static void main(String[] args)
  {
         PrintWriter outWriter = null;
        try {
                ApplicationHolder.initApplication(
                       PROPERTIES_FILE ,null);
                outWriter = new PrintWriter(
                                                new BufferedWriter(
                                                        new FileWriter(OUTPUT_FILE)),true);

                System.out.println("Test begins");
                CommandRequest cmdR  = CommandRequest.readFromInput();
                TestHtmlGen testObj = new TestHtmlGen();

                Map<String,String> params = cmdR.getArgs();

                testObj.serviceRequest( params,outWriter);
        }
        catch(Exception x)
        {
                x.printStackTrace();
        }
        finally
        {
          if (outWriter !=  null)
          {
            outWriter.close();
            System.out.println("Test complete");
          }
        }
  }

   public void serviceRequest(  Map<String,String> parameters,
                                PrintWriter out )
   {
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
                                                      ,Utils.getAsHashtable(parameters));
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
//                  session.putValue(key,formHandler.getValue(key));
               }
            }
          }
          getTransformObject(url).transform(templateHtml
                                          ,out
                                          ,formHandler);
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
   private void preExecuteRequest(String requestName, Map<String,String> arguments )
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

}//eof-function

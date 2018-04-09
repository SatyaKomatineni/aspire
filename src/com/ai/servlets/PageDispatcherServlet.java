/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 *
 *DeprecationNotes:
 *Consider deprecating this class.
 *There is a new version of it.
 *
 *
 */
package com.ai.servlets;

import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import com.ai.application.utils.AppObjects;
import com.ai.servlets.compatibility.ServletCompatibility;
import com.ai.servletutils.*;
import com.ai.htmlgen.*;
import com.ai.data.IIterator;
import com.ai.data.IDataCollection;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.masterpage.*;
import com.ai.common.FileUtils;

// public class PageDispatcherServlet extends BaseServlet
public class PageDispatcherServlet extends ProfileEnabledServlet
{
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
  }
   public void serviceRequest(String user
                              ,HttpSession session
                               , String uri
                                ,String query
                                ,Hashtable parameters
                                ,PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
      throws ServletException, IOException
   {
        // set the response header so that it is always expired
        // response.setDateHeader("Expires",0);
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
          //String formHandlerName = AppObjects.getValue(url+".formHandlerName");

          //IFormHandler formHandler =  FormUtils.getFormHandlerFor(formHandlerName
          //                                                        ,parameters);
		  Object formHandlerObject = FormUtils.getDataObjectFor(url,parameters);
		  if (formHandlerObject instanceof IFormHandler)
		  {
		  	  IFormHandler formHandler = (IFormHandler)formHandlerObject;
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
					ServletCompatibility.putSessionValue(session,key,formHandler.getValue(key));
	               }
	            }
	          }
		  }
          // befor you really transform see if the user has specified
          // any specific data output
          IFormHandlerTransform dataTransform
            = getDataTransform(url,parameters);

          if (dataTransform != null)
          {
              // data transform exist
          	  if (this.shoudIIncludeMasterPageHeaders(url,parameters))
          	  {
          	  	AppObjects.info(this,"Generic transformation requested");
          	  	AppObjects.info(this,"master page parameters are going to be included if available");
          	  	this.insertMasterPageParameters(url,parameters);
          	  }
              dataTransform.transform((IFormHandler)formHandlerObject,out);
              return;
          }

          //Back to the transform business
          IMasterPage masterPage = getMasterPage(url,parameters);
          if (masterPage != null)
          {
             //Master page exists
             parameters.put("aspire_masterpage","on");
             parameters.put("aspire_masterpage_tophalf",masterPage.topHalf());
             parameters.put("aspire_masterpage_bottomhalf",masterPage.bottomHalf());
             parameters.put("aspire_masterpage_header",masterPage.headerInclude());
             // response.getWriter().print(masterPage.topHalf());
          }
          Object trObj = getTransformObject(url);
          if (trObj instanceof IAITransform)
          {
            ((IAITransform)trObj).transform(templateHtml
                                          ,out
                                          ,(IFormHandler)formHandlerObject);
          }
          else
          {
            // Get the request dispatcher object
            performJSPTransform((IAIHttpTransform)trObj
                              ,templateHtml
                              ,formHandlerObject
                              ,request,response
                              ,getServletConfig().getServletContext());
          }

          //Closing treatement for master page
          if (masterPage != null)
          {
             //response.getWriter().print(masterPage.bottomHalf());
          }
      }
      catch( com.ai.application.interfaces.ConfigException x)
      {
         PrintUtils.writeException(out,x);
         AppObjects.log("Error: Configuration exception",x);
      }
      catch( com.ai.application.interfaces.RequestExecutionException x)
      {
         PrintUtils.writeException(out,x);
         AppObjects.log("Error: Request execution excpetion",x);
      }
      catch(java.io.IOException x)
      {
         PrintUtils.writeCompleteMessage(out,templateHtml + "not found");
         AppObjects.log("Error: IOException", x);
      }
      catch(com.ai.data.DataException x)
      {
         PrintUtils.writeException(out,x);
         AppObjects.log("Error: Data Exception", x);
      }
      catch(com.ai.common.TransformException x)
      {
          PrintUtils.writeException(out,x);
          AppObjects.log("Error: TransformException",x);
      }
      catch(com.ai.common.CommonException x)
      {
         PrintUtils.writeException(out,x);
         AppObjects.log("Error: Aspire related common Exception",x);
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
   public static Object getTransformObject(String url)
   {

      try
      {
         // See if there is a special transform for this object
         String requestTransform = AppObjects.getValue("request." + url + ".transform.classname",null);
         if (requestTransform != null)
         {
            //Backward compatiable transform selection
            Object pageLevelTransform = AppObjects.getIFactory().getObject(url + ".transform", null);
            return pageLevelTransform;
         }
         //Try the new way of doing this
         Object pageLevelTransform = AppObjects.getObjectAbsolute(url + ".transform",null);
         return pageLevelTransform;
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
         return obj;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("pd: Could not obtain the transform from the config file");
         AppObjects.log("pd: Using the default HtmlParser as the transformation object");
//         AppObjects.log(x);
         return new HtmlParser();
      }
   }
   /*****************************************************************
    * PrintWriter related hooks
    *****************************************************************
    */
   public String qhdGetContentType( HttpServletRequest request, HttpServletResponse response)
   {
      String url = request.getParameter("url");
      if (url == null)
      {
         return "text/html";
      }
      String contentType = AppObjects.getIConfig().getValue(url + ".contentType", new String("text/html"));
      return contentType;
   }

   public PrintWriter qhdGetPrintWriter(HttpServletRequest request, HttpServletResponse response)
      throws IOException
   {
      try
      {
         // If the transform is JSP don't bother
         String url = request.getParameter("url");
         if (url == null)
         {
            return super.qhdGetPrintWriter(request,response);
         }
         String transformType = AppObjects.getIConfig().getValue(url + ".transformtype", null);
         if (transformType == null)
         {
            SetHeaders.setHeaders1(url,request,response);
            return response.getWriter();
         }
         // String outputFormat = (String)request.getAttribute("aspire_output_format");
         String outputFormat = request.getParameter("aspire_output_format");
         if (outputFormat != null)
         {
             SetHeaders.setHeaders1(url,request,response);
             return response.getWriter();
         }
         if (transformType.equals("jsp"))
         {
            // dont get the out
            AppObjects.log("info: JSP transform detected");
            return null;
         }
         SetHeaders.setHeaders1(url,request,response);
         return response.getWriter();
      }
      catch(AspireServletException x)
      {
         AppObjects.log("Error: Could not set response headers. ", x);
         throw new IOException("Error: Could not set response headers. " + x.getMessage());
      }
   }

   /*****************************************************************
    * JSP transform related
    *****************************************************************
    */
    static void performJSPTransform(IAIHttpTransform trObj
                                    ,String jspURL
                                    ,Object formHandler
                                    ,HttpServletRequest request
                                    ,HttpServletResponse response
                                    ,ServletContext  servletContext
                                    )
                                    throws ServletException, IOException
    {
            try
            {
				RequestDispatcher rd = servletContext.getRequestDispatcher(jspURL);
            	if (trObj instanceof IAIHttpTransform1)
            	{
					// Get the request dispatcher object
					IAIHttpTransform1 tr1Obj = (IAIHttpTransform1)trObj;
					tr1Obj.transform(jspURL
									,formHandler
									,request
									,response
									,rd);
					return;
            	}

               // Get the request dispatcher object
               trObj.transform(jspURL
                               ,(IFormHandler)formHandler
                               ,request
                               ,response
                               ,rd);
            }
            finally
            {
               if (formHandler instanceof IFormHandler)
               {
	               ((IFormHandler)formHandler).formProcessingComplete();
			   }
            }
    }
    /*****************************************************************
     * performAITransform: AITransform related
     * Originally introduced for allowing post transformation decorator
     *****************************************************************
     */
    static void performAITransform(IAITransform trObj
    		, String urlTemplate
    		, PrintWriter out
    		, IFormHandler formHandlerObject)
    throws IOException
    {
    	
	    ((IAITransform)trObj).transform(urlTemplate
	            ,out
	            ,(IFormHandler)formHandlerObject);
    }
    static 
    /*****************************************************************
     * getDataTransform
     *****************************************************************
    */
   private IFormHandlerTransform getDataTransform(String url, Map parameters)
   {
       String outputFormat = (String)parameters.get("aspire_output_format");
       //If no output format is specified let it go
       if (outputFormat == null) return null;

       //output format available
       try
       {
           IFormHandlerTransform tr
            = (IFormHandlerTransform)AppObjects.getObjectAbsolute("GenericTransform." + outputFormat,null);
           return tr;
       }
       catch(RequestExecutionException x)
       {
           AppObjects.log("Warn: Output transform not available for the requested output format",x);
           return null;
       }
   }//eof-function

   private IMasterPage getMasterPage(String url, Map parameters)
         throws RequestExecutionException
   {
      //***********************************************
      //See if master page is disabled globally
      //So return no master page
      //***********************************************

      String bMpEnabled = AppObjects.getValue(AspireConstants.MP_ENABLE_MASTER_PAGE,"false");
      if (bMpEnabled.equals("false"))
      {
         return null;
      }

      // master page is enabled at the global level
      AppObjects.log("Info:Master page enabled");

      //See if the master page is enabled at the url level
      String mpRequestName = AppObjects.getValue(url + ".masterPageRequestName",null);
      if (mpRequestName == null)
      {
         //There is not a master page request
         //See if global overide is there
         String bUrlOverride = AppObjects.getValue(AspireConstants.MP_OVERRIDE_URL,"false");
         if (bUrlOverride.equals("false"))
         {
            //There is no override
            return null;
         }
         //There is a global override
         AppObjects.log("Info: Returning the global master page as a result of url override");
         return getGlobalDefaultMasterPage(url,parameters);
      }

      //Master page is enabled for this url
      //Master page request name is specified for this url
      return (IMasterPage)AppObjects.getObject(mpRequestName,parameters);
   }

   private IMasterPage getGlobalDefaultMasterPage(String url, Map parameters)
   {
      try
      {
         String mpRN = AppObjects.getValue(AspireConstants.MP_GLOBAL_MASTER_PAGE_REQUEST_NAME,null);
         if (mpRN == null)
         {
            //There is no global setting
            return new SampleMasterPage();
         }
         //There is a global request
         IMasterPage globalMP =
            (IMasterPage)AppObjects.getObject(mpRN,parameters);
         return globalMP;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("Warn: Could not obtain the global master page request object",x);
         return new SampleMasterPage();
      }
   }
   private void insertMasterPageParameters(String url, Map parameters)
   throws RequestExecutionException, AspireServletException
   {
	   //Back to the transform business
	   IMasterPage masterPage = getMasterPage(url,parameters);
	   if (masterPage != null)
	   {
	      //Master page exists
	      parameters.put("aspire_masterpage","on");
	      parameters.put("aspire_masterpage_tophalf",masterPage.topHalf());
	      parameters.put("aspire_masterpage_bottomhalf",masterPage.bottomHalf());
	      parameters.put("aspire_masterpage_header",masterPage.headerInclude());
	      // response.getWriter().print(masterPage.topHalf());
	   }
   }//eof-function
   private boolean shoudIIncludeMasterPageHeaders(String url, Map parameters)
   {
   	      String includeMasterPageParams = (String)parameters.get("aspire_masterpage_include_headers");
   	      if (includeMasterPageParams == null)
   	      {
   	      	//no string
   	      	return false;
   	      }
   	      //string available
   	      if (includeMasterPageParams.equalsIgnoreCase("true"))
   	      {
   	      	return true;
   	      }
   	      else
   	      {
   	      	return false;
   	      }
   }
}//eof-class

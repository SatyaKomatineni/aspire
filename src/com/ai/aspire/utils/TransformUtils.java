package com.ai.aspire.utils;
import java.util.*;
import java.io.*;
import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import com.ai.common.*;
import com.ai.data.DataException;
import com.ai.data.IIterator;
                                   
import java.io.PrintWriter;
import com.ai.htmlgen.*;

public class TransformUtils 
{
   public static void transform(String aspireURL, Hashtable params, PrintWriter out)
      throws TransformException
   {
      IConfig config = AppObjects.getIConfig();
      IFactory factory = AppObjects.getIFactory();

      //Get template file
      String templateHtml = null;
      IFormHandler formHandler = null;
      try
      {
          // For the given URL get the html template file
          if (aspireURL == null )
          {
             throw new TransformException("Error: Parameter called 'url' is required");
          }
          // Get html template file for this url
          templateHtml = FileUtils.translateFileIdentifier(aspireURL);
          // Get a form handler that can handle this form
          String formHandlerName = AppObjects.getValue(aspireURL+".formHandlerName");
          formHandler =  FormUtils.getFormHandlerFor(formHandlerName
                                                                  ,params);
          Object trObj = getTransformObject(aspireURL);
          if (trObj instanceof IAITransform)
          {
            ((IAITransform)trObj).transform(templateHtml
                                          ,out
                                          ,formHandler);
          }                                          
          else
          {
             throw new TransformException("Error: Unsupported transform");
          }
      }
      catch( com.ai.application.interfaces.ConfigException x)
      {
         throw new TransformException("Error: ConfigException",x);
      }                                   
      catch( com.ai.application.interfaces.RequestExecutionException x)
      {
         throw new TransformException("Error: Request execution excpetion",x);
      }                  
      catch(java.io.IOException x)
      {
         throw new TransformException("Error: IOException", x);
      }                  
      finally
      {
         if (formHandler != null)
         {
            formHandler.formProcessingComplete();
         }
      }
   }

   public static String transform(String aspireURL, Hashtable params) throws TransformException
   {
      ByteArrayOutputStream baos = null;
      PrintWriter pw = null;
      try
      {
         baos = new ByteArrayOutputStream();
         pw = new PrintWriter(baos);
         transform(aspireURL,params,pw);
         pw.close();
         return baos.toString();
      }
      finally
      {
         if (pw !=null) pw.close();
         if (baos != null) 
            com.ai.common.FileUtils.closeStream(baos);
      }
   }
   public static Object getTransformObject(String url)
   {

      try
      {         
         // See if there is a special transform for this object
         Object pageLevelTransform = AppObjects.getIFactory().getObject(url + ".transform", null);
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
         return new com.ai.xml.XSLTransform();
      }         
   }
   
   public static String transformHdsToString(String aspireURL
           , IFormHandler data)
   throws TransformException
   {
       
       ByteArrayOutputStream baos = null;
       PrintWriter pw = null;
       try
       {
          baos = new ByteArrayOutputStream();
          pw = new PrintWriter(baos);
          transformHds(aspireURL,pw,data);
          pw.close();
          return baos.toString();
       }
       finally
       {
          if (pw !=null) pw.close();
          if (baos != null) 
             com.ai.common.FileUtils.closeStream(baos);
       }
   }
   
   public static void transformHds(String aspireURL
           , PrintWriter out
           , IFormHandler data)
   throws TransformException
{
   //Get template file
   String templateHtml = null;
   try
   {
       // For the given URL get the html template file
       if (aspireURL == null )
       {
          throw new TransformException("Error: Parameter called 'url' is required");
       }
       // Get html template file for this url
       templateHtml = FileUtils.translateFileIdentifier(aspireURL);
       Object trObj = getTransformObject(aspireURL);
       if (trObj instanceof IAITransform)
       {
         ((IAITransform)trObj).transform(templateHtml
                                       ,out
                                       ,data);
       }                                          
       else
       {
          throw new TransformException("Error: Unsupported transform");
       }
   }
   catch( com.ai.application.interfaces.ConfigException x)
   {
      throw new TransformException("Error: ConfigException",x);
   }                                   
   catch(java.io.IOException x)
   {
      throw new TransformException("Error: IOException", x);
   }                  
}
   public static void transformHds(String aspireURL
           , PrintWriter out
           , IFormHandler data
           , Map additionalParams)
   throws TransformException
   {
	   if (!(data instanceof IUpdatableMap))
	   {
		   AppObjects.trace("formutils","the incoming object does not support updatable map");
		   throw new TransformException("It is not IUpdatableMap");
	   }
	   //it is a go
	   IUpdatableMap dataMap = (IUpdatableMap)data;
	   Iterator itr = additionalParams.keySet().iterator();
	   while(itr.hasNext())
	   {
		   String key = (String)itr.next();
		   dataMap.addKey(key.toLowerCase(),additionalParams.get(key));
	   }
	   TransformUtils.transformHds(aspireURL, out, data);
   }
   public static void transformHdsMp(String aspireURL,PrintWriter pw, IFormHandler formHandler)
   throws TransformException
   {
	   try
	   {
		   String templateHtml = AppObjects.getValue(aspireURL);
		   Map args = getArguments(formHandler);
		   String substTemplate = SubstitutorUtils.urlencodeSubstitute(templateHtml, args);
		   
	       Object trObj = getTransformObject(aspireURL);
	       if (trObj instanceof IAITransform)
	       {
	         ((IAITransform)trObj).transform(substTemplate
	                                       ,pw
	                                       ,formHandler);
	       }                                          
	       else
	       {
	          throw new TransformException("Error: Unsupported transform");
	       }
	   }
	   catch(Exception x)
	   {
		   throw new TransformException("Can not do transform for:" + aspireURL,x);
	   }
   }
   public static void transformHdsMp(String aspireURL
           , PrintWriter out
           , IFormHandler data
           , Map additionalParams)
   throws TransformException
   {
	   if (!(data instanceof IUpdatableMap))
	   {
		   AppObjects.trace("formutils","the incoming object does not support updatable map");
		   throw new TransformException("It is not IUpdatableMap");
	   }
	   //it is a go
	   IUpdatableMap dataMap = (IUpdatableMap)data;
	   Iterator itr = additionalParams.keySet().iterator();
	   while(itr.hasNext())
	   {
		   String key = (String)itr.next();
		   dataMap.addKey(key.toLowerCase(),additionalParams.get(key));
	   }
	   TransformUtils.transformHdsMp(aspireURL, out, data);
   }
   
   private static Map getArguments(IFormHandler hds)
   throws DataException
   {
	   Map map = new HashMap();
	   IIterator itr = hds.getKeys();
	   for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
	   {
		   String curKey = (String)itr.getCurrentElement();
		   String curValue = hds.getValue(curKey);
		   map.put(curKey,curValue);
	   }
	   return map;
   }//eof-function
}//eof-class 

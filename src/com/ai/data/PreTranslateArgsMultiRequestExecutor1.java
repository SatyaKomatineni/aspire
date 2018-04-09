/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.data;

import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.sql.*;
import java.util.*;
import com.ai.common.*;
import com.ai.db.*;
import com.ai.data.*;
import com.ai.servlets.AspireConstants;       
/**
 * 
 * PreTranslateArgsMultiRequestExecutor1
 * Acts as DataCollectionProducer and AspireUpdateHandler
 *
 * 7/13/16: observations only
 * ***************************
 * It returns only the last main requests output.
 * it will execute intermediate requests
 * request.1 -> exec
 * request.2 -> exec
 * request -> Only this result is returned
 *  
 * Changes
 * ******************
 * 1. to provide a stop in the pipeline
 * 2. You can use this going forward instead of its predecessor
 * 
 * What does it do:
 * ****************
 * 1. Evaluates a series of
 *    1. IDataCollectionProducers
 *    2. IAspireUpdateHandlers 
 * 2. After evaluating each request it collects the key/value pairs from each request
 * 3. Updates the incoming hashtable with the key/value pairs
 * 4. Sends the updated hashtable next request
 * 5. The final request could be an update request or a query request
 * 6. For a query request it returns the collection
 * 7. For the update request it returns the RequestExecutorResponse
 * 
 * @see                        DBMultiUpdateRequestExecutor
 */
public class PreTranslateArgsMultiRequestExecutor1 implements ICreator
{

   /**
    * interface from aspire and implemented by the client
    */
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
    {
      if (args instanceof Hashtable)
      {
         return executeProcedure(requestName, (Hashtable)args);
      }
      else if (args instanceof Vector)
      {
         Vector vArgs = (Vector)args;
         return executeProcedure(requestName, (Hashtable)vArgs.elementAt(0));
      }
      else
      {
         throw new RequestExecutionException("Error: Wrong type of arguments passed : " + args.getClass().getName());
      }
    }
    
   private Object executeProcedure( String requestName, 
                                    Hashtable arguments )
         throws RequestExecutionException
   {
      try
      {
            AppObjects.trace(this,"request name : %1s",requestName );
            AppObjects.trace(this,"arguments : %1s", arguments );  
      
            // read all the translation requests      
            for(int i=1;i>0;i++)
            {
               String requestStringKey = requestName + ".request." + i;
               String requestString = AppObjects.getIConfig().getValue(requestStringKey,null);
               AppObjects.trace(this, "executing requests for: %1s:%2s",requestStringKey,requestString);
               if (requestString == null)
               {
            	  AppObjects.trace(this, "Request string not availabe for: %1s",requestStringKey);
                  break;
               }
               // execute request, time to translate
               PipelineReturn pr = this.execPipelineRequest(requestString, arguments);
               Object intObj = pr.rtnObject;
               if (pr.continueFlag == false)
               {
            	   //no need to continue
                   AppObjects.trace(this,"Translated args are : %1s", arguments );
            	   return intObj;
               }
               
               if (intObj == null)
               {
            	   AppObjects.warn(this,"Got a null object back");
            	   continue;
               }
               //this is not the last boject
               //Get the arguments from its hashtable
               if (intObj instanceof IDataCollection)
               {
            	   Hashtable translatedArgs = getHashtable(intObj);
            	   appendHashtable(arguments,translatedArgs );
               }
               //finished with that request
               //continue with the next one
            }
            AppObjects.trace(this,"Translated args are : %1s",arguments );
            
            // execute the final request
            // Check for the final request if there is on
            
            // Final request not there
            String mainRequestString = AppObjects.getIConfig().getValue(requestName + ".request",null);
            if (mainRequestString == null)
            {
                //nothing to do but translation
            	//just return a success
               return new RequestExecutorResponse(true);
            }                     
            
            //There is the final request execute and return
            Vector parms = new Vector();
            parms.addElement(arguments);
            
            return AppObjects.getIFactory().getObject(mainRequestString, parms);
       }
       catch(com.ai.data.FieldNameNotFoundException x)
       {
          throw new RequestExecutionException("Translation failed", x);
       }            
       catch(com.ai.data.DataException x)
       {
          throw new RequestExecutionException("Data exception", x);
       }            
   }
   private void appendHashtable(Hashtable primary, Hashtable secondary )
   {
      // add secondary to the primary table
      for (Enumeration e=secondary.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         String value = (String)secondary.get(key);
         primary.put(key.toLowerCase(),value);
      }
   }
   private Object executeForObject(String requestName, Hashtable arguments)
   throws RequestExecutionException
   {
	      // make vector arguments for request executions
	      Vector parms = new Vector();
	      parms.addElement(arguments);
	      Object obj = AppObjects.getIFactory().getObject(requestName, parms);
	      return obj;
   }
   
   Hashtable getHashtable(Object obj)
         throws com.ai.application.interfaces.RequestExecutionException
         ,com.ai.data.DataException
         ,com.ai.data.FieldNameNotFoundException
   {
      Hashtable outParms = new Hashtable();
      IDataCollection col = null;
      try
      {
          col = (IDataCollection)obj;
          IIterator itr = col.getIIterator();
          IMetaData meta = col.getIMetaData();
          itr.moveToFirst();

          IDataRow dataRow = null;
          if (itr.isAtTheEnd() == false)
          {
            Object curElem = itr.getCurrentElement();
            if (curElem instanceof String)
            {
               dataRow = new DataRow(meta, (String)curElem,"|");
            }
            else
            {
               dataRow = (IDataRow)curElem;
            }   
          }  
          IIterator metaItr = meta.getIterator();
          for(metaItr.moveToFirst();!metaItr.isAtTheEnd();metaItr.moveToNext())
          {
             String colName = (String)metaItr.getCurrentElement();
             String colValue = null;
             if (dataRow != null )
             {
                colValue = dataRow.getValue(colName);
             }  
             else
             {
               colValue = "";
             }              
             outParms.put(colName,colValue);
          }
          return outParms;
      }
      finally
      {
    	  col.closeCollection();
      }
   }//eof-function
   private PipelineReturn execPipelineRequest(String requestString, Hashtable arguments)
   throws RequestExecutionException
   {
       Object intObj = this.executeForObject(requestString, arguments);
       if (intObj instanceof PipelineReturn)
       {
    	   //it is of type PipelineReturn
    	   return (PipelineReturn)intObj;
       }
       else
       {
    	   //it is a regular object
    	   return new PipelineReturn(intObj,true);
       }
   }
}//eof-class
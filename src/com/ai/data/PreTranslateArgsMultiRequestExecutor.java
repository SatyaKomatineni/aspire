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
 * PreTranslateArgsMultiRequestExecutor
 * Acts as DataCollectionProducer and AspireUpdateHandler
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
public class PreTranslateArgsMultiRequestExecutor implements ICreator
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
            AppObjects.log("request name : " + requestName );
            AppObjects.log("arguments : " + arguments );  
      
            // read all the translation requests      
            for(int i=1;i>0;i++)
            {
               String requestString = AppObjects.getIConfig().getValue(requestName + ".request." + i,null);
               if (requestString == null)
               {
                  break;
               }
               // time to translate
               Hashtable translatedArgs = getHashtable(requestString,arguments);
               appendHashtable(arguments,translatedArgs );         
            }
            AppObjects.log("Translated args are : " + arguments );
            // execute the final request
            
            String mainRequestString = AppObjects.getIConfig().getValue(requestName + ".request",null);
            if (mainRequestString == null)
            {
               // nothing to do but translation
               return new RequestExecutorResponse(true);
            }                     
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
   
   Hashtable getHashtable(String requestName, Hashtable arguments )
         throws com.ai.application.interfaces.RequestExecutionException
         ,com.ai.data.DataException
         ,com.ai.data.FieldNameNotFoundException
   {
      Hashtable outParms = new Hashtable();
      // make vector arguments for request executions
      Vector parms = new Vector();
      parms.addElement(arguments);
      Object obj = AppObjects.getIFactory().getObject(requestName, parms);
      if (obj instanceof IDataCollection)
      {
          IDataCollection col = (IDataCollection)obj;
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
          col.closeCollection();
      }
      return outParms;
   }
} 
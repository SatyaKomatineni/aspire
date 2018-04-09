/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;

import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.sql.*;
import java.util.*;
import com.ai.common.*;
import com.ai.db.*;
import com.ai.data.*;
import com.ai.servlets.AspireConstants;
/**
 * Substitutes parameters in to an sql statement.
 * DBPreTranslateArgsMultiRequestExecutor
 *
 * What does it do:
 * ****************
 * 1. Evaluates a series of db requests
 * 2. After evaluating each request it collects the key/value pairs from each request
 * 3. Updates the incoming hashtable with the key/value pairs
 * 4. Sends the updated hashtable and the connection to the next db request
 * 5. The final request could be an update request or a query request
 * 6. For a query request it returns the collection
 * 7. For the update request it returns the RequestExecutorResponse
 *
 * Restrictions
 * *************
 * 1. All but the last statement needs to be a single row fetching select
 * 2. Intermediate statements could be updates but they won't affect the hash table
 *
 * Drawbacks
 * **********
 * Although it could execute single requests, it
 * When is this applicable
 * ************************
 *
 * How is this different from DBRequestExecutor1
 * **********************************************
 *
 *
 * @see                        DBRequestExecutor1
 * @see                        DBBaseJavaProcedure
 * @see                        DBMultiUpdateRequestExecutor
 * @version                    1.37, 26 Jun 1996
 */
public class DBPreTranslateArgsMultiRequestExecutor extends DBBaseJavaProcedure
{

   public Object executeProcedure(Connection con,
                                    boolean bConnectionCreator,
                                    String requestName,
                                    Hashtable arguments )
         throws DBException
   {
      try
      {
            AppObjects.trace(this,"request name : %1s",requestName );
            AppObjects.trace(this,"arguments : %1s",arguments );

            //The following is added on June 10th 2003
            if (bConnectionCreator == true)
            {
               arguments.put("aspire.reserved.jdbc_connection",con);
            }

            // read all the translation requests
            for(int i=1;i>0;i++)
            {
               String requestString = AppObjects.getIConfig().getValue(requestName + ".request." + i,null);
               if (requestString == null)
               {
                  break;
               }
               // time to translate
               Hashtable translatedArgs = getHashtable(con,requestString,arguments);
               appendHashtable(arguments,translatedArgs );
            }
            AppObjects.trace(this,"Translated args are : %1s",arguments );
            // execute the final request

            String mainRequestString = AppObjects.getIConfig().getValue(requestName + ".request",null);
            if (mainRequestString == null)
            {
               // nothing to do but translation
               return new RequestExecutorResponse(true);
            }
            Vector parms = new Vector();
            parms.addElement(arguments);
            parms.addElement(con);
            if (bConnectionCreator == true)
            {
                //If I am the owner of the connection then I can transfer it otherwise not
                //updated on 2/20/2003
                arguments.put(AspireConstants.CONNECTION_OWNERSHIP_TRANSFER_FLAG_PARAM_KEY
                         ,AspireConstants.BOOL_TRUE);
            }

            return AppObjects.getIFactory().getObject(mainRequestString, parms);
       }
       catch(com.ai.data.FieldNameNotFoundException x)
       {
          throw new DBException("Translation failed", x);
       }
       catch(com.ai.data.DataException x)
       {
          throw new DBException("Data exception", x);
       }
       catch(com.ai.application.interfaces.RequestExecutionException x)
       {
          throw new DBException("RequestExecution failed", x);
       }

   }
   Object executeRequest(String requestName, Hashtable arguments )
   {
      return null;
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

   Hashtable getHashtable(Connection con, String requestName, Hashtable arguments )
         throws com.ai.application.interfaces.RequestExecutionException
         ,com.ai.data.DataException
         ,com.ai.data.FieldNameNotFoundException
   {
      Hashtable outParms = new Hashtable();
      // make vector arguments for request executions
      Vector parms = new Vector();
      parms.addElement(arguments);
      parms.addElement(con);
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

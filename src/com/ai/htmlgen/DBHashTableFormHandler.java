/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

import com.ai.application.utils.*;
import com.ai.data.*;
import java.util.Vector;
import java.util.Hashtable;
import com.ai.application.interfaces.*;
import java.util.Enumeration;
import com.ai.common.*;

public class DBHashTableFormHandler extends AFormHandlerWithRequestControlHandlers
                                 implements ICreator
{
   private boolean m_bLookForLoopAggregates = false;

   public IIterator getKeys()
   {
      Vector v = new Vector();
      Hashtable m = getUrlArguments();
      for(Enumeration e=m.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         Object val = m.get(key.toLowerCase());
         if (val instanceof String)
         {
           v.addElement(key);
         }
      }
      return new VectorIterator(v);
   }

   public String getValue(final String key )
   {
      Object objValue = getUrlArguments().get(key.toLowerCase());
      String value = null;
      if (objValue != null)
      {
         if (objValue instanceof String)
         {
            value = (String)objValue;
         }
         else
         {
            // an object
            value = "Object";
         }
         return value;
      }
      if (value == null)
      {
         AppObjects.trace(this,"htmlgen: Value not found for key: %1s", key.toLowerCase() );
         // look in the control handlers
         value = getValueFromControlHandlers(key);
         if (value == null)
         {
            AppObjects.trace(this,"htmlgen: Retrieving value from IConfig");
            // If the key is not found go for the global variables
            value = AppObjects.getValue(key,"");
         }
      }
      return value;
   }
   private String getValueFromControlHandlers(final String key)
   {
      if (m_bLookForLoopAggregates == false)
      {
         return null;
      }
      for(Enumeration e=getControlHandlerNames();e.hasMoreElements();)
      {
         String handlerName=(String)e.nextElement();
         try
         {
            IControlHandler handler = this.getControlHandler(handlerName);
            if (handler instanceof IControlHandler2)
            {
                String value = ((IControlHandler2)handler).getAggregateValue(key);
                if (value != null)
                {
                  return value;
                }
            }
         }
         catch(ControlHandlerException x)
         {
            // something wrong with this control handler
            AppObjects.log("Error:htmlgen: Could not retrieve a control handler",x);
            continue;
         }
      }
      return null;
   }

   private Hashtable convertToLowerCase(Hashtable inHashtable)
   {
      Hashtable table = new Hashtable();
      for(Enumeration e=inHashtable.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         table.put(key.toLowerCase(),inHashtable.get(key));
      }
      return table;
   }
   private boolean getLookForLoopAggregatesFlag(final String formName)
   {
      try
      {
         String bstr = AppObjects.getIConfig().getValue("request." + formName + ".lookForLoopAggregates","false");
         return  com.ai.filters.FilterUtils.convertToBoolean(bstr);
      }
      catch(com.ai.common.UnexpectedTypeException x)
      {
         return false;
      }
   }

   public void init(final String formName, final Hashtable arguments )
      throws ControlHandlerException
   {
      try
      {
        super.init(formName, arguments);

        m_bLookForLoopAggregates = getLookForLoopAggregatesFlag(formName);
        m_formName = formName;
        // Execute the main data request for this form
        // Make an argument vector or hashtable
        AppObjects.trace(this,"Looking for main data request for :%1s",formName);
        String mdrClassname = AppObjects.getValue("request." + formName + ".mainDataRequest.classname",null);
        if (mdrClassname != null)
        {
           Object obj = AppObjects.getIFactory().getObject(formName + ".mainDataRequest",arguments);
           if (obj instanceof IDataCollection)
           {
               this.workWithCollection((IDataCollection)obj);
           }
        }
        //Conditionally preload control handlers
        this.preLoadControlHandlers();
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("Warn:htmlgen: Could not execute the database request for Main data request",x);
      }
      catch(com.ai.data.DataException x)
      {
        AppObjects.log("Warn: Exception in main data request",x);
      }
   }//eof-function
   private void workWithCollection(IDataCollection collection)
         throws com.ai.data.DataException
   {
      try
      {
         AppObjects.log("cp: Maindata collection retrieved");
         IIterator itr = collection.getIIterator();
         IMetaData meta = collection.getIMetaData();
         itr.moveToFirst();

         IDataRow dataRow = null;
         if (itr.isAtTheEnd() == false)
         {
           Object curElemObj = itr.getCurrentElement();
           if (curElemObj instanceof String)
           {
              dataRow = new DataRow(meta, (String)curElemObj,"|");
           }
           else
           {
              dataRow = (IDataRow)curElemObj;
           }
         }
         IIterator metaItr = meta.getIterator();
         for(metaItr.moveToFirst();!metaItr.isAtTheEnd();metaItr.moveToNext())
         {
            String colName = (String)metaItr.getCurrentElement();
            String colValue = null;
            if (dataRow != null )
            {
               try { colValue = dataRow.getValue(colName); }
               catch(com.ai.data.FieldNameNotFoundException x)
               {
                 AppObjects.warn(this,"fieldname not found for : %1s",colName );
                 colValue = "";
               }
            }
            else
            {
              colValue = "";
            }
            getUrlArguments().put(colName.toLowerCase(),colValue);
         }
      }
      finally
      {
            AppObjects.log("cp: Closing the main data request collection");
            collection.closeCollection();
      }

   }//eof-function
}//eof-class

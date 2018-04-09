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
/**
 * Not sure if this class is being used
 */
public class GenericFormHandler extends AFormHandlerWithRequestControlHandlers 
                                 implements ICreator
{
   // Required data to satisfy the getValue command
  private IDataRow m_dataRow = null;
  private IMetaData m_metaData = null;
  
  
   public IIterator getKeys()
   {
      if (m_metaData != null)
      {
         return m_metaData.getIterator();
      }
      else 
         return null;
   }
   
   private String getValueFromUrlArguments(final String key )
   {
      if (getUrlArguments() == null)
      {
         return null;
      }
      return (String)(getUrlArguments().get(key));
   }  
   private String getValueFromDataRow(final String key)
   {
      try {
         return m_dataRow.getValue(key);
      }
      catch(com.ai.data.FieldNameNotFoundException x)
      {
         return null;
      }
   }
   public String getValue(final String key )
   {
          String value = getValueFromDataRow(key);
          if (value != null)
          {
            return value;
          }
          value = getValueFromUrlArguments(key);
          if (value != null) return value;
          return "Value not found for key: " + key ;
   }
   
   public String getFormName()
   {
      return m_formName;
   }
   public Object executeRequest(String requestName, Object args )
                throws RequestExecutionException
   {
      try
      {
           Vector vArgs = (Vector)args;
           String formName = (String)vArgs.elementAt(0);
           Hashtable urlArguments = null;
           if (vArgs.size() > 1)
           {
              urlArguments = (Hashtable)(vArgs.elementAt(1));
           }              
           init(formName,urlArguments );
           return this;
      }
      catch(com.ai.htmlgen.ControlHandlerException x)
      {
         throw new RequestExecutionException("Could not get control handlers",x);
      }
                 
   }
   public void init(String formName, Hashtable arguments ) throws ControlHandlerException
   {
      try 
      {
         super.init(formName, arguments);
        m_formName = formName;
        // Execute the main data request for this form
        // Make an argument vector or hashtable
        AppObjects.trace(this,"arguments to the mainrequest are: %1s",arguments ); 
        Object obj = AppObjects.getIFactory().getObject(formName + ".mainDataRequest",arguments);
        IDataCollection collection = (IDataCollection)obj;
        IIterator itr = collection.getIIterator();
        itr.moveToFirst();
  //      String row = (String)itr.getCurrentElement();
        m_metaData = collection.getIMetaData();
        m_dataRow = (IDataRow)itr.getCurrentElement();
        collection.closeCollection();                                
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("Could not execute the database request for Main data request");
         AppObjects.log(x);
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log(x);
      }
   }
}   
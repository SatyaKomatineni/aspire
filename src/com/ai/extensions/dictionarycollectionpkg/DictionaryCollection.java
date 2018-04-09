/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.extensions.dictionarycollectionpkg;

import com.ai.data.*;
import com.ai.common.*;
import java.util.*;
import com.ai.application.utils.*;

/**
 * Take an IDictionary and make it look like an IDataCollection with one row
 */
public class DictionaryCollection implements IDataCollection, IIterator
{
   private IDictionary        m_dictionary =null;
   private boolean            m_bAtTheEnd = false;
   private DictionaryDataRow  m_dr = null;
   private IMetaData          m_metaData = null;
   
   public DictionaryCollection(IDictionary dictionary) 
   {      
      m_dictionary = dictionary;
      m_dr = new DictionaryDataRow(dictionary,this);
      m_metaData = createMetaData(dictionary);
   }

   private IMetaData createMetaData(IDictionary d)
   {
      Vector v = new Vector();
      d.getKeys(v);
      return new VectorMetaData(v);
   }

//Methods from IDataCollection
        public  IMetaData getIMetaData()
               throws DataException
         {
            return m_metaData;
         }               
        public IIterator getIIterator()
               throws DataException {return this;}
               
        public void closeCollection()
            throws DataException{}
            

//Methods from IIterator   
        public void moveToFirst()
                  throws DataException
        {
            m_bAtTheEnd = false;
        }                  
        public void moveToNext()
                  throws DataException
        {
            m_bAtTheEnd = true;
        }
        public boolean isAtTheEnd()
                  throws DataException
        {
            return m_bAtTheEnd;
        }
        public Object getCurrentElement()
                  throws DataException
         {
            return this.m_dr;
         }                  
}    

class DictionaryDataRow implements IDataRow
{
   IDictionary m_dictionary = null;
   DictionaryCollection m_dc = null;
   public DictionaryDataRow(IDictionary dict, DictionaryCollection dc)
   {
      m_dictionary = dict;
      m_dc = dc;
   }
   /**
    * Return a string value for the specified index
    * The index is zero based
    */
  public String getValue(int inIndex)
  {
      try
      {
      IIterator itr = getColumnNamesIterator();
      int i=0;
      for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
      {
         if (i == inIndex)
         {
            String colName = (String)itr.getCurrentElement();
            return getValue(colName);
         }
         i++;
      }
      return null;
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log("Error: Could not get value for a field with index:" + inIndex,x);
         return null;
      }
      catch(com.ai.data.FieldNameNotFoundException x)
      {
         AppObjects.log("Error: Colname found, but could not get value for a field with index:" + inIndex,x);
         return null;
      }
      
  }

   /**
    * Return a string value for the specified column name 
    * If the name not found return the default value
    * It is recommended that the columnname is case insensitive
    * Internally the column name is converted to an index
    */
  public String getValue(String columnName, String defaultValue)
  {
      String obj = (String)m_dictionary.get(columnName);
      if (obj == null) return defaultValue;
      return obj;
  }
  
   /**
    * Return a string value for the specified column name
    * Column name is case insensitive (recommended) 
    */
  public String getValue(String columnName) 
         throws com.ai.data.FieldNameNotFoundException
   {
      return (String)m_dictionary.get(columnName);
   }         
  
   /**
    * Return column names
    * See the IIterator interface to understand how to access
    * these column names
    */
  public IIterator getColumnNamesIterator()
  {
      try
      {
      return m_dc.getIMetaData().getIterator();          
      }
      catch(com.ai.data.DataException x)
      {
           AppObjects.log("Error: Could not obtain metada for DictionaryCollection",x);
           return null;
      }
  }
}



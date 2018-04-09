/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.data;
import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;

/**
 * 7/10/2002/Satya
 * Converts an IDataCollection to IDataCollection1
 * perhaps should be deprecated, because we are no longer recommending IDataCollection1
 * @deprecated
 *
 */
                            
public class Collection1DataFilter implements IDataCollection1, ICreator
{
   private IDataCollection m_col;
   public Collection1DataFilter() 
   {
   }
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      // The argument is an IDataCollection object
      m_col = (IDataCollection)args;
      return this;
   }   
   public  IMetaData getIMetaData()
         throws DataException
   {
      return m_col.getIMetaData();  
   }         
   public IIterator getIIterator()
         throws DataException
   {
      return m_col.getIIterator();
   }         
   public void closeCollection()
      throws DataException
   {
      m_col.closeCollection();
   }      
   public IIterator getDataRowIterator()
      throws DataException
   {
      return new DataRowIterator(m_col.getIMetaData(),m_col.getIIterator());
   }
   class DataRowIterator implements IIterator
   {
      IIterator m_itr = null;
      IMetaData m_metaData = null;
      DataRowIterator(IMetaData inMetaData, IIterator inItr)
      {
         m_itr = inItr;
         m_metaData = inMetaData;
      }
      
        public void moveToFirst() throws DataException
        {
            m_itr.moveToFirst();
        }
        public void moveToNext() throws DataException
        {
          m_itr.moveToNext();
        }
        public boolean isAtTheEnd()
                  throws DataException
        {
         return m_itr.isAtTheEnd();
        }                  
        public Object getCurrentElement()
                  throws DataException
        {
            String row = (String)m_itr.getCurrentElement();
            DataRow dataRow = new DataRow(m_metaData, row,"|");
            return dataRow;
        }                  
   }      
} 


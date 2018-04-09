/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.data;

/**
 * 
 * Most likely to be deprecated
 * This class is introduced to provide type safety for collection elements
 *
 * Questions
 *************
 * 1. Why is this class not inheriting IDataRowIterator
 * 2. Can we deprecate this class
 *
 * @see IDataRowIterator
 * @see IDataCollection1
 *
 */
public class DataRowIterator implements IIterator
{
   private IIterator m_itr;
   private IMetaData m_metaData;
   
   public DataRowIterator(IIterator inItr, IMetaData inMetaData)
   {
      m_itr = inItr; 
      m_metaData = inMetaData;
   }
   public void moveToFirst()
            throws DataException{ m_itr.moveToFirst(); }
   public void moveToNext()
            throws DataException{ m_itr.moveToNext(); }
   public boolean isAtTheEnd()
            throws DataException { return m_itr.isAtTheEnd(); }
   public Object getCurrentElement()
            throws DataException
   {
      Object obj = m_itr.getCurrentElement();
      if (obj instanceof IDataRow) return obj;
      if (obj instanceof String)
      {
         return new DataRow(m_metaData, (String)obj,"|" );
      }
      throw new DataException("Error: Unknown Element type in collection");
   }            
}    



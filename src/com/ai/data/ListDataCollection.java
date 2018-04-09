/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;
import java.util.*;

import com.ai.common.IteratorConverter;

/**
 * Represents a list of IDataRows
 * The iterator returns IDataRows
 */
public class ListDataCollection extends ADataCollection implements IDataCollection1
{
        List m_rowList = new ArrayList();
        Vector m_MetaFieldsVector = new Vector();
        public ListDataCollection( Vector metaData) 
        {
                m_MetaFieldsVector = metaData;
        }
        public  IMetaData getIMetaData()
        {
           return new VectorMetaData(m_MetaFieldsVector); 
        }
        public IIterator getIIterator()
        {
                return new IteratorConverter(m_rowList.iterator());
        }
        
        public IIterator getDataRowIterator()
            throws DataException
      {
         return getIIterator();
      }
      public void addDataRow(IDataRow row)
      {
      	m_rowList.add(row);
      }
      public List getInternalList()
      {
      	return m_rowList;
      }
}                             


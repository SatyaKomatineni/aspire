/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;
import java.util.*;
import com.ai.common.Tokenizer;
import com.ai.application.utils.*;

/**
 * Represents a vector based data collection
 * The iterator returns IDataRows
 */
public class VectorDataCollection extends ADataCollection implements IDataCollection1
{
        Vector m_InputVector;
        Vector m_MetaFieldsVector = new Vector();
        public VectorDataCollection( Vector metaData, Vector rows) 
                throws InvalidVectorDataCollection
        {
                if (rows.size() == 0)
                {
                        AppObjects.log("Warn: Empty rows in this vector");
                }
                // Assume the first row of the vector
                // are the column names
                m_InputVector = rows;
                m_MetaFieldsVector = metaData;
        }
        public  IMetaData getIMetaData()
        {
           return new VectorMetaData(m_MetaFieldsVector); 
        }
        public IIterator getIIterator()
        {
                return new VectorDataRowIterator(m_InputVector,getIMetaData());
        }
        
        public IIterator getDataRowIterator()
            throws DataException
      {
         return getIIterator();
      }            
}                             


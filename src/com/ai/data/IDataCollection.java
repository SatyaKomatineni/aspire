/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;
/**
 * Most basic interface for representing collections of data
 * 
 * Related interfaces
 *
 *    IDataCollection1
 *    IDataRowCollection
 *    IMetaData
 * 
 *    IIterator
 *    IDataRowIterator
 *
 *    IDataRow
 *
 * Deprecated interfaces
 *    IDataCollection1
 *    IDataRowCollection
 *    IDataRowIterator
 *    
 * Recommended interfaces
 *    IDataCollection
 *    IIterator
 *    IMetaData
 *
 * Comments   
 *    Most likely there is a need for an IBaseDataCollection
 *    A base data collection will not need to have any meta data in it
 *
 * Using recommended interfaces
 *    1. An IIterator can give you objects of any type
 *    2. When used in an aspire context these can be one of the  following
 *          1. String, separated by |
 *          2. or an IDataRow
 *    3. Aspire must promise to deal with both sorts of iterators
 *    4. Going forward IDataRow should be preferred
 *
 * And here is the sample code of using this
 *
 *  IDataCollection col;
 *  IIterator itr = col.getIIterator();
 *     for(itr.moveToFirst()
 *        ;itr.isAtTheEnd()
 *        ;itr.moveToNext())
 *     {
 *        object o = itr.currentElement();
 *         IDataRow curRow = null;
 *         if (o instanceof IDataRow)
 *         {
 *           curRow = (IDataRow)o;
 *         }
 *         else
 *         {
 *           // assume it is a string
 *           curRow  = new DataRow((String)row..);
 *         }
 *         
 *        String col1_value = curRow.getValue("col1");
 *        String col2_value = curRow.getValue("col2");
 *     }
 * ...
 * finally {
 *  col.closeCollection();
 * }
 *
 * Documents
 * ************
 *    See users guide for the topic
 *       How to implement an IDataCollection Producer
 */

public interface IDataCollection 
{
        public  IMetaData getIMetaData()
               throws DataException;
        public IIterator getIIterator()
               throws DataException;
        public void closeCollection()
            throws DataException;
} 

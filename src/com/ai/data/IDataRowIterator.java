/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.data;

/*
 * Workaround for fixing the notion that IIterator 
 * is an iterator of strings.
 *
 * Replaces with a notion that IIterator is actually
 * an iterator of IDataRows.
 *
 * This is important to the html generation capabilities
 * of Aspire.
 *
 * For non-html related activities one should try to use
 * IIterator as the primary interface and have it return
 * the necessary downcasted object.
 *
 * For Aspire the implementation of this iterator could
 * support the notion both an iterator of strings and also
 * an iterator of IDataRows. This keeps the backward compatiablity
 * intact.
 *
 * At the time of this implementation there are two classes
 * that returns collections whose iterators are IDataRowIterators.
 * 
 * These are 
 *
 * DBRequestExecutor2
 * StoredProcedureExecutor2
 *
 * And here is the sample code of using this new scheme
 *
 *  IDataCollection col;
 *  IIterator itr = col.getIIterator();
 *  if (itr instanceof IDataRowIterator)
 *  {
 *     IDataRowIterator idataRowItr = (IDataRowIterator)itr;
 *     for(idataRowItr.moveToFirst()
 *        ;idataRowItr.isAtTheEnd()
 *        ;idataRowItr.moveToNext())
 *     {
 *        IDataRow curRow = idataRowItr.currentElement();
 *        String col1_value = curRow.getValue("col1");
 *        String col2_value = curRow.getValue("col2");
 *     }
 *  }
 *  else
 *  {
 *     //This only supports IIterator
 *     //tough luck
 *  }
 *  col.closeCollection();
 *
 * Related interfaces
 **********************
 * @see IDataRow
 * @see IIterator
 *
 * Related implementations
 **************************
 * @see RSDataRow
 * @see RSDataRowIterator
 */
public interface IDataRowIterator extends IIterator 
{
        public IDataRow getCurrentDataRow()
                  throws DataException;
} 
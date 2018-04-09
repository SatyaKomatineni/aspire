/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.data;
/*
public interface IDataCollection 
{
        public  IMetaData getIMetaData()
               throws DataException;
        public IIterator getIIterator()
               throws DataException;
        public void closeCollection()
            throws DataException;
} 
*/
public interface IDataCollection1  extends IDataCollection
{
        public IIterator getDataRowIterator()
            throws DataException;
} 

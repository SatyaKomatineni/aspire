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
/**
 * Introduced to support better access to underlying data structures
 */
public interface IDataRowCollection  extends IDataCollection
{
        public IIterator getIDataRowIterator()
            throws DataException;
} 


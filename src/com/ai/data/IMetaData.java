/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;

public interface IMetaData 
{
        public IIterator getIterator();
        public int   getColumnCount();
        public int getIndex(final String attributeName)
                throws FieldNameNotFoundException;
} 
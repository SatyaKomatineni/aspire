/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;

public interface IIterator {
        public void moveToFirst()
                  throws DataException;
        public void moveToNext()
                  throws DataException;
        public boolean isAtTheEnd()
                  throws DataException;
        public Object getCurrentElement()
                  throws DataException;
}                                
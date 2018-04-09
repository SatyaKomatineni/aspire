/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

import com.ai.data.*;

public interface ILoopForwardIterator
{
   /**
    * @deprecated use moveToFirst, moveToNext, isAtTheEnd functionality
    * return true if successful
    * return false if there are no more rows
    *
    * It is wrong to call gotoNextRow() without calling moveToFirstRow()
    * The above line doesnt sound right. You should be able to call
    *
    * Another assumption is you position at row when you start the loop.
    * Not sure if this is the convention
    *
    * This interface is untenable as you cant tell if there are any rows

    */
   public boolean gotoNextRow();
   /**
    * getValue from the current row matching the key
    */
   public String getValue(final String key);

   public void moveToFirst()
             throws DataException;

   public void moveToNext()
             throws DataException;

   public boolean isAtTheEnd()
                  throws DataException;
}

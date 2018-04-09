/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;


/*
public interface ILoopForwardIterator 
{
   // * return true if successful
   // * return false if there are no more rows
   public boolean gotoNextRow();
   public String getValue(final String key);

} 
*/

public interface ILoopRandomIterator extends ILoopForwardIterator
{
   public boolean moveToFirstRow();
   public boolean moveToRow(int rownum);
   public int getNumberOfRows();
   public boolean isRowAvailable();
} 

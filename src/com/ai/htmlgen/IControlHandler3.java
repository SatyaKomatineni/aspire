/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

/**
 * Adds an additional method to calculate totals etc.
 *
 * Baseclass methods
 *********************
 * public interface IControlHandler 
 * {
 *    public boolean eliminateLoop();
 *    public boolean isDataAvailable();
 *    public String getValue(final String key, int turn);
 *    public boolean getContinueFlag();
 *    public void formProcessingComplete();
 * }  
 * public interface IControlHandler1 extends IControlHandler
 * {
 *    //Could be called only if the iterator is at the end
 *    //in most of the cases.
 *    public int getNumberOfRowsRetrieved();
 *    public IDataCollection getDataCollection();
 * } 
 *
 * public interface ILoopForwardIterator 
 * {
 *    // return true if successful
 *    // return false if there are no more rows
 *    // initially points to the first row -1
 *    // One has to call gotoNextRow() to move to first row
 *    // otherwise getValue() will throw invalid state runtime exception
 *    public boolean gotoNextRow();                                                              
 *
 *    // return a value for a given key from the current row
 *    public getValue(final String key);
 *
 *  }  
 **/
public interface IControlHandler3 extends IControlHandler2, ILoopForwardIterator 
{

} 

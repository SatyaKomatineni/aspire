/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import com.ai.data.IDataCollection;
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
 */
public interface IControlHandler2 extends IControlHandler1 
{
   /**
    * Return the calculated value for a given key
    */
   public String getAggregateValue(final String key);
} 

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import com.ai.data.IDataCollection;
/**
 * Adds an extra method to IControlHandler.
 * Retrieves the number of rows retrieved
 * Also retrieves the underlying IDataCollection object
 *
 *<pre>
 * Derived class methods
 ************************
 * public interface IControlHandler 
 * {
 *    public boolean eliminateLoop();
 *    public boolean isDataAvailable();
 *    public String getValue(final String key, int turn);
 *    public boolean getContinueFlag();
 *    public void formProcessingComplete();
 * }  
 *
 * Methods of one of the useful classes
 *****************************************
 * public interface IDataCollection 
 * {
 *         public  IMetaData getIMetaData()
 *                throws DataException;
 *         public IIterator getIIterator()
 *                throws DataException;
 *         public void closeCollection()
 *             throws DataException;
 * } 
 *</pre>
*/
public interface IControlHandler1 extends IControlHandler
{
   /**
    * Retrieves the number of rows retrieved 
    * Could be called only if the iterator is at the end
    */
   public int getNumberOfRowsRetrieved();
   /**
    * Retrieves the underlying IDataCollection.
    */
   public IDataCollection getDataCollection();
} 

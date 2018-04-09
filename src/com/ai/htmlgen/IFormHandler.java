/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.htmlgen;
import com.ai.data.IIterator;

/**
 * Responsibliel for providing the data requirements of an html page.
 */
public interface IFormHandler {
   /**
    * return a value for a given key
    * returns an empty string if there is no value
    */
   public String getValue(final String key);
   /**
    * returns a list of keys
    */
   public IIterator getKeys();
   /**
    * returns an object that is responsible for loop data.
    * throws an exception if the loop handler is not found
    */
   public IControlHandler getControlHandler(final String name )
        throws ControlHandlerException;
   /**
    * indicates that the data is no longer required by the page.
    * Internal resources could be closed.
    */
   public void formProcessingComplete();        
   /**
    * returns false if there is no data in the form.
    */
   public boolean isDataAvailable();     
}

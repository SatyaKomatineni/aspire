/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.htmlgen;
/**
 * responsible for data in an html page that is inside a loop tag
 * @see IFormHandler
 * At this time there is a lot of awkwardness to this class.
 * At some point in the future this is going to eliminated.
 */
public interface IControlHandler 
{
   /**
    * returns true if the page needs to eliminate the loop 
    * when no data is present.
    */
   public boolean eliminateLoop();
   /**
    * returns true if data is available.
    */
   public boolean isDataAvailable();
   /**
    * returns a string value for a given key and the given row.
    * returns "No data found" if the key is not found
    * variable "turn" moves only forward.
    * 
    * Use ILoopForwarditerator instead
    */
   @Deprecated 
   public String getValue(final String key, int turn);
   /**
    * returns true if there are more rows
    * Use loopforward iterators to iterate
    */
   @Deprecated
   public boolean getContinueFlag();
   /**
    * Indicates to the control handler to close internal resources.
    */
   public void formProcessingComplete();
} 
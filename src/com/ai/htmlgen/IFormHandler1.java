/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Adds an enumeration method for obtaining loop handlers for
 * this form handler.
 *
 * Enumeration would be empty if there are no form handlers.
 *
 * Base class interface: IFormHandler's interface
 ***************************************************
 *  public String getValue(final String key);
 *  public IIterator getKeys();
 *  public IControlHandler getControlHandler(final String name )
 *       throws ControlHandlerException;
 *  public void formProcessingComplete();        
 *  public boolean isDataAvailable();
 */
public interface IFormHandler1 extends IFormHandler 
{
   /**
    * return an Enumeration of loop handler objects
    */
   public Enumeration getControlHandlerNames();
} 

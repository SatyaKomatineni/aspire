/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import java.util.Hashtable;
import java.util.Enumeration;
import com.ai.common.*;
import com.ai.data.*;

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
 *  public interface IFormHandler1 extends IFormHandler 
 *  {
 *      * return an Enumeration of loop handler objects
 *     public Enumeration getControlHandlerNames();
 *  } 
 */
public class UpdateFormHandlerAdapter implements IFormHandler1
{
   private IDictionary m_userSuppliedData;
   private IFormHandler1 m_formHandler;
   
   public UpdateFormHandlerAdapter(IDictionary userData
                                   ,IFormHandler1 formHandler) 
   {
      m_userSuppliedData = userData;
      m_formHandler = formHandler;
   }
   public String getValue(final String key)
   {
      String value = (String)m_userSuppliedData.get(key);
      if (value != null) return "";
      return m_formHandler.getValue(key);
   }
   public IIterator getKeys(){ return m_formHandler.getKeys(); }
   public IControlHandler getControlHandler(final String name )
        throws ControlHandlerException
   {
      return m_formHandler.getControlHandler(name);
   }        
   public void formProcessingComplete(){ m_formHandler.formProcessingComplete(); }
   public boolean isDataAvailable(){ return m_formHandler.isDataAvailable(); }
   // From IFormHandler1
   public Enumeration getControlHandlerNames()
   {
      return m_formHandler.getControlHandlerNames();
   } 
   
} 

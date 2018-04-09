/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.utils;
import com.ai.htmlgen.*;
import java.util.*;
import com.ai.data.*;
import com.ai.application.utils.*;

/**
 * Usage
 * FieldScrapper fs = new FieldScrapper();
 * AITransform3 transform = new AITransform3(fs)
 * transform.transform(htmlFilename,PrintWriter,fs);
 * PrintWriter.close()
 * 
 * Main data keys = fs.getKeysAsEnumeration();
 * Get loop names = fs.getControlHandlerNames();
 * Get keys for a loop = fs.getLoopKeysFor(loopname);
 *
 */
public class FieldScrapper implements IFormHandler1, IBooleanExpressionEvaluator 
{

   //Keep main data keys here
   Vector m_keys = new Vector();
   
   // <loopname,keysVector>
   Hashtable m_loopVectors = new Hashtable();

   //Default value to be returned for getKeyValues
   private String m_value="ha-ha";

   // Dummy loop handler to collect data
   private LoopHandler m_loopHandler = new LoopHandler();
   

   //Default constructor that does nothing
   public FieldScrapper() 
   {
   }

   /**
    * return a value for a given key
    * returns an empty string if there is no value
    */
   public String getValue(final String key)
   {
      m_keys.addElement(key);
      return m_value;
   }
   /**
    * returns a list of keys
    */
   public IIterator getKeys()
   {
      return new VectorIterator(m_keys);
   }
   /**
    * returns an object that is responsible for loop data.
    * throws an exception if the loop handler is not found
    */
   public IControlHandler getControlHandler(final String name )
        throws ControlHandlerException
   {
      m_loopVectors.put(name,new Vector());
      m_loopHandler.initializeLoopHandler(name);
      return m_loopHandler;
   }
   /**
    * indicates that the data is no longer required by the page.
    * Internal resources could be closed.
    */
   public void formProcessingComplete(){}
   /**
    * returns false if there is no data in the form.
    */
   public boolean isDataAvailable(){return true;}

//From IFormHandler1   
   /**
    * return an Enumeration of loop handler objects
    */
   public Enumeration getControlHandlerNames()
   {
      return m_loopVectors.keys();
   }
   public Vector getLoopKeysFor(String loopName)
   {
      return (Vector)m_loopVectors.get(loopName);
   }

   //Private functions
   private void setKeyForLoop(String loopName, Object key)
   {
      Vector curLoopKeysVector = (Vector)m_loopVectors.get(loopName);
      if (curLoopKeysVector == null)
      {
         AppObjects.log("Error: Loop not found");
         return;
      }
      curLoopKeysVector.addElement(key);
      
   }
   /****************************************************************************
    * Implementing the evaluator
    ****************************************************************************
    */
   public boolean evaluate(final String expression
           ,IFormHandler pageData
           ,IControlHandler loopData
           ,int curTurn )
   {
      AppObjects.info("info: Evaluating expression: %1s",expression);
      return true;
   }           
    
   /****************************************************************************
    * Class to emulate a loop
    ****************************************************************************
    */
   class LoopHandler implements IControlHandler
   {
      private String m_loopName = "Main";
      int curTurn = 1;

      void initializeLoopHandler(String inLoopName)
      {
         setLoopName(inLoopName);
         
      }
      private void setLoopName(String inLoopName)
      {
         m_loopName = inLoopName;
      }      
      /**
       * returns true if the page needs to eliminate the loop 
       * when no data is present.
       */
      public boolean eliminateLoop(){ return false; }
      /**
       * returns true if data is available.
       */
      public boolean isDataAvailable()
      {
         return true;
      }
      /**
       * returns a string value for a given key and the given row.
       * returns "No data found" if the key is not found
       * variable "turn" moves only forward.
       */
      public String getValue(final String key, int turn)
      {
         setKeyForLoop(m_loopName,key);
         curTurn = turn;
         return m_value;
      }
      /**
       * returns true if there are more rows
       */
      public boolean getContinueFlag()
      {
         if (curTurn > 1) return false;
         return true;
      }
      /**
       * Indicates to the control handler to close internal resources.
       */
      public void formProcessingComplete()
      {
         return;
      }
   } // ENd of inner class
   
} 
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
 * JSPGenTagListener jspGen = new JSPGenTagListener();
 * AITransform3 transform = new AITransformWithTranslator(jspGen,jspGen)
 *
 * PrintWriter jspWrite = new PrintWriter( "jspfilename");
 * transform.transform(htmlFilename,jspWriter,jspGen);
 * PrintWriter.close();
 * jspWriter.close();
 *
 * // Additional benifits
 * 
 * Main data keys = fs.getKeysAsEnumeration();
 * Get loop names = fs.getControlHandlerNames();
 * Get keys for a loop = fs.getLoopKeysFor(loopname);
 *
 */
public class JSPGenTagListener implements IFormHandler1
                        , IBooleanExpressionEvaluator 
                        , ITagListener
{

   //Keep main data keys here
   Vector m_keys = new Vector();
   
   // <loopname,keysVector>
   Hashtable m_loopVectors = new Hashtable();

   // <loopname,loopname>
   Hashtable m_writtenLoops = new Hashtable();

   //Default value to be returned for getKeyValues
   private String m_value="ha-ha";

   // Dummy loop handler to collect data
   private LoopHandler m_loopHandler = new LoopHandler();
   

   /**
    * return a value for a given key
    * returns an empty string if there is no value
    */
   public String getValue(final String key)
   {  
      // Remember the key for future reference
      m_keys.addElement(key);
      // pass the JSP equivalent
      StringBuffer  stBuf = new StringBuffer();
      stBuf.append("<%= pageData.getValue(\"" + key + "\") %>");
      return stBuf.toString();
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
      AppObjects.info(this,"Evaluating expression: %1s", expression);
      return true;
   }           
   /****************************************************************************
    * Implementing the tag listener
    ****************************************************************************
    */
   public String startPage()
   {
      StringBuffer stPage = new StringBuffer();

      //imports
      stPage.append("\n<%@ page import=\"com.ai.htmlgen.*\" %>");
      stPage.append("\n<%@ page import=\"com.ai.application.utils.*\" %>");
                               
      //Getting access to the page data object
      stPage.append("\n<% ");
      stPage.append(
         "\nIFormHandler pageData = (IFormHandler)request.getAttribute(\"Aspire.formHandler\");" );

      // jsp code to validate the data object         
      stPage.append("\nif (pageData == null)");
      stPage.append("\n{");
      stPage.append("\n   out.println(\"<html><head></head><body><h2 class=error>No data available for this screen</h2></body></html>\");");
      stPage.append("\n   return;      ");
      stPage.append("\n}         ");
      
      stPage.append("\n%>\n");
      return stPage.toString();
   }
   
   public String endPage()
   {
      return "";
   }
   public String tagDetected(ITag tag, int curState)
   {
      if (tag.getTagName().equals(ITag.TAGS_BGN_LOOP_TAG))
      {
         String loopName = tag.getDefaultAttributeValue();
         if (m_writtenLoops.get(loopName) == null)
         {// loopName not written yet
            m_writtenLoops.put(loopName,loopName);
            return processBgnLoop(tag.getDefaultAttributeValue());
         }
         else
         {
            // already written
            return "";
         }
      }
      else if (tag.getTagName().equals(ITag.TAGS_END_LOOP_TAG))
      {
         return processEndLoop(tag.getDefaultAttributeValue());
      }
      else if (tag.getTagName().equals(ITag.TAGS_BGN_IF_TAG))
      {
         return processBgnIf(tag.getDefaultAttributeValue());
      }
      else if (tag.getTagName().equals(ITag.TAGS_END_IF_TAG))
      {
         return processEndIf(tag.getDefaultAttributeValue());
      }
      else if (tag.getTagName().equals(ITag.TAGS_REPLACE_TAG))
      {
         return "";
      }
      else if (tag.getTagName().equals(ITag.TAGS_REPLACE_END_TAG))
      {
         return "";
      }
      else
      {  
         return "unknown tag";
      }
      
      
   }

   String processBgnLoop(String loopName)
   {
      StringBuffer  stBuf = new StringBuffer();
      String curLoopData = loopName + "Data";
      stBuf.append("\n<% ");
      stBuf.append("\nIControlHandler3 " + curLoopData + " = (IControlHandler3)pageData.getControlHandler(\"" + loopName + "\");");
      stBuf.append("\nif (" + curLoopData + ".isDataAvailable() == false)");
      stBuf.append("\n{");
      stBuf.append("\n   AppObjects.log(\"Warn: No data found for loop handler  " + loopName + " \");");
      stBuf.append("\n}");
      stBuf.append("\nif (" + curLoopData + ".isDataAvailable() == true)");
      stBuf.append("\n{");
      stBuf.append("\n    while(" + curLoopData + ".gotoNextRow() == true)");
      stBuf.append("\n    {");
      stBuf.append("\n%>");
      return stBuf.toString();
   }
    
   String processEndLoop(String loopName)
   {
      
      StringBuffer  stBuf = new StringBuffer();
      stBuf.append("\n<% ");
      stBuf.append("\n     } // end of while");
      stBuf.append("\n  } // end of if ");
      stBuf.append("\n%>");
      return stBuf.toString();
   }
   String processBgnIf(String cond)
   {
      Vector keyValue = com.ai.common.Tokenizer.tokenize(cond,"=");
      String key= (String)keyValue.elementAt(0);
      String value=(String)keyValue.elementAt(1);

      // jsp code
      StringBuffer  stBuf = new StringBuffer();
      stBuf.append("\n<% if (pageData.getValue(\"" + key +"\").equals(\"" + value + "\"))");
      stBuf.append("\n{");
      stBuf.append("\n%>");
      
      return stBuf.toString();
      
   }
    
   String processEndIf(String cond)
   {
      StringBuffer  stBuf = new StringBuffer();
      stBuf.append("\n<% }");

      stBuf.append("\n%>");
      return stBuf.toString();
      //  
      // } // end of if
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

            
         StringBuffer  stBuf = new StringBuffer();
         stBuf.append("<%= " + m_loopName + "Data.getValue(\"" + key + "\") %>");
         return stBuf.toString();
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
/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/default/CLog.java

package com.ai.application.defaultpkg;

import com.ai.application.interfaces.*;
import com.ai.application.interfaces.ICreator;
import java.io.*;
import com.ai.application.utils.AppObjects;
import java.io.BufferedOutputStream;
import java.util.*;
import com.ai.common.AICalendar;
import com.ai.common.Tokenizer;

public class CLog implements ILog, ICreator
{
   private  PrintWriter    m_out = null;
   private  Hashtable      m_msgToIntTable = new Hashtable();
   private  int            m_msgLevel = 1;
   private  boolean        m_bFlush = true;
   private  AICalendar     m_calendar = AICalendar.getInstance();
   private  Vector         m_selectiveFilterStrings = new Vector();
   private  Vector         m_excludeFilterStrings = new Vector();
   private  boolean        m_bTraceEnabled = false;

    CLog()
    {
      m_out = new PrintWriter(System.out);
    }

    CLog(IConfig config)
    {
      try
      {
         // read the log filename
         String filename = config.getValue("Logging.logfile");
         System.out.println("Writing to log file : " + filename);
         m_out = new PrintWriter( new FileOutputStream(filename));

         // read the message level
         String msgLevel = config.getValue("Logging.msgLevel","1");
         m_msgLevel = Integer.parseInt(msgLevel);

         // read the flush option
         String flushFlag = config.getValue("Logging.flush","no").toLowerCase();
         if (flushFlag.equals("yes"))
         {
            m_bFlush = true;
         }
         // read the trace option
         String traceEnabled = config.getValue("Logging.trace","no").toLowerCase();
         if (traceEnabled.equals("yes"))
         {
            m_bTraceEnabled = true;
         }
         // read the selective filter strings
         String selectiveFilter = config.getValue("Logging.selectiveFilters",null);
         if (selectiveFilter != null)
         {
            System.out.println("Selective filter strings : " + selectiveFilter );
            // gather the selective filter strings
            Tokenizer.tokenizeInto(selectiveFilter,",",m_selectiveFilterStrings);
            System.out.println("After parsing : " + m_selectiveFilterStrings);
         }
         // read the exclude filter strings
         String excludeFilter = config.getValue("Logging.excludeFilters",null);
         if (excludeFilter != null)
         {
            System.out.println("Exclude filter strings : " + excludeFilter );
            // gather the selective filter strings
            Tokenizer.tokenizeInto(excludeFilter,",",m_excludeFilterStrings);
            System.out.println("After parsing : " + m_excludeFilterStrings);
         }
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         BaseSupport.log(x);
         m_out = new PrintWriter(System.out);
      }
      catch(IOException y)
      {
         BaseSupport.log(y);
         m_out = new PrintWriter(System.out);
      }
      finally
      {
         initMsgToIntTable();
      }

    }
   void initMsgToIntTable()
   {
      m_msgToIntTable.put("security",new Integer(0));
      m_msgToIntTable.put("info",new Integer(1));
      m_msgToIntTable.put("warning",new Integer(2));
      m_msgToIntTable.put("error",new Integer(3));
      m_msgToIntTable.put("fatal",new Integer(4));
   }

   int translateMsgType(final String msgType )
   {
      Object msgLevel = m_msgToIntTable.get(msgType.toLowerCase());
      if (msgLevel == null)
      {
         return 1;
      }
      else
      {
         return ((Integer)msgLevel).intValue();
      }
   }
    public void log(String message, String msgType)
    {
      if (message == null)
      {
         log("Error/log: An attempt to log a null message","error");
      }
      if (translateMsgType(msgType) >= m_msgLevel )
      {
         // exclude messages based on exclude filters
         if (messageStartsWith(m_excludeFilterStrings,message))
         {
           // message starts with an exclusionary string
           // don't print it
           return;
         }
         // include messages based on selective filters
         if (!messageStartsWith(m_selectiveFilterStrings,message))
         {
            //Doesn't start with a selected message begining
            // return if there any selective filters
            if (m_selectiveFilterStrings.size() > 0)
            {  
               // Selective filters are in affect
               return;
            }
         }
         // message starts with a selective pattern
         m_out.println(getTimeString() + ":\t" + message );
         if (m_bFlush == true )
         {
            m_out.flush();
         }
      }// msg level filter
    }// log function
    private String getTimeString()
    {
      return m_calendar.getCurTimeString();
    }
    public void log(Throwable t)
    {
      // If the exception has a message log it
      if (t.getMessage() != null)
      {
         log(t.getMessage(),"error");
      }
      // Log trace if trace is enabled and logging is enabled
      if (m_bTraceEnabled == true)
      {
         if (translateMsgType("error") >= m_msgLevel)
         {
            t.printStackTrace(m_out);
            if (m_bFlush == true )
            {
               m_out.flush();
            }
         }
      } // end of trace
    }
    /**
     * Log the message irrespective
     * Log the trace if it is enabled
     */
    private void unconditionalLog(Throwable t)
    {
      // If the exception has a message log it
      if (t.getMessage() != null)
      {
         unconditionalWriteMessage(t.getMessage());
      }
      // Log trace if trace is enabled and logging is enabled
      if (m_bTraceEnabled == true)
      {
            t.printStackTrace(m_out);
            if (m_bFlush == true )
            {
               m_out.flush();
            }
      } // end of trace
    }
    /**
     * New support for logging exceptions based on their final cause
     */
    public void log(String cause, Throwable t)
    {
      if (writeMessage(cause) == false)
      {
         // Message not written
         // No need to print the exception
         return;
      }
      else
      {
         // message written
         unconditionalLog(t);
      }
    }
    private void unconditionalWriteMessage(String message)
    {
         // message starts with a selective pattern
         m_out.println(getTimeString() + ":\t" + message );
         if (m_bFlush == true )
         {
            m_out.flush();
         }
    }
    /**
     * Returns true if wrote the message to the log
     */
    private boolean writeMessage(String message)
    {
      
      // They only want to see messages with level 4 or above
      if (m_msgLevel > 4)
      {
         return false;
      }
         // exclude messages based on exclude filters
         if (messageStartsWith(m_excludeFilterStrings,message))
         {
           // message starts with an exclusionary string
           // don't print it
           return false;
         }
         // include messages based on selective filters
         if (!messageStartsWith(m_selectiveFilterStrings,message))
         {
            //Doesn't start with a selected message begining
            // return if there any selective filters
            if (m_selectiveFilterStrings.size() > 0)
            {
               // Selective filters are in affect
               // and this is not one of the selected message
               return false;
            }
         }
         // message starts with a selective pattern
         m_out.println(getTimeString() + ":\t" + message );
         if (m_bFlush == true )
         {
            m_out.flush();
         }
         return true;
    }    

    private boolean messageStartsWith(Vector messageBeginings, String message)
    {
       if (messageBeginings.size() == 0) return false;
       for (Enumeration e=messageBeginings.elements();e.hasMoreElements();)
       {
         String curString = (String)e.nextElement();
         if (message.startsWith(curString))
            return true;
       }
       return false;
    }

    public Object executeRequest(String requestName, Object args) {
      return new CLog();
    }
}

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
import java.util.*;
import com.ai.common.AICalendar;
import com.ai.common.Tokenizer;
import com.ai.common.CommonException;

/**
 * 
 * @author Satya
 * To do
 * *******************
 * 1. Document why this version is invented?
 * 2. what does it do more than clog1
 * 
 */
public class CLog2 implements ILog1, ICreator
{
   private  PrintWriter    m_out = null;
   private  Hashtable      m_msgToIntTable = new Hashtable();
   private  int            m_msgLevel = 1;
   private  boolean        m_bFlush = true;
   private  AICalendar     m_calendar = AICalendar.getInstance();
   private  Vector         m_selectiveFilterStrings = new Vector();
   private  Vector         m_excludeFilterStrings = new Vector();
   private  boolean        m_bTraceEnabled = false;
   private boolean      m_initialized = false;

    public CLog2()
    {
    }
    public CLog2(IConfig config)
    {
      init(config);
    }

	private FileOutputStream getFileStream( String fileName, IConfig cfg)
      throws CommonException
	{
	  String relativeHtmlFilename  = fileName;
      String specifiedFileSeparator = cfg.getValue("directories.file_separator","\\");
      if (specifiedFileSeparator.equals(File.separator) == false)
      {
         relativeHtmlFilename = relativeHtmlFilename.replace(specifiedFileSeparator.charAt(0)
                                             ,File.separatorChar);
      }

      // See if you can create the log file
      try
      {
         FileOutputStream fos = new FileOutputStream(relativeHtmlFilename);
         if (fos != null)
         {
            System.out.println("Info: Logfile resolved to: " + relativeHtmlFilename);
         }
         return fos;
      }
      catch(java.io.FileNotFoundException x)
      {
         System.out.println("Info: Logfile path not found for:" + relativeHtmlFilename);
         System.out.println("Info: Make sure the directory exists. Going to try with directory aliases");
      }


      // No.
      // Do the substitution
      StringTokenizer tokenizer = new StringTokenizer(relativeHtmlFilename,":");
      if (tokenizer.countTokens() < 2 )
      {
         throw new CommonException("Error:Logfile: Not an absolute path and No directory aliases detected.");
      }

      // There certainly are two tokens with : separator
      // first part is the root path
      // second part is the relative path
      String rootSpec = tokenizer.nextToken();
      String rootSpecTranslated = cfg.getValue("directories." + rootSpec ,null);

      if (rootSpecTranslated == null)
      {
         throw new CommonException("Error:Logfile: Not an absolute path, directory alias " + rootSpec + " is not specified");
      }

      String finalFilename = rootSpecTranslated + tokenizer.nextToken();
      try
      {
         FileOutputStream fos = new FileOutputStream(finalFilename);
         if (fos != null)
         {
            System.out.println("Info: Logfile resolved to: " + finalFilename);
         }
         return fos;
      }
      catch(java.io.FileNotFoundException x)
      {
          throw new CommonException("Could not create file:" + finalFilename);
      }
	}


    public void init(IConfig config)
    {
      // if already initialed return true
      if (m_initialized == true)
      {
         return;
      }

      //Not initialized, initialize it
      this.m_initialized = true;
      if (config == null)
      {
         m_out = new PrintWriter(System.out);
         return;
      }

      // Configuration available
      try
      {
         // read the log filename
         String filename = config.getValue("Logging.logfile");
         System.out.println("Writing to log file : " + filename);

         //Case of sysout
    	  if (filename.equalsIgnoreCase("sysout"))
		  {
		     m_out = new PrintWriter(System.out);
		  }
    	  
    	  //Case of syserr
    	  else if (filename.equalsIgnoreCase("syserr"))
    	  {
    	  	m_out = new PrintWriter(System.err);
    	  }
    	  
    	  //Most likely a coded filename
    	  else
    	  {
    	  	m_out = new PrintWriter(getFileStream(filename,config));
    	  }

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
      catch(CommonException z)
      {
         BaseSupport.log(z);
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
    protected String getTimeString()
    {
      return m_calendar.getCurTimeString();
    }
    protected AICalendar getCalendar()
    {
    	return m_calendar;
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

    public Object executeRequest(String requestName, Object args)
    {
      if (args != null)
      {
         IConfig inConfig = (IConfig)args;
         init(inConfig);
      }
      else
      {
         init(null);
      }
      return this;
    }
//*******************************************************************************
//* Implementing ILog1 interfaces
//*******************************************************************************

   public boolean isItNecessaryToLog(int logLevel)
   {
      return (logLevel >= this.m_msgLevel);
   }

}
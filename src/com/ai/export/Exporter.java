/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.export;

import com.ai.xml.AITransform;
import java.io.*;
import com.ai.application.defaultpkg.ApplicationHolder;
import com.ai.htmlgen.FormUtils;
import com.ai.application.utils.AppObjects;
import com.ai.servletutils.ServletUtils;
import java.util.Hashtable;
import java.util.Vector;
import java.sql.Connection;
import com.ai.db.IConnectionManager;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.db.DBException;
import com.ai.common.FileUtils;
import com.ai.common.ITranslator;
import com.ai.common.ITranslatorHashtable;
import com.ai.htmlgen.IFormHandler;
import com.ai.htmlgen.IControlHandler1;
import com.ai.common.Tokenizer;

public class Exporter extends com.ai.common.ACommandLineApplication
{
   private PrintWriter m_log = null;
   private PrintWriter outWriter = null;
   
   // Base class level obligations
   public Exporter(String[] args)
   {
      super(args);
   }
  protected int internalStart(String[] args)
  {
      System.out.println("Exporting export definition \"" + args[0] 
                           + "\" from property file \"" + args[1] );
      ApplicationHolder.initApplication(args[1], null);
      AppObjects.log(com.ai.aspire.AspireReleases.getShortCurRelease());

      // Get the third command line argument as the additional set of arguments
      String argString = null;
      if(args.length > 2)
      {
         argString = args[2];
      }
      if (performExport(args[0],argString) == true )
         return 0;
      else
         return 1;         
  }
  
  protected boolean  verifyArguments(String[] args)
  {
      if (args.length < 2)  return false;
      return true;
  }
  public static void main(String[] args)
  {
     try
     {
        // we need two arguments
        Exporter exp = new Exporter(args);
        exp.start();
     }
     catch (Throwable t)
     {
      System.out.println(t.getMessage());
      t.printStackTrace();
     }
  }
  protected String getHelpString()
  {
      String line1 = "Command line:\t\t\t java com.ai.export.Exporter [/h|/?] <export_definition> <application property file>";
      String line2 = "\nExport definition: \tName of the export definition from the property file";
      String line3 = "\nProperty file: \tAbsolute path of the property filename.";
      return super.getHelpString() + "\n" + line1 + line2 + line3;
  }

  //**************************************************************************
  //* Real export application level logic
  //**************************************************************************
  public boolean performExport(String exportSpec, String cmdLineArgString)                                
  {
      boolean bRollBack = false;
      Connection con = null;
      try
      {
         // Gather arguments
         String argsString = AppObjects.getIConfig().getValue(exportSpec + ".args",null);
         Hashtable cmdLineArgs = Tokenizer.tokenizeArgsInto(cmdLineArgString, new Hashtable(),true);
         Hashtable args = updateArgs(argsString, cmdLineArgs);
         
         String formName      = AppObjects.getIConfig().getValue(exportSpec + ".formHandlerName");
         String outFileName   = getOutFilename(exportSpec,args);
         String logFileName = getLogFilename(exportSpec, outFileName );
         String templateFile  = FileUtils.translateFileName(
                  AppObjects.getIConfig().getValue(exportSpec + ".template"));
                  
         args.put("export_filename",outFileName);
         String dataSourceName  = AppObjects.getIConfig().getValue(exportSpec + ".db", null);
                  

         m_log = new PrintWriter( new FileOutputStream(logFileName));
         writeToScreen("Start export of export definition :" + exportSpec);
         writeToLog("Start export of export definition :" + exportSpec);
         writeToLog("Opening export file : " + outFileName );
         outWriter = new PrintWriter( new FileOutputStream(outFileName));
         // obtain a connection
         if (dataSourceName != null)
         {
            con = getConnection(dataSourceName);
            con.setAutoCommit(false);
            args.put("aspire.reserved.jdbc_connection",con);
         }            
         else
         {
            AppObjects.log("Datasource name not specified for export");
//            return;
         }
         writeToLog("Reading dataset from database");
         // Get an output stream for export         
         IFormHandler dataSet = FormUtils.getFormHandlerFor(formName,args);
         IControlHandler1 exportLoopControlHandler = (IControlHandler1)dataSet.getControlHandler("export_loop");
         
         if (dataSet.isDataAvailable() == true)
         {
            AITransform.processHtmlPage( templateFile
                               ,outWriter
                               ,dataSet
                               );
            writeToLog("Number of rows exported : " + exportLoopControlHandler.getNumberOfRowsRetrieved());                               
          }                               
          else
          {
            writeToLog("No data available");
            AppObjects.log("ex: No data found for this export.");
            AppObjects.log("ex: Output filename for the export is : " + outFileName);
          }
          writeToLog("Closing export file");
         if (con != null)
         { 
            AppObjects.log("db: Commiting the connection");  
            con.commit();
          }  
      }
      catch (ConfigException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
      catch (IOException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
      catch (RequestExecutionException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
      catch (java.sql.SQLException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
      catch (DBException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
       catch(com.ai.htmlgen.ControlHandlerException x)
      {
         bRollBack = true;
         AppObjects.log("db: Could not obtain the loop data for export_loop");
         AppObjects.log(x);
      }
      finally                
      {
         writeToScreen("End of export");
         // close files
         // Commit the connection
         if (outWriter != null)
         {
            outWriter.flush();outWriter.close();
         }
         if (m_log != null)
         {
            m_log.flush();m_log.close();
         }
         
         if (con != null)
         {
            try {con.setAutoCommit(true);}
            catch(java.sql.SQLException x)
            {
               AppObjects.log("Could not set autocommit to true");
               AppObjects.log(x); 
            }
            if (bRollBack == true)
            {
               rollbackAndReturnConnection(con);
            }
            else
            {
               try {putConnection(con);}
               catch(com.ai.common.CommonException x){ AppObjects.log(x); }
            }               
         }            
      }
         return (!bRollBack);  
  }
  private void rollbackAndReturnConnection(Connection con)
  {
     if (con == null) return;
     try
     {
        AppObjects.log("db: Errors detected. Rolling back the connection");
        con.rollback();
        putConnection(con);
      }
      catch(java.sql.SQLException x)
      {
         AppObjects.log(x);
         AppObjects.log("Could not rollback the connection");
      }        
      catch(RequestExecutionException x)
      {
         AppObjects.log(x);
         AppObjects.log("Could not return the connection");
      }        
      catch(DBException x)
      {
         AppObjects.log(x);
         AppObjects.log("Could not return the connection");
      }        
  }                  
  
  private Hashtable updateArgs( String argsString, Hashtable cmdLineArgs )
  {
      if (argsString == null)
      {
         return cmdLineArgs;
      }
      return cmdLineArgs;
  }
  private Connection getConnection(String datasourceName)
   throws RequestExecutionException, DBException
  {
      IConnectionManager conMan = 
         (IConnectionManager)(AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null));
      return conMan.getConnection(datasourceName);
  }
  private void putConnection(Connection con)
   throws RequestExecutionException, DBException
  {
      AppObjects.log("db: Returning connection to the pool");
      IConnectionManager conMan = 
         (IConnectionManager)(AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null));
      conMan.putConnection(con);
  }
  private String getOutFilename(String exportSpec, Hashtable args )
   throws com.ai.application.interfaces.ConfigException, RequestExecutionException
  {
         String fname = FileUtils.translateFileName(
                  AppObjects.getIConfig().getValue(exportSpec + ".outputFileName"));
         // see  if there is a filetranslator specified
         Object obj = AppObjects.getIFactory().getObject(exportSpec + ".filenameTranslator" ,null);
         if (obj instanceof ITranslator)
         {
            ITranslator translator = (ITranslator)obj;
            fname = translator.translateString(fname);
         }
         else if (obj instanceof ITranslatorHashtable)
         {
            ITranslatorHashtable translator = (ITranslatorHashtable)obj;
            fname = translator.translateString(fname,args);
         }
         else
         {
            throw new RequestExecutionException("Error:translaor: Doesn't match translator type");
         }
         return fname;
  }
  private String getLogFilename(String exportSpec , String exportFilename)
  {
      // see if there is a directory specified for the log files
      // if then
      //    Create a log file in that directory
      // else
      //    Just append the .log to the end of the exportFilename
      String logDirectory = AppObjects.getIConfig().getValue(exportSpec + ".logDirectory",null);
      if (logDirectory == null)
      {
         return exportFilename + ".log";
      }
      // directory exists
      String logFilename = logDirectory 
                           + File.separator 
                           + FileUtils.basename(exportFilename) + ".log";
      System.out.println("Log Filename before translation: " + logFilename );
      logFilename = FileUtils.translateFileName(logFilename);
      System.out.println("Log Filename after translation: " + logFilename );
      return logFilename;
  }

  
  
  private void writeToLog(String msg)
  {
    AppObjects.log("ex:" + msg);
    if (m_log != null)
    {
      m_log.println(com.ai.common.AICalendar.getCurTimeString() + ":" + msg);
    }
  }
  private void writeToScreen( String msg )
  {
      System.out.println(com.ai.common.AICalendar.getCurTimeString() + ":" + msg);
  }
} 

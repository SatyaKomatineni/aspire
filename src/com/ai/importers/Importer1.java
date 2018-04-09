package com.ai.importers;

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
import com.ai.xml.AITransform;
import java.io.*;
import com.ai.application.defaultpkg.ApplicationHolder;
import com.ai.htmlgen.FormUtils;
import com.ai.application.utils.AppObjects;
import com.ai.servletutils.ServletUtils;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.sql.Connection;
import com.ai.db.IConnectionManager;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.db.DBException;
import com.ai.common.FileUtils;
import com.ai.common.ITranslator;
import com.ai.htmlgen.IFormHandler;

public class Importer1 extends com.ai.common.ACommandLineApplication
{
   private String m_importSpec = null;
   private String m_importFilename = null;
   
   private BufferedReader m_importFileReader = null;
   private PrintWriter m_rejectFileWriter = null;
   private PrintWriter m_errorFileWriter = null;
   
   private Connection m_con = null;
   private Hashtable m_args = null;
   
   // Base class level obligations
   public Importer1(String[] args)
   {
      super(args);
   }
  protected int internalStart(String[] args)
  {
      System.out.println("Importing import definition \"" + args[0] + "\" from property file \"" + args[1] );
      System.out.println("Using " + args[2] + " as the file to import ");
      ApplicationHolder.initApplication(args[1], null);
     System.out.println(com.ai.aspire.AspireReleases.getShortCurRelease());
     AppObjects.log(com.ai.aspire.AspireReleases.getShortCurRelease());
      if (performImport(args[0], args[2]) == true )
         return 0;
      else
         return 1;         
  }
  
  protected boolean  verifyArguments(String[] args)
  {
      if (args.length < 3)  return false;
      return true;
  }
  public static void main(String[] args)
  {
     // we need two arguments
     try
     {
        Importer1 imp = new Importer1(args);
        imp.start();
      }
      catch(Throwable t)
      {
         AppObjects.log(t);
      }
  }
  protected String getHelpString()
  {
      String line1 = "Command line:\t\t\t java com.ai.import.Importer [/h|/?] <import_definition> <application property file> <import filename>";
      String line2 = "\nImport definition: \tName of the import definition from the property file";
      String line3 = "\nProperty file: \tAbsolute path of the property filename.";
      String line4 = "\nimport file: \tAbsolute path of the import filename.";
      return super.getHelpString() + "\n" + line1 + line2 + line3 + line4;
  }

  //**************************************************************************
  //* Real export application level logic
  //**************************************************************************

  private void init(String importSpec, String importFilename )
      throws java.io.IOException, java.sql.SQLException
  {
      m_importSpec = importSpec;
      m_importFilename = importFilename;
      // Obtain file streams for all the concerned files
      m_importFileReader = new BufferedReader(new FileReader( importFilename ));
      
      m_rejectFileWriter = new PrintWriter( 
               new FileOutputStream(importFilename + ".reject" ));
      m_errorFileWriter = new PrintWriter(      
               new FileOutputStream(importFilename + ".error" ));
              
      // Get a connection
      m_con = getConnection(importSpec);
      m_args = createArgs(importSpec);  
      m_args.put("import_filename",importFilename);
      // Start import
  }
  public boolean performImport(String importSpec, String importFilename)                                
  {
      boolean bRollBack = false;
    try
    {
        init(importSpec, importFilename);
        if (m_con != null) 
        {
           try {m_con.setAutoCommit(false);}
           catch (java.sql.SQLException x) 
           { 
            AppObjects.log("Could not set autocommit to false");
            AppObjects.log(x);
            return false;
           }
           m_args.put("aspire.reserved.jdbc_connection",m_con);
        }         
        if (executePreImportRequest(importSpec, m_args ) == false)
        {
           bRollBack = true;
           AppObjects.log("imp: Preexecution request failed ");
           return false;
        }
                    
        String line;
        int totalLines = 0;
        int numOfSuccessfullLines =0;
        int numOfRejectedLines = 0;
        int lineNum = 0;
        while((line=m_importFileReader.readLine()) != null)
        {
           boolean result = processLine( importSpec, line, m_args );
           if (result == true)
           {
               numOfSuccessfullLines++;
           }
           else
           {
               numOfRejectedLines++;
           }
           lineNum++;
           if (lineNum > 10)
           {
              m_con.commit();
              System.out.println("So far " + numOfSuccessfullLines + numOfRejectedLines + " lines have been processed");
              lineNum = 0;
           }
        }
        if (lineNum > 0)
        {
           m_con.commit();
        }
        
        AppObjects.log("Import successfully completed");

        // report end of report
        reportEndOfImport(numOfSuccessfullLines, numOfRejectedLines);
        return true;
    } // try
    catch(java.sql.SQLException x)
    {
       bRollBack = true;
       AppObjects.log(x);
    }        
    catch(java.io.IOException x)
    {
      if (x instanceof java.io.FileNotFoundException)
      {
          bRollBack = true;
          rollbackAndReturnConnection(m_con);
          AppObjects.log("imp: Could not open the import file for read");
      }
      else
      {
         AppObjects.log("Could not read from the inut file");
         bRollBack = true;
      }
    }
      finally
      {
         try
         {
            AppObjects.log("End of import");
           
            AppObjects.log("Closing the files");
            if (m_importFileReader != null)
               m_importFileReader.close();
            if (m_rejectFileWriter != null)
               m_rejectFileWriter.close();
            if (m_errorFileWriter != null)
               m_errorFileWriter.close();
                
            AppObjects.log("Returning the connection");
            if (bRollBack == true)
            {
               rollbackAndReturnConnection(m_con);
               return false;
            }
            else
            {
               if (m_con != null)
                  putConnection(m_con);
               return true;                  
            }
         }
         catch( java.io.IOException x)
         {
            AppObjects.log("Could not close import file");
            AppObjects.log(x);
            return false;
         }              
      }
  }
  private boolean processLine(String importSpec, String line, Hashtable inArgs)
   throws java.io.IOException
  {
      try 
      {
         Hashtable args = createLineArgs(inArgs, line);
         AppObjects.getIFactory().getObject(importSpec + ".importRequest",args);
      }
      catch(RequestExecutionException x)
      {
         addToRejectFile( line );
         
         logToErrorFile("Error processing: " + line);
         logToErrorFile("Error message follows ");
         logToErrorFile(x.getMessage());
         
         AppObjects.log("Error processing line :" + line );
         AppObjects.log(x);
         return false;
      }
      return true;      
  }
  private Hashtable createLineArgs(Hashtable inArgs, String line)
  {
         Hashtable args = new Hashtable();
         Vector v = com.ai.common.Tokenizer.tokenize(line,",","null");
         int i=1;
         for (Enumeration e=v.elements();e.hasMoreElements();)
         {
            Integer thisInt = new Integer(i++);
            String elem = (String)e.nextElement();
            if (!elem.equals("null"))
            {
               args.put(thisInt.toString(), elem);
            }               
         }
         // append the input args to this
         for(Enumeration e=inArgs.keys();e.hasMoreElements();)
         {
            Object key = e.nextElement();
            args.put(key,inArgs.get(key));
         }
         return args;
  }
  private boolean executePreImportRequest(String importSpec
                                   , Hashtable args)
  {
      try
      {
         String classRequest = AppObjects.getIConfig().getValue(
               "request." + importSpec + ".preExecRequest.className", null);
         if (classRequest == null)
         {
         // no preexec needed
         return true;
         }   
         // Get the request name
         AppObjects.getIFactory().getObject(importSpec + ".preExecRequest",args);
         return true;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("Preexecution failed");
         AppObjects.log(x);
         return false;
      }       
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
  }                  
  
   private Hashtable createArgs(String importSpec)
   {
      Hashtable args = new Hashtable();
         String argsString = AppObjects.getIConfig().getValue(importSpec + ".args",null);
         if (argsString == null)
         {
         return args;
         }
         return args;
   }
   private Connection getConnection(String importSpec)
   {
      try
      {
         String dataSourceName  = AppObjects.getIConfig().getValue(importSpec + ".db", null);
         if (dataSourceName == null) return null;
         IConnectionManager conMan = 
         (IConnectionManager)(AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null));
         return conMan.getConnection(dataSourceName);
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log(x);
         return null;
      }
      catch(DBException x)
      {
         AppObjects.log(x);
         return null;
      }
   }                 
  private void putConnection(Connection con)
  {
      try 
      {
          AppObjects.log("db: Returning connection to the pool");
          con.setAutoCommit(true);
          IConnectionManager conMan = 
             (IConnectionManager)(AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null));
          conMan.putConnection(con);
      }          
      catch(com.ai.db.DBException x)
      {
        AppObjects.log("Could not return the connection ");
        AppObjects.log(x);
      }
      catch(RequestExecutionException x)
      {
        AppObjects.log("Could not return the connection ");
        AppObjects.log(x);
      }
      catch(java.sql.SQLException x)
      {
        AppObjects.log("Could not return the connection ");
        AppObjects.log(x);
      }
  }                               
  private void addToRejectFile(String line)
   throws java.io.IOException
  {
      m_rejectFileWriter.println(line);
  }
  private void logToErrorFile( String msg) throws java.io.IOException
  {
      m_errorFileWriter.println(com.ai.common.AICalendar.getCurTimeString() + ":" + msg);
  }
        private void reportEndOfImport( int numOfSuccessfullLines,
                                        int numOfRejectedLines )
                                        throws java.io.IOException
        {        
            int totalLines = numOfSuccessfullLines + numOfRejectedLines;
            
           // write to sys.out
           System.out.println("End of import");
           System.out.println(totalLines + " lines have been proccessed");
           
           System.out.println(numOfSuccessfullLines + " lines have been successful");
           System.out.println(numOfRejectedLines + " lines have been rejected");
           
           logToErrorFile("End of import");
           logToErrorFile(totalLines + " lines have been proccessed");
           logToErrorFile(numOfSuccessfullLines + " lines have been successful");
           logToErrorFile(numOfRejectedLines + " lines have been rejected");
        }
} 
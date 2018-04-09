/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.utils;
 
import com.ai.data.*;
import com.ai.jawk.*;
import com.ai.application.utils.*;
import com.ai.application.defaultpkg.ApplicationHolder;
import java.io.*;
import java.util.*;
import com.ai.common.*;
import com.ai.htmlgen.*;

public class GenJSP extends com.ai.common.ACommandLineApplication
{
   
   // Base class level obligations
   public GenJSP(String[] args)
   {
      super(args);
   }
  protected int internalStart(String[] args)
  {
      try 
      {
         String propertyFilename = null;
         if (args.length > 0)
         {
            propertyFilename = args[0];
         }
         else
         {
            propertyFilename = "g:\\cb\\com\\ai\\application\\test\\TestAppConfig.properties";
         }            
         System.out.println("Generating JSP files for aspire templates");
         
         ApplicationHolder.initApplication(propertyFilename,args);
         FileProcessor fp = new FileProcessor();
         fp.addFileProcessorListener(new GenJSPListener());
         fp.processFile("stdin");
         return 0;
      }
      catch(java.io.IOException x)
      {
         x.printStackTrace();
         return 1;
      }
  }
  
  protected boolean  verifyArguments(String[] args)
  {
      if (args.length < 1)  return false;
      return true;
  }
  public static void main(String[] args)
  {
     // we need two arguments
     GenJSP app = new GenJSP(args);
     app.start();
  }
  
  protected String getHelpString()
  {
      String line1 = "Command line:\t\t\t java com.ai.aspire.utils.GenJSP [/h|/?] <application property file>";
      String line3 = "\nProperty file: \tAbsolute path of the property filename.";
      return super.getHelpString() + "\n" + line1 + line3;
  }

  private boolean invokedStandalone = false;
} 

class GenJSPListener extends AFileProcessorListener
{
   BufferedReader  m_reader;
   
   static final int TEMPLATE_FILE = 1;
   static final int JSP_FILE = 2;
   
   private int m_expected; 
   private String m_templateFile;  
   
    public void beginOfFile(BufferedReader reader )
    {
      m_reader = reader;
      System.out.println("Testing starts" );
      System.out.println("Please enter the name of the template file ");
      m_expected = TEMPLATE_FILE;   
    }
    public void newLine( final String line )
    {
       if (line.toLowerCase().equals("quit"))
       {
         System.out.println("Application exiting ");
          System.exit(0);
       }
       if (m_expected == JSP_FILE)
       {
         m_expected = TEMPLATE_FILE;
         process(m_templateFile, line);
         System.out.println("Completed processing of the template file");
         System.out.println("Please enter the name of the template file ");
       } 
       else
       {
         // expecting template file
         m_templateFile = line;     // remember the template file
         m_expected = JSP_FILE;
         System.out.println("Please enter the name of the jsp file ");
       }
    }                                   
    public  void endOfFile()
    {
      System.out.println("End of test ");
    }

    void process(String templateFile, String jspFile)
    {
      PrintWriter jspWriter = null;
      try
      {
      
         jspWriter = new PrintWriter(new FileOutputStream(jspFile));

         JSPGenTagListener myForm = new JSPGenTagListener();
         AITransformWithTranslator.processHtmlPage(templateFile
                  ,jspWriter
                  ,myForm      // form handler
                  ,myForm      // evaluator
                  ,myForm);    // tag listener
      }
      catch(FileNotFoundException x)
      {
         System.out.println(x.getMessage());
         AppObjects.log(x);
         return;
      }
      catch(IOException x)
      {
         System.out.println(x.getMessage());
         AppObjects.log(x);
         return;
      }
                
      finally
      {
         if (jspWriter != null)
         {
            jspWriter.close();
         }
      }
    }
}
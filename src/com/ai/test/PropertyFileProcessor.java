/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;
import com.ai.data.*;
import com.ai.jawk.*;
import com.ai.application.utils.*;
import com.ai.application.defaultpkg.ApplicationHolder;
import java.io.*;
import java.util.*;
import com.ai.common.*;
import com.ai.application.interfaces.IConfig;

public class PropertyFileProcessor extends com.ai.common.ACommandLineApplication
{
   private PrintWriter m_log = null;
   private PrintWriter outWriter = null;
   
   // Base class level obligations
   public PropertyFileProcessor(String[] args)
   {
      super(args);
   }
  protected int internalStart(String[] args)
  {
      try 
      {
         String propertyFilename = args[0];
         System.out.println("Processing each of the properties files for master properties file: \"" + propertyFilename + "\"" );
         ApplicationHolder.initApplication(propertyFilename,args);

         IConfig cfg = AppObjects.getIConfig();
         String includeFiles = cfg.getValue("application.includefiles",null);
         
         processFile(propertyFilename, cfg);
         if (includeFiles == null)
         {
            return 0;
         }
         // include files exist
         System.out.println("config: Include files detected");
         Vector includeFilesVector = com.ai.common.Tokenizer.tokenize(includeFiles,",");
         
         for(Enumeration includeFilesEnum=includeFilesVector.elements();
               includeFilesEnum.hasMoreElements();)
         {
            try 
            {
               String includeFilename = ((String)includeFilesEnum.nextElement());
               String translatedFilename = 
               com.ai.common.FileUtils.translateFileName(includeFilename);
               processFile(translatedFilename,cfg);
            }
            catch(java.io.IOException x)
            {
               x.printStackTrace();
               continue;
            }                     
         }

         return 0;
      }
      catch(java.io.IOException x)
      {
         x.printStackTrace();
         return 1;
      }
      finally
      {
         System.out.println("All property files processed");
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
     PropertyFileProcessor app = new PropertyFileProcessor(args);
     app.start();
  }
  
  protected String getHelpString()
  {
      String line1 = "Command line:\t\t\t java com.ai.test.PropertyFileProcessor [/h|/?] <application property file>";
      String line3 = "\nProperty file: \tAbsolute path of the property filename.";
      return super.getHelpString() + "\n" + line1 + line3;
  }
  /**
   *   place your code here
   */
  void processFile(final String fullFilename, IConfig applicationConfig) throws java.io.IOException
  {
         System.out.println("Processing properties file: " + fullFilename);
  }
}


/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.extensions.xmlconfig;

import java.util.Properties;
import java.util.Hashtable;
import com.ai.application.interfaces.*;
import com.ai.application.interfaces.AConfig;
import com.ai.application.defaultpkg.*;
import com.ai.common.*;

import java.util.*;

import java.io.FileInputStream;

public class ConfigWithXMLIncludes extends AConfig implements ICreator
{
    private Hashtable m_cachedBundles = new Hashtable();
    private Properties m_bootStrapProperties = new Properties();
        
    public String getValue(String key)  throws ConfigException
    {
      return getValueFromSource(ApplicationHolder.getDefaultConfigFile(),key);
    }

    public String getValueFromSource(String source, String key) throws ConfigException
    {
         Properties thisBundle = getBundle(source);
         String value = thisBundle.getProperty(key.toLowerCase(),null);
         if (value == null)
         {
                throw new ConfigException( source
                           ,key
                           ,ConfigException.CONFIG_KEY_NOT_FOUND);
         }
         return value;
    }
    private Properties getBundle( String bundleName )
        throws ConfigException
    {
      Object obj = m_cachedBundles.get(bundleName);
      if (obj != null)  return (Properties)obj;

      // Bundle not found in cache
      // Go to properties files to load it
      Properties bundle = loadProperties(bundleName);
      m_cachedBundles.put(bundleName, bundle);
      return bundle;

    }
    
    public Object executeRequest(String requestName, Object args) 
        throws RequestExecutionException
    {
        if (args != null)
        {
            Properties p = (Properties)args;
            BaseSupport.log("Info:Boot strap properties detected. And these are as follows:");
            BaseSupport.log("Info:" + p);
            this.m_bootStrapProperties = p;
        }
        return this;
    }

    private Properties loadProperties(final String filename)
      throws com.ai.application.interfaces.ConfigException
    {
              Properties mainFileProps = null;
              try{ mainFileProps = getPropertiesFrom(filename); }
              catch(java.io.IOException x)
              {
                System.out.println("fatal.config: Could not load the main config file " + filename );
                throw new com.ai.application.interfaces.ConfigException(
                     filename
                     ,"unknown"
                     ,"fatal.config: Could not load the main config file " + filename);
              }
              Properties lowerCaseProps = new Properties();
              
              // Start out with the boot strap properties
              appendAsLowerCaseProperties(this.m_bootStrapProperties,lowerCaseProps);
              
              // Over lay the bootstrap properties with mainFileProperties
              appendAsLowerCaseProperties(mainFileProps,lowerCaseProps);
              
              Vector includeFilesVector = getIncludeFilesVector(lowerCaseProps);
                            
              for(Enumeration includeFilesEnum=includeFilesVector.elements();
                  includeFilesEnum.hasMoreElements();)
              {
                  try {
                     String includeFilename = ((String)includeFilesEnum.nextElement());
                     String translatedFilename = 
                        com.ai.common.FileUtils.translateFileName(includeFilename,lowerCaseProps);
                     Properties theseProps = getPropertiesFrom(translatedFilename);
                     appendAsLowerCaseProperties(theseProps, lowerCaseProps);
                  }
                  catch(java.io.IOException x)
                  {
                     //x.printStackTrace();
                     continue;
                  }                     
              }
              return lowerCaseProps;
    }
    public Vector getIncludeFilesVector(Properties prevProperties)
    {
        String includeFiles = prevProperties.getProperty("application.includefiles");
        String includeXmlFiles = prevProperties.getProperty("application.xmlincludefiles");
        if (includeFiles == null)           
        {
         return new Vector();
        }
        // include files exist
        System.out.println("config: Include files detected");
        Vector includeFilesVector = com.ai.common.Tokenizer.tokenize(includeFiles,",");

        // see if I have any xml files included
        if  (includeXmlFiles == null)
        {
            return includeFilesVector;
        }

        // xml include files exist
        // generate properties files for those
        System.out.println("config: XML Include files detected");
        Vector includeXMLFilesVector = com.ai.common.Tokenizer.tokenize(includeXmlFiles,",");
        Vector newXMLPropFiles = generatePropFiles(includeXMLFilesVector,prevProperties);

        // combine the vectors
        for(Enumeration e=newXMLPropFiles.elements();e.hasMoreElements();)
        {
            Object o  = e.nextElement();
            includeFilesVector.add(o);
        }
        return includeFilesVector;
    }

    private Vector generatePropFiles(Vector xmlFiles, Properties lowerCaseProps)
    {
        Vector newFiles = new Vector();
        for(Enumeration includeFilesEnum=xmlFiles.elements();
            includeFilesEnum.hasMoreElements();)
        {
            try {
               String includeFilename = ((String)includeFilesEnum.nextElement());
               String translatedFilename = 
                  com.ai.common.FileUtils.translateFileName(includeFilename,lowerCaseProps);

               System.out.println("Processing XML config file:" + translatedFilename);
               XMLConfigFileProcessor.processXMLConfigFile(translatedFilename);
               newFiles.add(FileUtils.renameExtension(translatedFilename,"properties"));
            }
            catch(Throwable x)
            {
               BaseSupport.log("Error","Could not process an xml config file. Continuing with other files");
               BaseSupport.log(x);
               continue;
            }           
        } // end of for
        return newFiles;
    }
    
    
    private void appendAsLowerCaseProperties(Properties p, Properties lowerCaseProperties)
    {
         for(Enumeration keys = p.keys();keys.hasMoreElements();)
         {
            String thisKey = (String)keys.nextElement();
             lowerCaseProperties.setProperty(thisKey.toLowerCase()
                        ,p.getProperty(thisKey) );
         }
    }
    
    private Properties getPropertiesFrom(final String filename)
      throws java.io.IOException
    {
      FileInputStream fileStream = null;
      try
      {
         fileStream = new FileInputStream(filename);
         System.out.println("info.config: Loading properties from " + filename );
         Properties props = new Properties();
         props.load(fileStream);
         return props;
      }
      catch(java.io.IOException x)
      {
         System.out.println("warn.config: Could not find config file " + filename);
         throw x;
      }
      finally
      {
         if (fileStream != null)
         {
            fileStream.close();
         }
      }
    }
    public void finalize() throws Throwable
    {
      System.out.println("Config object being finalized");
      super.finalize();
    }
} 

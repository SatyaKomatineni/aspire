/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.application.defaultpkg;

import java.util.Properties;
import java.util.Hashtable;
import com.ai.application.interfaces.IResourceReader;
import com.ai.application.interfaces.AConfig;
import java.util.*;

import java.io.InputStream;
import java.io.FileInputStream;
import com.ai.application.interfaces.*;

public class CConfigWithIncludes1 extends AConfig implements ICreator
{
        private Properties m_realProperties = null;
        private Properties m_bootStrapProperties = new Properties();

        public CConfigWithIncludes1()
        {
        }

    public String getValue(String key)  throws ConfigException
    {
       String value = m_realProperties.getProperty(key.toLowerCase(),null);
       if (value == null)
       {
              throw new ConfigException( "configuration"
                         ,key
                         ,ConfigException.CONFIG_KEY_NOT_FOUND);
       }
       return value;
    }

    public String getValueFromSource(String source, String key) throws ConfigException
    {
       throw new ConfigException("Not supported");
    }

    public Object executeRequest(String requestName, Object args)
        throws RequestExecutionException
    {
       try
       {
        if (args != null)
        {
            Properties p = (Properties)args;
            BaseSupport.log("Info:Boot strap properties detected. And these are as follows:");
            BaseSupport.log("Info:" + p);
            this.m_bootStrapProperties = p;
            initialize();
        }
        return this;
       }
       catch(ConfigException x)
       {
          throw new RequestExecutionException("Error:Could not initialize configuration",x);
       }
    }
    private void initialize()
          throws ConfigException
    {
        String propertyFilename = ApplicationHolder.getDefaultConfigFile();
        if (propertyFilename != null)
        {
           initializeUsingPropertiesFile(propertyFilename);
        }
        else
        {
           initializeUsingBaseConfig();
        }
    }

    private void initializeUsingPropertiesFile(String filename) throws ConfigException
    {
       m_realProperties = loadProperties(filename, new FileResourceReader());
    }

    private void initializeUsingBaseConfig() throws ConfigException
    {
       IConfig baseConfig = ApplicationHolder.getBaseConfig();
       if (baseConfig == null)
       {
          throw new ConfigException("Error:Required base configuration object not set");
       }
       //baseConfig available
       String baseResource = baseConfig.getValue("aspire.configuration.baseresource");
       m_realProperties = loadProperties(baseResource,this.getExternalResourceReaderHook());
    }

    private Properties loadProperties(String resourceName, IResourceReader reader)
      throws com.ai.application.interfaces.ConfigException
    {
              Properties mainFileProps = null;
              mainFileProps = getPropertiesFrom(resourceName, reader, new PropertiesConfig(m_bootStrapProperties));
              Properties lowerCaseProps = new Properties();

              // Start out with the boot strap properties
              appendAsLowerCaseProperties(this.m_bootStrapProperties,lowerCaseProps);

              // Over lay the bootstrap properties with mainFileProperties
              appendAsLowerCaseProperties(mainFileProps,lowerCaseProps);

              String includeFiles = lowerCaseProps.getProperty("application.includefiles");
              if (includeFiles == null)
              {
               return lowerCaseProps;
              }
              // include files exist
              System.out.println("config: Include files detected");
              Vector includeFilesVector = com.ai.common.Tokenizer.tokenize(includeFiles,",");
              for(Enumeration includeFilesEnum=includeFilesVector.elements();
                  includeFilesEnum.hasMoreElements();)
              {
                  try {
                     String includeFilename = ((String)includeFilesEnum.nextElement());
                     Properties theseProps = getPropertiesFrom(includeFilename,reader, new PropertiesConfig(lowerCaseProps));
                     appendAsLowerCaseProperties(theseProps, lowerCaseProps);
                  }
                  catch(ConfigException x)
                  {
                     System.out.println("Warn:Exception from reading a resource:" + x.getRootCause());
                     continue;
                  }
              }
              return lowerCaseProps;
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

    private Properties getPropertiesFrom(String resourceName, IResourceReader reader, IConfig prevConfig)
      throws ConfigException
    {
      InputStream fileStream = null;
      try
      {
         fileStream = reader.readResource(resourceName, prevConfig);
         System.out.println("info.config: Loading properties from " + resourceName );
         Properties props = new Properties();
         props.load(fileStream);
         return props;
      }
      catch(java.io.IOException x)
      {
         throw new ConfigException("Error:Could not read resource:" + resourceName,x);
      }
      finally
      {
         if (fileStream != null)
         {
            try { fileStream.close(); }
            catch(java.io.IOException x) {x.printStackTrace();}
         }
      }
    }//eof-function
    protected IResourceReader getExternalResourceReaderHook()
    {
       return new FileResourceReader();
    }
}//eof-class

class FileResourceReader implements IResourceReader
{
   public InputStream readResource(String resourceName, IConfig config) throws java.io.IOException
   {
      System.out.println("Trying to read resource:" + resourceName);
      String translatedFilename =
         com.ai.common.FileUtils.translateFileName(resourceName,config);

      System.out.println("Trying to read translated resource:" + translatedFilename);
      System.out.flush();
      return new FileInputStream(translatedFilename);

   }
}


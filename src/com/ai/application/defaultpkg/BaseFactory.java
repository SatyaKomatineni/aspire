/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/default/BaseFactory.java

package com.ai.application.defaultpkg;

import com.ai.application.interfaces.IConfig;
import com.ai.application.interfaces.AConfig;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.IFactory;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.ICreator;
import java.util.Properties;
import java.io.FileInputStream;

class BaseFactory implements IFactory {

//   private static Properties s_defaultResourceBundle = null;
   private static IConfig m_config = null;
   private static IFactory s_baseFactory = null;


   synchronized static public IFactory getInstance()
   {
      if(s_baseFactory != null) return s_baseFactory;
      m_config = getBaseConfig();
      if (m_config == null)
      {
         m_config = getConfigFromAFile();
      }
      s_baseFactory = new BaseFactory();
      return s_baseFactory;
   }

   /**
    * Default Constructor
    */
    protected BaseFactory() {
    }
    /**
     * getObject returns a supported factory object
     */
    public Object getObject(String inIdentifier, Object args)
      throws RequestExecutionException
    {
         BaseSupport.log("Info","Get Object for : " + inIdentifier);
         String identifier = "request." + inIdentifier;
         if (m_config == null)
         {
            BaseSupport.log("error", "No default configuration is available");

            return new RequestExecutionException(
               RequestExecutionException.PROPERTIES_FILE_NOT_FOUND);
         }
         Object creator = null;
         String objClassName = m_config.getValue(
                                        identifier + ".className"
                                        ,null);
         if (objClassName == null)
         {
                throw new RequestExecutionException(RequestExecutionException.REQUEST_NOT_REGISTERED);
         }
         try
         {
            BaseSupport.log("info", "Loading " + objClassName );
            Class classObj = Class.forName(objClassName);
            creator = classObj.newInstance();
            return ((ICreator)(creator)).executeRequest(identifier, args);
         }
         catch(java.lang.ClassNotFoundException x)
         {
            BaseSupport.log(x);
            throw new RequestExecutionException(x.getMessage());
         }
         catch (java.lang.InstantiationException x)
         {
            BaseSupport.log(x);
            throw new RequestExecutionException(x.getMessage());
         }
         catch(java.lang.IllegalAccessException x)
         {
            BaseSupport.log(x);
            throw new RequestExecutionException(x.getMessage());
         }
         catch(java.util.MissingResourceException x)
         {
                BaseSupport.log(x);
                throw new RequestExecutionException(RequestExecutionException.REQUEST_NOT_REGISTERED);
         }
    }//end of function

    private static IConfig getBaseConfig()
    {

       //See if there is a primary config to use
       IConfig config = ApplicationHolder.getUserConfig();
       if (config != null) return config;

       //Try to see if there is a base config
       config = ApplicationHolder.getBaseConfig();
       if (config != null) return config;

       return null;
    }

    /**
     * @deprecated  see if you can initialize always with a config and not a file
     * @returns a base config
     */
    private static IConfig getConfigFromAFile()
    {
       String defaultConfigFile=null;
       FileInputStream defaultConfigFileStream = null;
       try
       {
          // UserConfig not available. Look for a Config file
          defaultConfigFile =  ApplicationHolder.getDefaultConfigFile();
          defaultConfigFileStream =  new FileInputStream(defaultConfigFile);
          Properties p = new Properties();
          p.load(defaultConfigFileStream);
          IConfig config = new PropertiesConfig(p);
          return config;
       }
       catch (java.io.IOException x)
       {
          BaseSupport.log("warning","Can not find properties file for : "
                         + defaultConfigFile );
          BaseSupport.log(x);
          throw new RuntimeException("Could not find configuration file");
       }
       finally
       {
          if (defaultConfigFileStream != null)
          {
             try {defaultConfigFileStream.close();}
             catch(java.io.IOException x){ x.printStackTrace(); }
          }
       }
    }//eof-function

}//end of class


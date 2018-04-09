/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/default/ApplicationHolder.java

package com.ai.application.defaultpkg;
import java.util.*;

import com.ai.application.interfaces.*;
import com.ai.application.utils.*;

/**
 * Obtains an application object for use as specified in the config file.
 * If no such application object exists, it uses the default one.
 * 
 * 12/5/15
 * ***************
 * There is a bug with initializers
 * if an object is indicated as an initializer but not 
 * implement IApplicationInitalizer system is throwing exception.
 * This is fixed by allowing initializers without this interface.
 * A warning is issued that the initializer is responsible for its own
 * initialization, may be through the factory.
 *  
 */
public class ApplicationHolder extends CAppShutDownAdapter implements IAppShutDownListener {
   /**
    * Place to keep the cached application object.
    * Has an ownership relationship
    */
    static private IApplication s_application  = null;
    static private String  s_defaultConfigFile = null;
    static private boolean m_bAppInitialized = false;

    // An attempt at initialization using an IConfig instead of the config file
    static private IConfig m_userConfig = null;
    static private IConfig m_baseUserConfig = null;

    static public IConfig getBaseConfig()
    {
       return m_baseUserConfig;
    }

   public static boolean isInitialized()
   {
      return m_bAppInitialized;
   }
    public static IConfig getUserConfig()
    {
      return m_userConfig;
    }

    public static  String getDefaultConfigFile()
    {
      if (s_defaultConfigFile == null)
      {
         //throw new RuntimeException("Configuration file has not been set");
         System.out.println("Warn:A request has been made for default configuration file but it is not set yet.");
      }
      return  s_defaultConfigFile;
    }
    static private void setDefaultConfigFile(String inConfigFilename )
    {
      s_defaultConfigFile = inConfigFilename;
    }

    private ApplicationHolder() {
      super();
    }
    public String getOwnerName(){ return "Application Holder"; }
    public boolean shutDown()
    {
      s_application = null;
      return true;
    }
    /**
     * This first call into the application architecture that sets up the Application object.
     * @parameter configFilename Name of the application configuration file
     *            Name of the properties file including the package name
     * @return void
     * sideaffect: Gets the application objects and caches it locally for future use
     *            via getIApplication() method
     * @see getIApplication()
     */

   static synchronized public void initApplication( String configFile)
   {
      Properties bootStrapProperties = null;
      
      initApplication(configFile,bootStrapProperties,null);
   }

   static synchronized public void initApplication( String configFile, String args[] )
   {
      Properties bootStrapProperties = null;
      initApplication(configFile,bootStrapProperties,args);
   }
   
   static synchronized public void initApplication( String configFile
         ,Properties bootStrapProperties
         ,String args[] )
   {
      if (m_bAppInitialized == false)
      {
         setDefaultConfigFile( configFile );
         s_application = createApplication(bootStrapProperties);
         s_application.addAppShutDownListener(new ApplicationHolder());
         s_application.startup(args);
         callInitializers();
         m_bAppInitialized = true;
      }
   }

   static synchronized public void initApplication( IConfig userConfig, String args[] )
   {
      if (m_bAppInitialized == false)
      {
//         setDefaultConfigFile( configFile );
         m_userConfig = userConfig;
         Properties bootStrapProperties = null;
         s_application = createApplication(bootStrapProperties);
         s_application.addAppShutDownListener(new ApplicationHolder());
         s_application.startup(args);
         callInitializers();
         m_bAppInitialized = true;
      }
   }

   static synchronized public void initApplicationWithBaseConfig( IConfig baseConfig, Properties bootStrapProperties, String args[] )
   {
      if (m_bAppInitialized == false)
      {
         m_baseUserConfig = baseConfig;
         s_application = createApplication(bootStrapProperties);
         s_application.addAppShutDownListener(new ApplicationHolder());
         s_application.startup(args);
         callInitializers();
         m_bAppInitialized = true;
      }
   }


   private static boolean callInitializers()
   {
      // Aspire.startup.initializers=init1,init2
      // request.init1.className=com.ai.application.defaultpkg.SystemPropertiesInitializer
      // Will have to implment com.ai.application.interfaces.IApplicationInitializer
      // Aspire.systemProperties=prop1,prop2
      // ..prop1.key
      // ..prop1.value
      //
      IConfig cfg = s_application.getIConfig();
      ILog log = s_application.getILog();
      IFactory fact = s_application.getIAppFactory();

      String initializers = cfg.getValue("Aspire.startup.initializers",null);
      if (initializers == null)
      {
         AppObjects.log("Info: No initializers specified");
         return true;
      }
      AppObjects.log("Info: Calling Initializers");
      // Initializers available
      Vector v = com.ai.common.Tokenizer.tokenize(initializers,",");
      for (Enumeration e=v.elements();e.hasMoreElements();)
      {
         String initializer = (String)e.nextElement();
         AppObjects.info("ApplicationHolder", "Creating initializer for: %s", initializer);
         try {
            Object oInitializer = fact.getObject(initializer,null);
            if (oInitializer instanceof IApplicationInitializer1)
            {
               ((IApplicationInitializer1)oInitializer).initialize(cfg,log,fact,initializer);
            }
            else if (oInitializer instanceof IApplicationInitializer)
            {
               //Assume IApplicationInitializer
               ((IApplicationInitializer)oInitializer).initialize(cfg,log,fact);
            }
            else
            {
            	//It is neither the called initialized is expected to be a singleton
            	//It's intialize invoked by the factory will be sufficient
            	AppObjects.warn("ApplicationHolder", 
            			"The initializer has not implemented IApplicationInitialzer:%s", oInitializer.getClass().getName());
            }
         }
         catch(RequestExecutionException x)
         {
            AppObjects.log("Error: Could not call initializer " + initializer, x);
         }
      }
      return true;
   }

   static private IApplication createApplication(Properties bootStrapProperties)
   {
      IApplication app = null;
      try
      {
              Object obj = BaseFactory.getInstance().getObject(IApplicationConstants.applicationObjName
                  ,bootStrapProperties);
              app = (IApplication)obj;
      }
      catch(RequestExecutionException x)
      {
              app = new CApplication(bootStrapProperties);
      }
      return app;
   }
    static public IApplication getIApplication()
    {
      // If the application is already there just return it
      if (s_application != null)        return s_application;

      // Try to locate it using the default factory object
         // Add ApplicationHolder to the shutdown
         return s_application;
    }
}

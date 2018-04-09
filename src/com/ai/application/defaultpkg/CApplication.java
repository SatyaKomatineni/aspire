/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/default/CApplication.java

package com.ai.application.defaultpkg;

import java.util.Enumeration;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.IConfig;
import com.ai.application.interfaces.IFactory;
import com.ai.application.interfaces.ILog;
import com.ai.application.interfaces.IArgumentListener;
import com.ai.application.interfaces.IAppShutDownListener;
import java.util.Properties;

import com.ai.application.interfaces.IApplication;
import java.util.Vector;

/**
   CApplication - Houses common application level singleton objects.
   This is singleton
   Provides default implementation for IApplication
   Every interface that it is hosting it will try to get the objects for these from the properties files.
   If not found an implementation for these it will use the default implementations available in this module itself.
   It uses factory to locate all these objects by their name via a properties file.
   Factory interface itself is an exception.  It will use the default factory to locate the real factory if available.
 * @see IApplication 
 * @author Satya Komatineni
 * @version unspecified
 */
public class CApplication implements IApplication {
    private static IApplication application = null;
    /**
     * IApplication caches these individual objects for quicker access
     */
    private IConfig m_config = null;
    private ILog     m_log = null;
    private IFactory m_factory = null;

    Vector m_argumentListeners = new Vector();
    Vector m_shutDownListeners = new Vector();
    
   /**
    * Factory maintains it's own instance. It is not cached here
    */
        
   /** Constructs CApplication
    */
    public CApplication() 
    {
//      Properties bootStrapProperties = null;
//      this(bootStrapProperties);
         this(null);
    }
    
    public CApplication(Properties bootStrapProperties)
    {
      init(bootStrapProperties);
    }
    public void init(Properties bootStrapProperties)
    {
      m_config = createIConfig(bootStrapProperties);
      m_log = createILog(m_config);
      m_factory = createIFactory();
    }     
    
    public IConfig getIConfig() { return m_config; }
    public ILog    getILog() { return m_log; }
    public IFactory getIAppFactory() { return m_factory; }
    
    public static IApplication getInstance() {
      if (application == null)
      {
         application = new CApplication();
      }
      return application;
    }
    
    /**
      * returns an instance of IConfig that you can use to obtain configuration information.
       @return IConfig
       @see IConfig
     */
    public IConfig createIConfig(Properties bootStrapProperties) 
    {
       IConfig cfg = null;
       // If a user specified config already exists return it
       if (ApplicationHolder.getUserConfig() != null)
       {
         return ApplicationHolder.getUserConfig();
       }

       // Delegate to the BaseFactory to retrieve the desired configuration
       try
       {
          IFactory fact = BaseFactory.getInstance();
          Object obj = fact.getObject("ApplicationObjects.Config",bootStrapProperties);
          cfg = (IConfig)obj;
       }
       catch(RequestExecutionException x)
       {          
          cfg = new CConfigUsingProperties();
       }          
       return cfg;
    }
    
    public IFactory createIFactory() 
    {
       IFactory fact = BaseFactory.getInstance();
       try 
       {
          Object obj =  fact.getObject("ApplicationObjects.Factory",null); 
          fact = (IFactory)obj;
       }
       catch(RequestExecutionException x)
       {
         fact = new CFactory();
       }          
       return fact;
    }
    
    /**
       @roseuid 36950B4101A2
     */
    public ILog createILog(IConfig cfg)  
    {
      ILog log = null;
      try
      {
         IFactory fact = BaseFactory.getInstance();
          Object obj = fact.getObject("ApplicationObjects.Log",cfg);
          log = (ILog)obj;
      }          
      catch(RequestExecutionException x)
      {
         log = new CLog(cfg);
      }
      return log;
    }                      
    /**
       @roseuid 369B98F40205
     */
    public boolean startup(String[] args)
    {
      Vector argumentListeners = (Vector)(m_argumentListeners.clone());
      boolean argsVerified = true;
      for (Enumeration e = argumentListeners.elements();e.hasMoreElements();)
      {
         boolean argsValid = ((IArgumentListener)(e.nextElement())).verifyArguments(args);
         if (argsValid == false) 
         {
            argsVerified = false;
         }
      }
      return argsVerified;
    }
    
    /**
       @roseuid 369B9BE100CB
     */
    public boolean shutDown()
    {
      if (pollShutDownListeners() == false)
      {
         return false;
      }
      // initiate the actual shut down
      Vector shutDownListeners = (Vector)m_shutDownListeners.clone();
      boolean overallShutDownStatus = true;
      for (Enumeration e = shutDownListeners.elements();e.hasMoreElements();)
      {  
         IAppShutDownListener shutDownListener = (IAppShutDownListener)e.nextElement();
         boolean shutDownStatus = shutDownListener.shutDown();
         if (shutDownStatus == false) 
         {
            BaseSupport.log("error", "Component " + shutDownListener.getOwnerName() + " failed to shutdown");
            overallShutDownStatus = false;
         }
      }
      return overallShutDownStatus;
    }
    private boolean pollShutDownListeners()
    {
      Vector shutDownListeners = (Vector)m_shutDownListeners.clone();
      for (Enumeration e = shutDownListeners.elements();e.hasMoreElements();)
      {
         boolean shutDownApproved = ((IAppShutDownListener)(e.nextElement())).canYouShutDown();
         if (shutDownApproved == false) 
         {
            BaseSupport.log("error","One of the polled components not ready for shutdown");
            return false;
         }
      }
      return true;
    }
    
    /**
       @roseuid 369B99CD03D4
     */
    public void addArgumentListener(IArgumentListener listener)
    {
      m_argumentListeners.addElement(listener);
    }
    
    /**
       @roseuid 369B9A2401B2
     */
    public void removeArgumentListener(IArgumentListener listener)
    {
      m_argumentListeners.removeElement(listener);
    }
    /**
       @roseuid 369A5F92007F
     */
    public void addAppShutDownListener(IAppShutDownListener listener)
    {
      m_shutDownListeners.addElement(listener);
    }
    
    /**
       @roseuid 369A616B00ED
     */
    public void removeAppShutDownListener(IAppShutDownListener listener)
    {
      m_shutDownListeners.removeElement(listener);
    }
    
}

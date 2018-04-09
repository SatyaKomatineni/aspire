/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.application.defaultpkg;
import java.util.Properties;
import java.util.Hashtable;
import com.ai.application.interfaces.*;
import com.ai.application.interfaces.AConfig;
import java.util.*;

import java.io.FileInputStream;

public class CConfigUsingProperties extends AConfig {
        private Hashtable m_cachedBundles = new Hashtable();
        
        public CConfigUsingProperties() 
        {
        }
        
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
      FileInputStream propFileStream = null;
      try 
      {
              Properties bundle = new Properties();
              propFileStream = new FileInputStream(bundleName);
              bundle.load(propFileStream);  
              
              Properties caseInsensitiveBundle = new Properties();
              for(Enumeration e=bundle.propertyNames();e.hasMoreElements();)
              {
                  String key = (String)e.nextElement();
                  caseInsensitiveBundle.put(key.toLowerCase(),bundle.getProperty(key));
              }
              m_cachedBundles.put(bundleName, caseInsensitiveBundle);
              return caseInsensitiveBundle;
       }
       catch(java.io.IOException x)
       {
              throw new ConfigException(bundleName
                              ,"Key not available"
                              ,ConfigException.CONFIG_FILE_NOT_FOUND );
       }              
       finally
       {
         if(propFileStream != null)
         {
            try {propFileStream.close();}
            catch(java.io.IOException x)
            {
               x.printStackTrace();
            }
         }
       }
    }
    
    public Object executeRequest(String requestName, Object args) 
        throws RequestExecutionException
    {
        return this;
    }
        
} 
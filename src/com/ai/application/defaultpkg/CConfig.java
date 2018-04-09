/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/default/CConfig.java

package com.ai.application.defaultpkg;

import com.ai.application.interfaces.IConfig;
import com.ai.application.interfaces.ICreator;
import com.ai.application.utils.AppObjects;

import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.AConfig;

/**
 * CConfig - Default Implementation of IConfig, that uses resource bundles.
 * @see IConfig
 * @see ICreator
 * @author Satya Komatineni
 * @version unspecified
 */
public class CConfig extends AConfig implements ICreator {
    
    private Hashtable m_cachedBundles = new Hashtable();

    /**
     * Constructs a configuration object with a default config file.
     * Application can not start without a config file
     */
    CConfig() {
    }
    /**
     * gets value for this specified key.
     * Looks up in the default configuration file
     * @return value string
     * @param  key string for which value is required
     */
    public String getValue(String key)  throws ConfigException
    {
      return getValueFromSource(ApplicationHolder.getDefaultConfigFile(),key);
    }
    
    /**
     * gets value for this specified key from config file specified.
     * Uses the config file specified for lookup.
     * Uses resource bundles to fullfill the request
     * 
     * @return value string
     * @param  source Name of the properties file to get the value from.
     * @param  key string for which value is required
     */
    public String getValueFromSource(String source, String key) throws ConfigException
    {
        try 
        {
         PropertyResourceBundle thisBundle = getBundle(source);
         return thisBundle.getString(key);
        }
        catch(java.util.MissingResourceException x)
        {
//                System.out.println(x);
                throw new ConfigException(source,key,ConfigException.CONFIG_KEY_NOT_FOUND);
        }
    }
    private PropertyResourceBundle getBundle( String bundleName )
        throws ConfigException
    {
      Object obj = m_cachedBundles.get(bundleName);
      if (obj != null)
      {
         return (PropertyResourceBundle)obj;
      }
      // Bundle not found in cache
      // Go to properties files to load it
      PropertyResourceBundle bundle = (PropertyResourceBundle)(ResourceBundle.getBundle(bundleName));
      if (bundle != null)
      {
        // retrieved a properties file 
        //Cache the bundle for future use
        m_cachedBundles.put(bundleName, bundle);
        return bundle;
      }
      throw new ConfigException(bundleName,"Key note available", ConfigException.CONFIG_FILE_NOT_FOUND);
    }
    
    /**
       @roseuid 36950B410350
     */
    public Object executeRequest(String requestName, Object args) 
    {
      return new CConfig();
    }
}

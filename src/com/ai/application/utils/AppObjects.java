/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/utils/AppObjects.java

package com.ai.application.utils;

import com.ai.application.defaultpkg.ApplicationHolder;
import com.ai.application.interfaces.*;
import com.ai.aspire.authentication.pls.IPLSDataSetPersistence;

import java.util.*;
import com.ai.common.*;

/**
 *
 * 12/4/15
 * *************
 * See the dated notes below
 * info and warn etc are overloaded
 * Sometimes this causes errors
 * For now no changes.
 * 
 * 12/4/15
 * **************
 * Make a note on how to use these functions correctly
 *
 */
public class AppObjects {
    public static final String LOG_SECURITY_S = "Security";
    public static final String LOG_INFO_S = "Info";
    public static final String LOG_ERROR_S = "Error";
    public static final String LOG_WARN_S = "Warning";
    public static final String LOG_CRITICAL_S = "Fatal";

    public static final int LOG_SECURITY = 0;
    public static final int LOG_INFO = 1;
    public static final int LOG_WARN = 2;
    public static final int LOG_ERROR = 3;
    public static final int LOG_CRITICAL = 4;

    // configuration details
    private static IConfig  m_config = null;
    /**
       @roseuid 369FB41B013F
     */
    public static IConfig getIConfig() {
      if(m_config != null) return m_config;
      m_config = ApplicationHolder.getIApplication().getIConfig();
      return m_config;
    }

    public static String getValue(String key)
      throws com.ai.application.interfaces.ConfigException
    {
      return getIConfig().getValue(key);
    }

    public static String getValue(String key, String defaultValue)
    {
      return getIConfig().getValue(key, defaultValue);
    }

    /**
     * Primary intent
     *
     * This function is used from Factory methods.
     * This function allows to parameterize the arguments
     *
     * Look for
     * 1. Look in the hashmap for key, if found return it
     * 2. Next look for requestname + ".key" in the database
     *
     * Caveat:
     * Make sure you pass your keys are passed in lower case
     *
     * @param key
     * @param requestName
     * @param argumentMap
     * @return
     */
    public static String getValueForRequestUsingSubstitution(String key, String requestName, Map argumentMap)
      throws com.ai.application.interfaces.ConfigException
    {
       String value = getValue(requestName + "." + key);
       if (value.indexOf('{') == -1) return value;
       String newValue = SubstitutorUtils.generalSubstitute(value,argumentMap);
       return newValue;
    }

    public static String getValueUsingSubstitution(String key, IDictionary argumentMap)
      throws com.ai.application.interfaces.ConfigException
    {
       String value = getValue(key);
       if (value.indexOf('{') == -1)
       {
        //no substitutions
         return value;
       }
       //substitutions indicated
       String newValue = SubstitutorUtils.generalSubstitute(value,argumentMap);
       return newValue;
    }

    public static String getValueUsingSubstitution(String key, IDictionary argumentMap, String defaultVal)
    {
       String value = getValue(key,defaultVal);
       if (value.indexOf('{') == -1)
       {
        //no substitutions
         return value;
       }
       //substitutions indicated
       String newValue = SubstitutorUtils.generalSubstitute(value,argumentMap);
       return newValue;
    }
    /**
     * *****************Factory Methods************************
     * Factory related utility methods
     * ********************************************************
     */
    public static IFactory getIFactory() {
      return ApplicationHolder.getIApplication().getIAppFactory();
    }

    public static Object getObject(String objName, Object args)
      throws com.ai.application.interfaces.RequestExecutionException
    {
      return getIFactory().getObject(objName, args);
    }

    public static Object getObjectAbsolute(String objName, Object args)
      throws com.ai.application.interfaces.RequestExecutionException
    {
       Object o = getIFactory();
       if (o instanceof IFactory2)
       {
           return ((IFactory2)(o)).getObjectAbsolute(objName,args);
       }

       throw new RequestExecutionException("Error: IFactory2 is required for getObjectAbsolute(). Use FilterEnabledFactory2 in the configuration file");
    }

    public static Object getObject(String objName, Object args, Object defaultObject)
    {
      try
      {
         return getIFactory().getObject(objName, args);
      }
      catch( com.ai.application.interfaces.RequestExecutionException x)
      {
         AppObjects.log("warn: Could not obtain object via factory", x);
         return defaultObject;
      }
    }

    /*
     * Given an interface return its implementation
     * whether it is a singleton or not is determined by
     * the implementation.
     * 
     * Throws a run time exception
     * Best suited for static variable initialization
     */
    public static Object getImplementation(String interfaceName)
    {
		try
		{
			return AppObjects.getObject(interfaceName,null);
		}
		catch(RequestExecutionException x)
		{
			throw new RuntimeException(
					"Could not instantiate an implementation for:" + interfaceName
					,x);
		}
    }
    /**
     * *****************Logging Methods************************
     * Logging related utility methods
     * ********************************************************
     */
    public static ILog getILog() {
      return ApplicationHolder.getIApplication().getILog();
    }

    /**
       @roseuid 369FB455002A
     */
    public static IApplication getIApp() {
      return ApplicationHolder.getIApplication();
    }
    public static void log(java.lang.Throwable x)
    {
        log(LOG_INFO,x);
    }
    public static void log(int logType, java.lang.Throwable x)
    {
        AppObjects.getILog().log(x);
    }
    public static void log(String logType, String logMessage )
    {
      AppObjects.getILog().log(logMessage, logType );
    }
    public static void log(String logMessage )
    {
      AppObjects.getILog().log(logMessage, AppObjects.LOG_INFO_S );
    }
    /**
     * Log an exception based on a final cause
     * This final cause could either be selected or excluded using properties set
     *
     * 11/27/15
     * *****************
     * the info, warn etc are overloaded.
     * May not be the wisest thing to have done.
     * 
     * info(object, message) - Just print the string
     * info(object, message, additional arg) - use string formatting
     * 
     * Sometimes Intent is to use the second but end up using the first form
     * instead. that produces spurious results
     * 
     * What shoudl be done?
     * *********************
     * 1. Keep this in mind for now
     * 2. Make sure the first argument of info(this...) all the time
     * 3. May be, likely, rename the older methods 
     * 4. May be like warnp(....) for warn primitive or previous
     * 
     * More thoughts
     * ***************
     * 1. if info gets called for infop, then it is ok
     * 2. but if infop gets called for info, then 
     * 		a) the message becomes classname
     * 		b) the argument becomes message
     * 3. In otherwords it gets messed up
     * 
     * Is it ok to rename the older info to infop?
     * *******************************************
     * 1. we loose efficiency because of an extra string formating
     * 2. if we leave it alone occasional errors
     * 
     * ***************************************************
     * Make sure we know the first argument is classname to these methods
     * ***************************************************
     * 
     * Final decision as of 11/27/15
     * ***********************************
     * Considering efficiency leave it alone for now
     * 
     * Example 7/6/16
     * ***********************************
     * Ex: AppObjects.trace(this,"Executing : %1s", statementString );
     * 
     */
    public static void log(String finalCause, Throwable t)
    {
      AppObjects.getILog().log(finalCause,t);
    }

    public static void info(Object object, String message)
    {
       log(AppObjects.LOG_INFO_S,constructLogMessage("Info:",object, message));
    }

    //Ex: AppObjects.trace(this,"Executing : %1s", statementString );
    public static void trace(Object object, String message)
    {
       log(AppObjects.LOG_INFO_S,constructLogMessage("Trace:",object, message));
    }

    public static void warn(Object object, String message)
    {
       log(AppObjects.LOG_WARN_S,constructLogMessage("Warn:",object, message));
    }

    public static void error(Object object, String message)
    {
       log(AppObjects.LOG_ERROR_S,constructLogMessage("Error:",object, message));
    }

    private static String constructLogMessage(String prefix, Object object, String message)
    {
       String prefix2 = null;
       if (object instanceof String)
       {
          prefix2 = (String)object;
       }
       else
       {
          prefix2 = object.getClass().getName() + ":";
       }
       return prefix + prefix2 + message;
    }
    
    
    /**
     * Optimized logging support
     */
    public static boolean isItNecessaryToLog(int logType)
    {
        ILog ilog = AppObjects.getILog();
        if (!(ilog instanceof ILog1))
        {
        	//it is not an instance of ILog1
        	return true;
        }
        //it is an ILog1
        ILog1 ilog1 = (ILog1)ilog;
        return ilog1.isItNecessaryToLog(logType);
    }
    public static void info(Object object, String formatMessage, Object ...args)
    {
       if (isItNecessaryToLog(AppObjects.LOG_INFO))
       {
    	   String message = String.format(formatMessage,args);
    	   log(AppObjects.LOG_INFO_S,constructLogMessage("Info:",object, message));
       }
    }

    public static void secure(Object object, String formatMessage, Object ...args)
    {
       if (isItNecessaryToLog(AppObjects.LOG_SECURITY))
       {
    	   String message = String.format(formatMessage,args);
    	   log(AppObjects.LOG_SECURITY_S,constructLogMessage("Secure:",object, message));
       }
    }
    public static void trace(Object object, String formatMessage, Object ...args)
    {
        if (isItNecessaryToLog(AppObjects.LOG_INFO))
        {
     	   String message = String.format(formatMessage,args);
     	   log(AppObjects.LOG_INFO_S,constructLogMessage("Trace:",object, message));
        }
    }

    public static void warn(Object object, String formatMessage, Object ...args)
    {
        if (isItNecessaryToLog(AppObjects.LOG_WARN))
        {
      	   String message = String.format(formatMessage,args);
      	   log(AppObjects.LOG_WARN_S,constructLogMessage("Warn:",object, message));
        }
    }

    public static void error(Object object, String formatMessage, Object ...args)
    {
        if (isItNecessaryToLog(AppObjects.LOG_ERROR))
        {
	  	   String message = String.format(formatMessage,args);
	       log(AppObjects.LOG_ERROR_S,constructLogMessage("Error:",object, message));
        }
    }
}//eof-class

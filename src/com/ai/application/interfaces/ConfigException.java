/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.application.interfaces;
import com.ai.common.CommonException;
                
public class ConfigException extends CommonException 
{
        
        public static final String     CONFIG_FILE_NOT_FOUND 
                = "Configuration file not found";
        public static final String      CONFIG_KEY_NOT_FOUND
                = "Configuration key not found in any config file";
                                
        public ConfigException(String msg) 
        {
                this(msg,null);
        }
        public ConfigException(String msg, Throwable t )
        {
            super(msg);
            setChildException(t);
        }
        
        public ConfigException(final String file
                              ,final String key 
                              ,final String msg )
         {
            this("Key " + key + " not found in file " + file );
         }                              
} 

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.application.interfaces;

public abstract class AConfig implements IConfig
{

    public String getValue(String key, String defaultValue)
   {
      try {  return getValue(key); }
      catch (ConfigException x){ return defaultValue; }
   }
} 

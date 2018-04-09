/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.extensions.xmlconfig;

public class DefaultConfigSectionProcessorOutput implements IConfigSectionProcessorOutput
{
   public void setValue(String key, String value)
   {
      System.out.println(key + "=" + value);
   }
   public void println(String line)
   {
      System.out.println(line);
   }
   public void close()
   {
   }
}   

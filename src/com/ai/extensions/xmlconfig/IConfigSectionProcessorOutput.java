/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.extensions.xmlconfig;

public interface IConfigSectionProcessorOutput {
   public void setValue(String key, String value);
   public void println(String line);
   public void close();
} 

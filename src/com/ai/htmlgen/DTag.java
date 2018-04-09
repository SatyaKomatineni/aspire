/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

/**
 * Default implementation for ITag
 *
 */
public class DTag implements ITag 
{

   private String m_name;
   private String m_defAttribute;

   public DTag(String name, String attribute) 
   {
      m_name = name;
      m_defAttribute = attribute;
   }

   public   String getDefaultAttributeValue()
   {
      return m_defAttribute;
   }
   public    String getTagName()
   {
      return m_name;
   }
} 

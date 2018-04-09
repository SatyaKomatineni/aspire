/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

/**
 * Representing the idea of a tag
 */
public interface ITag 
{
   static final String TAGS_REPLACE_TAG = "REPLACE_BGN";
   static final String TAGS_REPLACE_END_TAG = "REPLACE_END";
   static final String TAGS_END_LOOP_TAG = "END_LOOP";
   static final String TAGS_BGN_LOOP_TAG = "BGN_LOOP";
   static final String TAGS_BGN_IF_TAG = "BGN_IF";
   static final String TAGS_END_IF_TAG = "END_IF";
   
   /**
    * Typically what follows the tag name
    * Tag structure
    * ex: <!--RLF_TAG bgn_name def_attribute givenname -->
    */
   String getDefaultAttributeValue();

   
   /**
    * Typically what follows the tag name
    * Tag structure
    * ex: <!--RLF_TAG bgn_name def_attribute givenname -->
    */
   String getTagName();
} 

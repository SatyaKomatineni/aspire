/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.*;

/**
 * Derived classes
 * **********************
 * SimpleDataRowDictionary
 * IStringDictionary
 * IUpdatableDictionary 
 *
 */
public interface IDictionary 
{
   /**
    * returns null if the key is not found
    */
   public Object get(Object key);
   public void getKeys(List list);
   public void addChild(IDictionary childDictionary);
   public void removeChild(IDictionary childDictionary);
   
} 
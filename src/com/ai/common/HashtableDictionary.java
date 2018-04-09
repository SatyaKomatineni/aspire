/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.*;

public class HashtableDictionary extends DDictionary
{
   private Hashtable m_ht;
   public HashtableDictionary(Hashtable t)
   {
      m_ht = t;
   }
   public Object internalGet(Object key)
   {
      return m_ht.get(key);
   }
} 
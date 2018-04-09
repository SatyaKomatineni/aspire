/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import java.util.*;
import com.ai.common.*;

public class HDSDictionary extends DDictionary
{
   private ihds m_hds;
   public HDSDictionary(ihds t)
   {
      m_hds = t;
   }
   public Object internalGet(Object key)
   {
      return m_hds.getValue((String)key);
   }
}
/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import java.util.*;
import com.ai.common.*;

public class FormHandlerDictionary extends DDictionary
{
   private IFormHandler m_fh;
   public FormHandlerDictionary(IFormHandler t)
   {
      m_fh = t;
   }
   public Object internalGet(Object key)
   {
      return m_fh.getValue((String)key);
   }
}
/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;
import java.util.*;
import com.ai.common.*;

public class StringMetaData implements IMetaData
{
  Vector m_fields;
  public StringMetaData(String inStr, String separator)
  {
         m_fields = com.ai.common.Tokenizer.tokenize(inStr, separator );
  }
  public int getColumnCount()
  {
   return m_fields.size();
  }
  public IIterator getIterator()
  {   return new VectorIterator(m_fields);
  }
  public int getIndex(final String attributeName)
              throws FieldNameNotFoundException
  {
      int i = m_fields.indexOf(attributeName);
      if (i == -1)
      {
         throw new FieldNameNotFoundException();
      }
      return i;
  }
   
} 
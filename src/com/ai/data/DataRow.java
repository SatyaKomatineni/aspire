/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.data;
import java.util.*;

public class DataRow implements IDataRow
{
  Vector    m_tokenizedString;
  IMetaData m_metaData;
    
  public DataRow(IMetaData inMetaData, String inString, String inSeparators)
  {
      m_metaData = inMetaData;
      m_tokenizedString = com.ai.common.Tokenizer.tokenize(inString, inSeparators);
  }
  public String getValue(int inIndex)
  {
    return returnValue((String)m_tokenizedString.elementAt(inIndex));
  }
  private String returnValue(String value)
  {
    if (value.equals("none"))
    {
      return "";
    }
    return value;
  }
  
  public String getValue(String columnName, String defaultValue)
  {
      try 
      { 
         return returnValue((String)m_tokenizedString.elementAt(m_metaData.getIndex(columnName)));
      }         
      catch(com.ai.data.FieldNameNotFoundException x)
      {  
         return defaultValue;
      }
  }
  public String getValue(String columnName) throws com.ai.data.FieldNameNotFoundException
  {
         return returnValue((String)m_tokenizedString.elementAt(m_metaData.getIndex(columnName)));
  }         
  public IIterator getColumnNamesIterator()
  {
   return m_metaData.getIterator();
  }
} 

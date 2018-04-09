/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.data;
import java.util.*;

import com.ai.application.utils.AppObjects;

/**
 * 
 * Represents a data row where column values are maintained in a list.
 *
 *@see com.ai.data.IDataRow
 *@see com.ai.db.RSDataRow
 *@see com.ai.data.DataRow
 */
public class ListDataRow implements IDataRow
{
  List 		m_columnValuesList = new ArrayList();
  IMetaData m_metaData;
    
  public ListDataRow(IMetaData inMetaData, List columnValuesList)
  {
      m_metaData = inMetaData;
      m_columnValuesList = columnValuesList;
  }
  public String getValue(int inIndex)
  {
    return 
	returnValue(m_columnValuesList.get(inIndex));
  }
  private String returnValue(Object value)
  {
    if (value == NullObject.self)
    {
      return "";
    }
    return (String)value;
  }
  
  public String getValue(String columnName, String defaultValue)
  {
      try 
      { 
         return returnValue(m_columnValuesList.get(m_metaData.getIndex(columnName)));
      }         
      catch(com.ai.data.FieldNameNotFoundException x)
      {  
         return defaultValue;
      }
  }
  public String getValue(String columnName) throws com.ai.data.FieldNameNotFoundException
  {
         return returnValue(m_columnValuesList.get(m_metaData.getIndex(columnName)));
  }         
  public IIterator getColumnNamesIterator()
  {
   return m_metaData.getIterator();
  }
  
  public String toString()
  {
  	try
	{
  		return DataUtils.toStringDataRow(this);
	}
  	catch(DataException x)
	{
  		String msg = "Error: Unable to convert an IDataRow to a string. Received a data excetpion. See the log file for the trace";
  		AppObjects.log(msg,x);
  		return msg;
	}
  }
} 

package com.ai.data;
import java.util.*;
/**
 * This class is not complete yet
 * Don't use it yet
 */
public class CachedDataRow implements IDataRow 
{
   Vector m_values = new Vector();
   IMetaData m_metaData;
   
   public CachedDataRow(IDataRow dataRow, IMetaData metaData) 
   {
      // for each ro
      m_metaData = metaData;
   }
   /**
    * Return a string value for the specified index
    * The index is zero based
    */
  public String getValue(int inIndex)
  {
   return "";
  }

   /**
    * Return a string value for the specified column name 
    * If the name not found return the default value
    * It is recommended that the columnname is case insensitive
    * Internally the column name is converted to an index
    */
  public String getValue(String columnName, String defaultValue) { return "";}
  
   /**
    * Return a string value for the specified column name
    * Column name is case insensitive (recommended) 
    */
  public String getValue(String columnName) 
         throws com.ai.data.FieldNameNotFoundException { return ""; }
  
   /**
    * Return column names
    * See the IIterator interface to understand how to access
    * these column names
    */
  public IIterator getColumnNamesIterator()
  {
   return null;
  }
} 
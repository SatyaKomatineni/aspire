/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.data;
import java.util.*;

/**
 * An abstract representation of one row of an IDataCollection
 */
public interface IDataRow
{
   /**
    * Return a string value for the specified index
    * The index is zero based
    */
  public String getValue(int inIndex);

   /**
    * Return a string value for the specified column name 
    * If the name not found return the default value
    * It is recommended that the columnname is case insensitive
    * Internally the column name is converted to an index
    */
  public String getValue(String columnName, String defaultValue);
  
   /**
    * Return a string value for the specified column name
    * Column name is case insensitive (recommended) 
    */
  public String getValue(String columnName) 
         throws com.ai.data.FieldNameNotFoundException;
  
   /**
    * Return column names
    * See the IIterator interface to understand how to access
    * these column names
    */
  public IIterator getColumnNamesIterator();
} 

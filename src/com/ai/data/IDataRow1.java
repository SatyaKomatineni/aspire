/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.data;
import java.util.*;

/**
 * Extends IDataRow
 * Adds the ability to retrieve values as objects
 * That will keep the object fidelity
 * This is first introduced to read blobs
 */
public interface IDataRow1 extends IDataRow
{
   /**
    * Return a string value for the specified index
    * The index is zero based
    */
  public Object getValueAsObject(int inIndex)
  	throws com.ai.data.DataException;

   /**
    * Return a string value for the specified column name
    * Column name is case insensitive (recommended) 
    */
  public Object getValueAsObject(String columnName) 
         throws com.ai.data.DataException;
  
} 

package com.ai.htmlgen;
import com.ai.data.*;
import com.ai.application.interfaces.*;

/**
 * Provides extensibility for Generic table handlers
 */
 
public interface IColumnFilter extends ISingleThreaded
{
   public void setRow(IDataRow row, int rowNum);
   public String getValue(String key);
} 

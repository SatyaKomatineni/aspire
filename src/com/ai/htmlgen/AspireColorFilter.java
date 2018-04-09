/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

import com.ai.htmlgen.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import com.ai.data.*;
/**
 * request.Aspire.defaultColumnFilter.className=com.ai.htmlgen.AspireColorFilter
 * request.Aspire.defaultColumnFilter.oddColor=<your_color_string>[aspire_odd]
 * request.Aspire.defaultColumnFilter.evenColr=<your_color_string>[aspire_even]
 * request.Aspire.defaultColumnFilter.rows=2
 *
 */
public class AspireColorFilter implements 
                        IColumnFilter
                        , ICreator
   
{
   static private String ASPIRE_COLOR="aspire_color";
   static private String ASPIRE_ROW_NUM="aspire_rownum";
   
   private String m_oddColor = "aspire_odd";
   private String m_evenColor = "aspire_even";
   private int m_curRow = 0;
   private int m_numberOfRows = 2;

   boolean m_bCurModeEven = true;
   
   // from creator
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
      {
         IConfig cfg = AppObjects.getIConfig();

         m_oddColor = cfg.getValue(requestName + ".oddColor","aspire_odd");
         m_evenColor = cfg.getValue(requestName + ".evenColor","aspire_even");
         String rowsString  = cfg.getValue(requestName + ".rows","1");
         m_numberOfRows = Integer.parseInt(rowsString);                                                                           
         return this;
      }

   public void init(String oddColor, String evenColor, int numberOfRows)
   {
      m_oddColor = oddColor;
      m_evenColor = evenColor;
      m_numberOfRows = numberOfRows;
   }
   // for calculator
   public void setRow(IDataRow row, int rowNum)
   {
      m_curRow = rowNum;
      int rem = (m_curRow-1)%m_numberOfRows;
      if (rem == 0) 
      {
         // Time to flip
         m_bCurModeEven = (m_bCurModeEven) ? false : true;
      }
      else
      {
         //continue
      }
      
   }
   public String getValue(String key)
   {
      String lowercaseKey = key.toLowerCase();
      if (lowercaseKey.equals(this.ASPIRE_ROW_NUM))
      {
         return Integer.toString(m_curRow);
      }
      else if (lowercaseKey.equals(this.ASPIRE_COLOR))
      {
         return (m_bCurModeEven) ? m_evenColor : m_oddColor;
      }
      else
      {
         return null;
      }
   }
}                

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.*;
import java.text.SimpleDateFormat;

public class AICalendar 
{
  static private  AICalendar s_AICalendar = new AICalendar();
  private Calendar m_calendar = Calendar.getInstance();
  private SimpleDateFormat m_sdf = new SimpleDateFormat();

  protected AICalendar() 
  {
  }
  static public AICalendar getInstance()
  {
   return s_AICalendar;
  }

  static public String getCurTimeString()
  {
      SimpleDateFormat sdf = new SimpleDateFormat();
      return sdf.format(new Date(System.currentTimeMillis()));
  }
  static public String getCurTimeStringUsingAFormatString(String format)
  {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(new Date(System.currentTimeMillis()));
  }
  public Date getTime()
  {
   return m_calendar.getTime();
  }
  static public String formatTimeInMilliSeconds(long time)
  {
      
      SimpleDateFormat sdf = new SimpleDateFormat();
      Date date = new Date(time);
      return sdf.format(date);
  }
} 


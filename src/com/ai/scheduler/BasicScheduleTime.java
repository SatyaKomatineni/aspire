/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.scheduler;

public class BasicScheduleTime implements IScheduleTime {

  private long m_timeInterval;
  
  public BasicScheduleTime(long timeInterval) 
  {
      m_timeInterval = timeInterval;
  }
  long getTimeInterval()
  {
      return m_timeInterval;
  }
} 

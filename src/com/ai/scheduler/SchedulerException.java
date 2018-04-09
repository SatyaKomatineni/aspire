/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.scheduler;
import com.ai.common.*;

public class SchedulerException extends CommonException
{
  public SchedulerException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public SchedulerException(String msg )
  {
   this(msg,null);
  }
}             
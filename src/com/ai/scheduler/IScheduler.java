/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.scheduler;

/**
 * Schedule the given task with the given time constraints
 */
public interface IScheduler 
{
   // A symbolic name for retrieving the singleton that implements
   // the IScheduler interface.
   public static final String GET_SCHEDULER_REQUEST = "AppObjects.scheduler";
   
   public void schedule( IScheduleTask task, IScheduleTime scheduleTime )
               throws SchedulerException;
} 
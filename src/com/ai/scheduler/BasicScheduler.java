/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.scheduler;

import com.ai.application.interfaces.*;
import com.ai.common.*;
// import com.ai.application.defaultpkg.*;
import com.ai.application.utils.AppObjects;
import java.util.Vector;
import java.util.Enumeration;

/**
 * A singleton scheduler that schedules a task at specified durations.
 * Holds a list of schedule tasks and their time constraints.
 * Runs a 1 minute timer
 */
public class BasicScheduler implements IScheduler, ICreator
{
   Vector m_taskVector = new Vector();
  
  //* Many ways to construct the scheduler object
  //* Through a regular constructor
  //* Or through a factory
  //* Both creation methods should call the init() method
  public BasicScheduler() 
  {
   init();
  }
  public void init()
  {
  }
  public Object executeRequest(String requestName, Object args)
     throws RequestExecutionException
  {
      //      init();
      // init() already called because of default constructor
      startScheduler();
      return this;
  }   
  
  /**
   * Start a daemon thread for timer ticks.
   */
  public void startScheduler()
  {
         AppObjects.log("sc: Scheduler started at :" + AICalendar.getCurTimeString());
        // get the interval for the timer
        String timerIntervalInMilliSec = AppObjects.getIConfig().getValue("AppObjects.scheduler.timer_interval_in_milli_seconds","60000");
        long lTimerIntervalInMilliSec = Long.parseLong(timerIntervalInMilliSec);
        SchedulerThread schedulerThread = new SchedulerThread(lTimerIntervalInMilliSec);
        schedulerThread.start();    
  }
  
  /**
   * Schedule the task with the specified time constraints
   */
  public void schedule( IScheduleTask task, IScheduleTime scheduleTime )
               throws SchedulerException
  {
      if (!(scheduleTime instanceof BasicScheduleTime) )
      {
         // not a recognizable timing spec
         throw new SchedulerException("Wrong type of timing constraint");
      }
      // Could proceed
      m_taskVector.addElement(new SchedulableItem(task,(BasicScheduleTime)scheduleTime));      
  }
  
  /**
   * check and spawn tasks if they need to be based on the time constraints.
   */
  private void spawnTasks()
  {
      AppObjects.log("sc: timer tick detected : " + AICalendar.getInstance().getCurTimeString());
      // get a copy of the task vector
      Vector taskVector = (Vector)m_taskVector.clone();
      for(Enumeration e=taskVector.elements();e.hasMoreElements();)
      {
         Object obj = e.nextElement();
         SchedulableItem schedulableItem = (SchedulableItem)obj;
         schedulableItem.m_curCount++;
         if (schedulableItem.m_curCount >= schedulableItem.m_intervalCount)
         {
            schedulableItem.m_curCount = 0;
            spawnTask(schedulableItem);
         }
      }
  }

  private void spawnTask(SchedulableItem item)
  {
      SchedulableTaskThread taskThread = new SchedulableTaskThread(item.m_scheduleTask);
      taskThread.start();
  }

//***********************************
//* private class SchedulerThread
//***********************************
   class SchedulerThread extends Thread
   {
      private long m_sleepInterval;
      public SchedulerThread( long sleepInterval)
      {
         setDaemon(true);
         m_sleepInterval = sleepInterval;
      }
      public void run()
      {
         try
         {
             while(true)
             {
                sleep(m_sleepInterval);
                spawnTasks();
             }
         }
         catch (InterruptedException x)
         {
            AppObjects.log("sc: Scheduler interrupted and shutting down");
            AppObjects.log(x);
         }             
      }
   }
//***********************************
//* private class SchedulableItem
//***********************************
class SchedulableItem
{
   public IScheduleTask m_scheduleTask;
   public BasicScheduleTime m_timeInterval;
   public long m_curCount;
   public long m_intervalCount;

   SchedulableItem( IScheduleTask task, BasicScheduleTime timeInterval )
   {
      m_scheduleTask = task;
      m_timeInterval = timeInterval;
      m_curCount = 0;
      m_intervalCount = timeInterval.getTimeInterval();
   }
}
//***********************************
//* private class SchedulableTaskThread
//***********************************
class SchedulableTaskThread extends Thread
{
   public IScheduleTask m_scheduleTask;

   SchedulableTaskThread( IScheduleTask task )
   {
      m_scheduleTask = task;
//      setDaemon(true);
   }
   public void run()
   {
      m_scheduleTask.execute();
   }
}
//***********************************
//* End of the main public class
//***********************************
} 

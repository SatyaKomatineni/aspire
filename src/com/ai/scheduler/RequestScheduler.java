package com.ai.scheduler;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;

public class RequestScheduler implements IApplicationInitializer
                                    , IInitializable
                                    , ISingleThreaded
                                    , IScheduleTask
{
   public String m_initializerName = null;
   public String m_requestNameToSchedule = null;
   public int m_timerTicks = 0;

   //Implementing the IApplicationInitializer contract
   public boolean initialize(IConfig cfg, ILog log, IFactory factory)
   {
      try
      {
         IScheduler scheduler =
               (IScheduler)factory.getObject(IScheduler.GET_SCHEDULER_REQUEST,null);

         scheduler.schedule(this,new com.ai.scheduler.BasicScheduleTime(m_timerTicks));
         return true;
      }
      catch(com.ai.application.interfaces.RequestExecutionException x)
      {
         AppObjects.log("Error: Initialization exception for initializer:" + m_initializerName, x);
         return false;
      }
      catch(SchedulerException x)
      {
         AppObjects.log("Error: Scheduler exception for:" + m_initializerName, x);
         return false;

      }
   }
   //Implementing IInitializable to know the context and cache the request name to schedule
   public void initialize(String requestName)
   {
      try
      {
         m_initializerName = requestName;
       m_requestNameToSchedule = AppObjects.getValue(requestName + ".requestNameToSchedule");
       String timerTicks = AppObjects.getValue(requestName + ".howmanyTimerTicks");
       m_timerTicks = Integer.parseInt(timerTicks);
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         AppObjects.log("Error:Could not initialize the task for initializer:" + requestName, x);
      }
   }
   //Implementing IScheduleTask
   //Callback from the scheduler
   public boolean execute()
   {
      try
      {
         AppObjects.getObject(m_requestNameToSchedule,null);
         return true;
      }
      catch(RequestExecutionException x)
      {
        AppObjects.log("Error:Error executing " + m_requestNameToSchedule,x);
        return false;
      }
   }
}
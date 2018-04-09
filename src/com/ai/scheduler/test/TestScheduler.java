/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.scheduler.test;
import com.ai.application.defaultpkg.ApplicationHolder;
import com.ai.scheduler.*;
import com.ai.common.AICalendar;

public class TestScheduler {

  public static void main(String[] args)
  {
        try {
                ApplicationHolder.initApplication(
                        "g:\\cb\\com\\ai\\application\\test\\TestAppConfig_satya.properties",null);
                System.out.println("Test begins");
                
                BasicScheduler basicScheduler = new BasicScheduler();
                basicScheduler.schedule(new PrintoutScheduleTask()
                                       , new BasicScheduleTime(3));
                basicScheduler.startScheduler();
                
                System.out.println("Wait for the timer tick messages every minute");
//                wait(300000);
                Thread.currentThread().sleep(60000);
                System.out.println("Test completed");
        }
        catch(Exception x)
        {
                x.printStackTrace();
        }
  }
}  

class PrintoutScheduleTask implements IScheduleTask
{
   public boolean execute()
   {
      System.out.println("Inside the print scheduler at : " + AICalendar.getInstance().getCurTimeString());
      return true;
   }
}

/*
 * Created on Dec 20, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.cp4;

import com.ai.application.utils.AppObjects;
import com.ai.common.AICalendar;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
//*************************************************************************
//* ConnectionPoolCleanUptask
//*************************************************************************
class ConnectionPoolCleanupTask implements com.ai.scheduler.IScheduleTask
{
   private SingleDataSourceConnectionPool m_pool = null;
   public ConnectionPoolCleanupTask(SingleDataSourceConnectionPool pool)
   {
   	this.m_pool = pool;
   }
   public boolean execute()
   {
      AppObjects.info("cp: Connection view before cleanup at: %1s",AICalendar.getCurTimeString());
      m_pool.printConnections();
      m_pool.testAndCleanupConnections();
      AppObjects.info("cp: Connection view after cleanup: %1s", AICalendar.getCurTimeString());
      m_pool.printConnections();
      return true;        
   }
}

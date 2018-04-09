/*
 * Created on Nov 30, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.application.defaultpkg;

import com.ai.application.interfaces.IConfig;
import com.ai.application.utils.AppObjects;
import com.ai.common.AICalendar;

/**
 * @author Satya
 * Reasons
 * ******************
 * 1. to override the gettimestring
 */
public class CLog3 extends CLog2 
{
	private String logpattern = null;
    public CLog3()
    {
    	super();
    }
    

    private void clog3Init(IConfig config)
    {
    	logpattern = config.getValue("Logging.timepattern","EEE MMM dd  HH:mm:ss:SSS yy");
    }
    public CLog3(IConfig config)
    {
    	super(config);
    	clog3Init(config);
    }
    
    public Object executeRequest(String requestName, Object args)
    {
      if (args != null)
      {
         IConfig inConfig = (IConfig)args;
         init(inConfig);
         clog3Init(inConfig);
      }
      else
      {
         init(null);
         clog3Init(null);
      }
      return this;
    }
    
    protected String getTimeString()
    {
    	int hc = Thread.currentThread().hashCode();
    	String timestring = null;
    	if (logpattern == null)
    	{
    		timestring = AICalendar.getCurTimeString();
    	}
    	else
   		{
   			timestring = AICalendar.getCurTimeStringUsingAFormatString(logpattern);
   		}
    	return timestring + ":tid:" + hc;
    }//eof-function
    
}//eof-class

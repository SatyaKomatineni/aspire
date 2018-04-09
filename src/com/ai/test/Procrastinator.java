/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import java.io.*;
import com.ai.common.Tokenizer;

public class Procrastinator implements ICreator { 

        public Object executeRequest(String requestName, Object args)
                throws RequestExecutionException
        {
                        java.io.PrintWriter fileWriter=null;
                try 
                {       
                        String filename;
                        String appendMode;
                        String timePeriod;
                        int timePeriodInMilliSecs = 0;
                        try
                        {
                                filename = AppObjects.getIConfig().getValue(requestName + ".filename");
                                appendMode = AppObjects.getValue(requestName + ".openmode");
                                timePeriod = AppObjects.getIConfig().getValue(requestName + ".time","60");
                                timePeriodInMilliSecs = Integer.parseInt(timePeriod) * 1000;
                        }
                        catch(ConfigException x)
                        {
                                AppObjects.log(x);
                                AppObjects.log("Error", "File name not in config file");
                                throw new RequestExecutionException (
                                        RequestExecutionException.LOADED_CLASS_CAN_NOT_EXECUTE_REQUEST );
                        }
                        if (appendMode.equals("append"))
                        {
                            fileWriter = new PrintWriter( new FileOutputStream(filename,true) );
                        }
                        else
                        {
                            fileWriter = new PrintWriter( new FileOutputStream(filename) );
                        }                                
                        fileWriter.println("************************New record******************");
                     if (args != null)
                     {
                        // arguments passed
                        // The assumptions is that this is a vector of or a hashtable 
                        if (args instanceof Vector)
                        {
                           fileWriter.println("Vector passed");
                        }
                        else
                        {
                           Hashtable params = (Hashtable)args;
                            for (Enumeration e=params.keys();e.hasMoreElements();)
                            {
                              String key = (String)e.nextElement();
                              fileWriter.println(key + ":" + params.get(key) );
                            }
                        }
                     }   
                  fileWriter.println("Starting to procrastinate for " + timePeriodInMilliSecs + " millisecond befor termination");
                  Thread.currentThread().sleep(timePeriodInMilliSecs);
                  fileWriter.println("Starting to procrastinate for " + timePeriodInMilliSecs + " millisecond befor termination");
                  return new com.ai.application.interfaces.RequestExecutorResponse(true);
                }
                catch (java.io.IOException x)
                {
                  throw new RequestExecutionException("Error: IOException", x);
                }
                catch(java.lang.InterruptedException x)
                {
                  throw new RequestExecutionException("Error: Interrupted exception", x);
                }
                finally
                {
                  if (fileWriter != null)
                  {
                     fileWriter.close();
                  }
                }
        }
} 
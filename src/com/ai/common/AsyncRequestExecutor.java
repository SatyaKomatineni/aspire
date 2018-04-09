/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;

//import com.ai.data.*;

/** 
 * Responsibility: 
 * Execute the corresponding request in a child thread and return.
 * Useful for long reporting operations
 *
 * Creator related info
 **********************
 * Not a creator type. Executes for a response every time.
 */
public class AsyncRequestExecutor implements ICreator
{

   // from creator
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
      {
         // identify what is the real request
         String realRequestName = AppObjects.getIConfig().getValue(requestName + ".AsyncRequestName", null);
         if ( realRequestName == null)
         {
            AppObjects.log("error.RequestExecutor: Could not find the async request name");
            return new RequestExecutorResponse(true);
         }
         // request has been found
         RequestTask thisTask = new RequestTask(realRequestName, args);
         Thread t = new Thread(thisTask);
         t.setDaemon(true);
         t.start();
         AppObjects.info(this,"Thread started for request %1s", realRequestName );
         return new RequestExecutorResponse(true);
      }
} 

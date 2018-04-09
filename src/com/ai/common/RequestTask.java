/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import com.ai.application.utils.*;
import com.ai.application.interfaces.*;

public class RequestTask implements Runnable 
{
   private String m_requestName;
   private Object m_args;
   public RequestTask(final String inRequestName
                      ,Object args) 
   {
      m_requestName = inRequestName;
      m_args = args;
   }
   /**
    * Throws no exceptions
    * Does not return any values 
    */
   public void run()
   {
      try
      {
         AppObjects.info(this,"Starting  a new task for %1s", m_requestName );
         Object obj = AppObjects.getIFactory().getObject(m_requestName, m_args );
         AppObjects.info(this,"Task for %1s ended", m_requestName);
      }
      catch(RequestExecutionException x)
      {
         AppObjects.error(this, "Could not execute request %1s. Params are : %2s"
        		 		, m_requestName 
                        ,m_args.toString() );
         AppObjects.log("Error: Could not execute request",x);                        
      }
      catch(RuntimeException r)
      {
         AppObjects.log("Error:async: Run time exception detected", r);
         throw r;
      }
      catch(Error e)
      {
         AppObjects.log("Critical: An exception of type Error reported",e);
         String closeOnError = AppObjects.getIConfig().getValue("Aspire.closeOnError","no");
         if (closeOnError.toLowerCase() == "yes")
         {
            AppObjects.log("Critical Error reported. System being shutdown with code -1");
            System.exit(-1);
         }
         else
         {
            throw e;
         }
      }
   }
} 
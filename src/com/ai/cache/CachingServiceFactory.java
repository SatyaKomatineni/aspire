package com.ai.cache;

import com.ai.application.utils.*;
import com.ai.application.interfaces.*;

public class CachingServiceFactory
{
   private static ICachingService m_cachingService;
   static {
      String cobjName = AppObjects.getValue("request." + ICachingService.NAME + ".classname",null);
      if (cobjName == null)
      {
         m_cachingService = new DefaultCachingService();
      }
      else
      {
         //Caching service specified
         try
         {
            m_cachingService =
                  (ICachingService)AppObjects.getObject(ICachingService.NAME,null);
         }
         catch(RequestExecutionException x)
         {
            AppObjects.log("Error: Could not instantiate caching service",x);
            m_cachingService = new DefaultCachingService();
         }
      }
   }
   public static ICachingService getCachingService()
   {
      return m_cachingService;
   }
}
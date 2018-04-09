package com.ai.cache;

import com.ai.application.utils.*;
import java.util.*;
import com.ai.common.*;

public class CacheUtils
{
   public static String getCacheKey(String requestName, Map params)
   {
       String cacheKey = AppObjects.getValue(requestName + ".cacheKey",null);
       if (cacheKey == null) return null;
       String ignoreCase = AppObjects.getValue(requestName + ".ignoreCase","true");

       //cachekey is there
       cacheKey = cacheKey.toLowerCase();
       if (l.bt()) AppObjects.trace("CacheUtils","cache:Input params:%1s", params);
       String finalCacheKey = SubstitutorUtils.generalSubstitute(cacheKey,params);
       
       if (!(ignoreCase.equals("true")))
       {
           //ignore case is not true
           return finalCacheKey;
       }
       
       //ignore case is true
       return finalCacheKey.toLowerCase();
       
   }
   public static Object getObjectFromCache(String requestName, Map params)
   {
      String cacheKey = getCacheKey(requestName,params);
      if (cacheKey == null)
      {
         AppObjects.warn("CacheUtils","Trying to retrieve a cached object. But no cache key available");
         return null;
      }

      ICachingService cs = CachingServiceFactory.getCachingService();
      return cs.getFromCache(cacheKey);
   }

   public static Object getObjectFromCache(String cacheKey)
   {
      ICachingService cs = CachingServiceFactory.getCachingService();
      return cs.getFromCache(cacheKey);
   }
   public static void putObjectInCache(String cacheKey, Object obj)
   {
      ICachingService cs = CachingServiceFactory.getCachingService();
      cs.cache(obj,cacheKey,-1);
   }
   public static void putObjectInCache(String requestName, Map params, Object obj)
   {
      String cacheKey = getCacheKey(requestName,params);
      if (cacheKey == null)
      {
         AppObjects.log("Warn:Trying to put a cached object. But no cache key available");
         return;
      }

      //Placing the object in the cache
      AppObjects.info("CacheUtils","Cache: Placing the object in cache with key:%1s",cacheKey);

      ICachingService cs = CachingServiceFactory.getCachingService();
      cs.cache(obj,cacheKey,-1);
   }
   public static ICachingService getCachingService()
   {
	   return CachingServiceFactory.getCachingService();
   }

   public static void invalidateObjectInCache(String requestName, Map params)
   {
      String cacheKey = getCacheKey(requestName,params);
      if (cacheKey == null)
      {
         AppObjects.warn("CacheUtils","Trying to invalidate an object in cache. But no cache key available");
         return;
      }
      AppObjects.info("CacheUtils","Cache: Invalidating the object in cache with key:%1s",cacheKey);
      ICachingService cs = CachingServiceFactory.getCachingService();
      cs.invalidate(cacheKey);
   }
   public static void removeCacheKeysStartingWith(String userid)
   {
	   ICachingService ics = getCachingService();
	   List ckeylist = ics.getCacheKeys();
	   Iterator itr = ckeylist.iterator();
	   while(itr.hasNext())
	   {
		   String ckey = (String)itr.next();
		   if (ckey.startsWith(userid))
		   {
			   ics.invalidate(ckey);
			   AppObjects.info("CacheUtils", "Invalidating key:%1s", ckey);
		   }
	   }
   }//eof-function
}//eof-class
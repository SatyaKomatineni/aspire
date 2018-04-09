package com.ai.cache;

import java.util.List;

/**
 *  December of 2003
 *  I am really in two minds about caching service.
 *  Usually I don't like to cache things unless there is a method to the madness.
 *  I will have to see how this package will work out.
 *  Currently this package is treated experimental
 *
 */

public interface ICachingService
{
   public static String NAME="Aspire.AppObjects.CachingService";
   //place the object with a key
   public void cache(Object objectToCache
                     ,String key
                     ,int howLongInTicks);

   //returns null if the key is not there
   public Object getFromCache(String key);

   //For the given key invalidate the object
   public void invalidate(String key);
   
   public List getCacheKeys();

}
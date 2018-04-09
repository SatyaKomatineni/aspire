package com.ai.cache;

import java.util.*;
import com.ai.application.utils.*;

public class DefaultCachingService implements ICachingService
{

   //<String:key,Object:CacheableEntity>
   Hashtable ht = new Hashtable();

   //**************************************************************************
   //place the object with a key
   //**************************************************************************
   public void cache(Object objectToCache
                     ,String key
                     ,int howLongInTicks)
   {
	  AppObjects.info(this,"Placing object in cache with key:%1s", key);
      CacheableEntity ce = new CacheableEntity(objectToCache,key,howLongInTicks);
      ht.put(key,ce);
   }

   //**************************************************************************
   //returns null if the key is not there
   //**************************************************************************

   public Object getFromCache(String key)
   {
      CacheableEntity ce = (CacheableEntity)ht.get(key);
      if (ce == null)
      {
         AppObjects.info(this,"Requested cached object for key:%1s is not in the cache.",key);
         return null;
      }
      return ce.m_cachedObject;
   }

   //**************************************************************************
   //For the given key invalidate the object
   //**************************************************************************
   public void invalidate(String key)
   {
      CacheableEntity ce = (CacheableEntity)ht.get(key);
      if (ce == null)
      {
         AppObjects.warn(this,"Requested cached object for key:%1s is not in the cache for invalidation", key);
         return;
      }
      //Ce is there. Remove it from the cache
      AppObjects.info(this,"Cached object with key:%1s is being removed from the cache", key);
      ht.remove(key);
   }
   
   public List getCacheKeys()
   {
	   ArrayList al = new ArrayList();
	   Enumeration e = ht.keys();
	   while(e.hasMoreElements())
	   {
		   String ckey = (String)e.nextElement();
		   al.add(ckey);
	   }
	   return al;
   }
}//eof-class

//**************************************************************************
//* Local CacheableEntity class
//**************************************************************************
class CacheableEntity
{
   public Object m_cachedObject;
   public String m_key;
   public int m_ticks;

   public CacheableEntity(Object inObject, String inKey, int inTicks)
   {
      m_cachedObject = inObject;
      m_key = inKey;
      m_ticks = inTicks;
   }
}

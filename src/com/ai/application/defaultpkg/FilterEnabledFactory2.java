/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.application.defaultpkg;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import java.util.Hashtable;

/**
 * Enhances the previous release with
 * IInitializable support
 * Instantiation without any derivation contracts
 *
 */
public class FilterEnabledFactory2 extends AFactory1 implements ICreator, IFactory2
{
    private static Hashtable m_creatorCache = new Hashtable();

    private Object getCreatorFromCache( String inIdentifier )
    {
      return m_creatorCache.get(inIdentifier);
    }
    /**
     * Responsible for creating an object identified by className
     * If more than one thread requests a creator Only the first one will succeed
     * The second one will get it from the cache
     */
    private synchronized Object getCreator(String className, String absoluteRequestNameString )
      throws com.ai.application.interfaces.RequestExecutionException
    {
        //if creator is in the cache, get it from there
         Object creator = getCreatorFromCache( className );
         if (creator == null)
         {
             //creator is not in the cache, create a new one
             creator = createCreator(className);

             //if it is initializable initialize it
             if (creator instanceof IInitializable)
             {
                 ((IInitializable)(creator)).initialize(absoluteRequestNameString);
             }
             if (!(creator instanceof ISingleThreaded ))
             {
               // Add creator to the cache only if it is not single threaded
               // Only non-single-threaded objects can expect to get their
               // execute methods call on multiple threads
                m_creatorCache.put(className,creator);
             }
         }  // if - creator creation
         return creator;
    }

    /**
     * Creates an object with no consideration of synchronization.
     * The calling methods are expected to provide the synchronization.
     * Only called by the getCreator() for now
     */
    protected Object createCreator(String className)
      throws com.ai.application.interfaces.RequestExecutionException
    {
         try
         {
            Class classObj = Class.forName(className);
            AppObjects.log("Creating the creator :" + className);
            Object creator = classObj.newInstance();
            return   creator;
         }
         catch(java.lang.ClassNotFoundException x)
         {
            AppObjects.log(x);
            throw new RequestExecutionException(x.getMessage());
         }
         catch (java.lang.InstantiationException x)
         {
            AppObjects.log(x);
            throw new RequestExecutionException(x.getMessage());
         }
         catch(java.lang.IllegalAccessException x)
         {
            AppObjects.log(x);
            throw new RequestExecutionException(x.getMessage());
         }
    }

    /**
     * Public interface of this object.
     * Responsible for either creating a new object via a well known method
     * Or retrieve the creator object from cache and call the well known method
     * Either way every time this method is called, the creator's well known method is called.
     * It is upto the creator to return cached objects if necessary as in the case of singleton.
     */
    public Object getObject(String inIdentifier, Object args )
        throws RequestExecutionException
    {
        return getObjectAbsolute("request." + inIdentifier,args);
    }

    public Object getObjectAbsolute(String inIdentifier, Object args )
        throws RequestExecutionException
    {
         // Get first the classname of the request
         String objClassName = AppObjects.getIConfig().getValue(inIdentifier + ".className",null);
         if (objClassName == null)
         {
            String msg = "warn: Could not find classname for request '" + inIdentifier + "'" ;
            AppObjects.log(msg);
            throw new RequestExecutionException(msg);
         }

         // Get the creator based on the classname
         // It could have been cached or new depending on if the className supports ISingleThreaded
         Object creator = getCreator(objClassName,inIdentifier);

        Object obj = null;
        if (creator instanceof ICreator)
        {
            // Call the well known method
            obj = ((ICreator)creator).executeRequest(inIdentifier,args);
        }
        else
        {
            //This creator doesnt implement ICreator
            obj = creator;
        }

         // Apply filters if any
         // see if there is a filter available
         String filterName = AppObjects.getIConfig().getValue(inIdentifier + ".filterName", null);
         if (filterName != null)
         {
            // filter available
            AppObjects.log("Calling the filter : " + filterName );
            Object filterObj = getObject(filterName,obj);
            obj =  filterObj;
         }

         return obj;
    }

    public Object executeRequest(String requestName, Object args)
        throws RequestExecutionException
    {
      return this;
    }
}

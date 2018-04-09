/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.application.defaultpkg;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhances the previous release with
 * IInitializable support
 * Instantiation without any derivation contracts
 * 
 * 12/5/15
 * **************
 * Made the cache variable into a volatile variable
 * verify later if this is correct or needed
 *
 */
public class FilterEnabledFactory4 extends AFactory1 implements ICreator, IFactory2
{
	//Made this volatile. Not sure if needed, but I suspect that I do
    private static volatile Map m_creatorCache = new HashMap();
    private static Class m_multiInstanceTagInterface = null;
    static
    {
        try
        {
            m_multiInstanceTagInterface =
                Class.forName("com.ai.application.interfaces.ISingleThreaded");
        }
        catch(ClassNotFoundException x)
        {
            throw new RuntimeException("Could not get a class for interface ISingleThreaded");
        }
    }

    private Object getCreatorFromCache( String inIdentifier )
    {
      return m_creatorCache.get(inIdentifier);
    }
    /**
     * Responsible for creating an object identified by className
     * If more than one thread requests a creator Only the first one will succeed
     * The second one will get it from the cache
     */
    private Object getCreator(String className, String absoluteRequestNameString )
      throws com.ai.application.interfaces.RequestExecutionException
    {
        try
        {
	        //if creator is in the cache, get it from there
	         Object creator = getCreatorFromCache( className );
	         if (creator != null) return creator;
	         
	         //creator is not there
	         //creator is not in the cache, create a new one
	         Class classObj = Class.forName(className);
	         AppObjects.trace(this,"Creating the creator:%1s",className);
	         if (isMultiInstance(classObj))
	         {
	             //It is a multiinstance
	             creator = createCreator(classObj);
	         }
	         else
	         {
	             //it is a single instacne
	             creator = createCreatorSingleton(classObj,className);
	         }
	
	         //if it is initializable initialize it
	         if (creator instanceof IInitializable)
	         {
	             ((IInitializable)(creator)).initialize(absoluteRequestNameString);
	         }
	         return creator;
        }
        catch(ClassNotFoundException x)
        {
            throw new RequestExecutionException("Error: in the factory.",x);
        }
    }
    
    private Object createCreatorSingleton(Class classObj, String className)
    throws RequestExecutionException
    {
        AppObjects.trace(this,"Creating a singleton. Going to lock on the class object");
        synchronized(classObj)
        {
            Object creator = m_creatorCache.get(className);
            if (creator != null) return creator;
            
            //creator is still null
            creator = createCreator(classObj);
            
            // Add creator to the cache only if it is not single threaded
            // Only non-single-threaded objects can expect to get their
            // execute methods call on multiple threads
            m_creatorCache.put(className,creator);
            
            return creator;
        }
    }
    private boolean isMultiInstance1(Object o)
    {
        if (o instanceof ISingleThreaded)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    private boolean isMultiInstance1(Class classObj)
    {
        
        Class[] interfaceArray = classObj.getInterfaces();
        for(int i=0;i<interfaceArray.length;i++)
        {
            Class thisClass = interfaceArray[i];
            if (thisClass.getName().equals("com.ai.application.interfaces.ISingleThreaded"))
            {
                //it is single threaded
                return true;
            }
        }
        //See if the parent is a multiinstance
        Class parent = classObj.getSuperclass();
        if (parent == null)
        {
            //no more parents
            return false;
        }
        return isMultiInstance(parent);
    }

    private boolean isMultiInstance(Class classObj)
    {
        return m_multiInstanceTagInterface.isAssignableFrom(classObj);
    }
    /**
     * Creates an object with no consideration of synchronization.
     * The calling methods are expected to provide the synchronization.
     * Only called by the getCreator() for now
     */
    protected Object createCreator(Class classObj)
      throws com.ai.application.interfaces.RequestExecutionException
    {
         try
         {
            return classObj.newInstance();
         }
         catch (java.lang.InstantiationException x)
         {
            throw new RequestExecutionException(x.getMessage());
         }
         catch(java.lang.IllegalAccessException x)
         {
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
            AppObjects.trace(this,"Calling the filter : %1s",filterName );
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

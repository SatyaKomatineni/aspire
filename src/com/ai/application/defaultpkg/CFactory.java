/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/default/CFactory.java

package com.ai.application.defaultpkg;

import com.ai.application.interfaces.ISingleThreaded;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.IFactory;
import com.ai.application.interfaces.ICreator;
import com.ai.application.utils.AppObjects;
import java.util.Hashtable;

public class CFactory implements IFactory, ICreator {

    private static Hashtable m_creatorCache = new Hashtable();    
    public CFactory() {
    }
    private Object getCreatorFromCache( String inIdentifier )
    {
      return m_creatorCache.get(inIdentifier);
    }
    /**
     * Unsynchronized method
     */
    private synchronized ICreator getCreator(String inIdentifier )
      throws com.ai.application.interfaces.RequestExecutionException
    {
         ICreator creator = (ICreator)getCreatorFromCache( inIdentifier );
         if (creator == null)
         {
             creator = createCreator(inIdentifier);
             if (!(creator instanceof ISingleThreaded ))
             {
               // Add creator to the cache only if it is not single threaded
               // Only non-single-threaded objects can expect to get their
               // execute methods call on multiple threads 
                m_creatorCache.put(inIdentifier,creator);
             }
         }  // if - creator creation
         return creator;
    }
    
    private ICreator createCreator(String identifier)
      throws com.ai.application.interfaces.RequestExecutionException
    {
         try 
         {
            String objClassName = AppObjects.getIConfig().getValue(identifier + ".className");
            Class classObj = Class.forName(objClassName);
            AppObjects.log("Creating the creator :" + objClassName);
            Object creator = classObj.newInstance();     
            return   ((ICreator)(creator));
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
         catch(ConfigException x)
         {
                AppObjects.log(x);
                throw new RequestExecutionException(RequestExecutionException.REQUEST_NOT_REGISTERED);
         }
    }
    public Object getObject(String inIdentifier, Object args )
        throws RequestExecutionException
    {                 
         ICreator creator = getCreator("request." + inIdentifier);
         return creator.executeRequest("request." + inIdentifier,args);
    }
    public Object oldGetObject(String inIdentifier, Object args) 
        throws RequestExecutionException
        {
         String identifier = "request." + inIdentifier;
         String objClassName = null;
         Object creator = null;
         try 
         {
            System.out.println("before getting config");
            objClassName = AppObjects.getIConfig().getValue(identifier + ".className");
            System.out.println("after getting config " + objClassName);
            Class classObj = Class.forName(objClassName);
            creator = classObj.newInstance();
            return   ((ICreator)(creator)).executeRequest(identifier, args);
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
         catch(ConfigException x)
         {
                AppObjects.log(x);
                throw new RequestExecutionException(RequestExecutionException.REQUEST_NOT_REGISTERED);
         }
    }
    
    /**
       @roseuid 36950B4200EF
     */
    public Object executeRequest(String requestName, Object args) 
        throws RequestExecutionException
    {
      return this;
    }
}

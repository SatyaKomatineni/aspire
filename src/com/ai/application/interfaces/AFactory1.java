/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.application.interfaces;
import com.ai.application.utils.AppObjects;

public abstract class AFactory1 implements IFactory1
{
    public Object getObjectWithDefault(String identifier, Object args, Object defaultObject)
    {
       try { return getObject(identifier,args);}
       catch(RequestExecutionException x)
       {
         AppObjects.log("Warn: Could not instantiate object identified by '" + identifier + "'" ,x);
         return defaultObject;
       }
    }
   
} 
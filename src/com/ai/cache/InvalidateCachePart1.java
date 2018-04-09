package com.ai.cache;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.utils.TransformUtils;
import com.ai.common.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Map;
/**
 * Invalidates an object in cache based on its key
 *
 * Additional property file arguments
 * 1. cacheKey=substitutable cache key
 *
 * Output
 * 1.resultName: true
 *
 */

public class InvalidateCachePart1 extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
        throws RequestExecutionException
    {
        try {
            String option = AppObjects.getValue(requestName + ".option");
            if (option.equalsIgnoreCase("allkeysforuser"))
            {
            	invalidateAllKeysForUser(requestName,inArgs);
            }
            else
            {
            	invalidateUniqueKey(requestName,inArgs);
            }
            	
            return new Boolean(true);
         }
         catch(ConfigException x)
         {
            throw new RequestExecutionException("Error:Configuration key .cacheKey not found for this request",x);
         }

    }
    private void invalidateUniqueKey(String requestName, Map inArgs)
    throws RequestExecutionException
    {
        try {
            String cacheKey = AppObjects.getValue(requestName + ".cacheKey");
            String finalKey = SubstitutorUtils.generalSubstitute(cacheKey,inArgs);
            CacheUtils.invalidateObjectInCache(requestName,inArgs);
            AppObjects.info(this,"Invalidated key:" + finalKey);
         }
         catch(ConfigException x)
         {
            throw new RequestExecutionException("Error:Configuration key .cacheKey not found for this request",x);
         }
    }
    private void invalidateAllKeysForUser(String requestName, Map inArgs)
    throws RequestExecutionException
    {
        try {
            String cacheKey = AppObjects.getValue(requestName + ".userid");
            String finalKey = SubstitutorUtils.generalSubstitute(cacheKey,inArgs);
            CacheUtils.removeCacheKeysStartingWith(finalKey);
            AppObjects.info(this,"Invalidated all keys for user:" + finalKey);
         }
         catch(ConfigException x)
         {
            throw new RequestExecutionException("Error:Configuration key .cacheKey not found for this request",x);
         }
    }
}



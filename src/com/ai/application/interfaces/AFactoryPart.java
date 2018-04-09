package com.ai.application.interfaces;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.parts.IgnoreResult;

/**
 * AFacotryPart is an ICreator with Map as its input
 * Given a map of arguments create an object
 * Place the created object in the input Map with a "resultName"
 * 
 * Note that this is a singleton.
 * Don't manage state in instance variables.
 * If you want that then you need to make your class
 * multi-instance by implementing ISingleThreaded
 * 
 * @see ICreator
 * @see ISingleThreaded
 * @see IInitializable 
 * @see FilterEnabledFactory4
 */
public abstract class AFactoryPart implements ICreator
{
    protected abstract Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException;

    // from ICreator
    public Object executeRequest(String requestName, Object inArgs)
            throws RequestExecutionException
    {

        Map mArgs = getMapArgs(inArgs);
        Object o = executeRequestForPart(requestName,mArgs);
        String resultName = AppObjects.getValue(requestName + ".resultName",null);
        if (resultName != null)
        {
        	if (o != null)
        	{
        		//valid object
        		if (!(o instanceof IgnoreResult))
        		{
        			//o is not ignoreresult
        			mArgs.put(resultName.toLowerCase(),o);
        		}
        	}
        }
        return o;
    }
    private Map getMapArgs(Object inArgs) throws RequestExecutionException
    {
        if (inArgs instanceof Map)
            return (Map)inArgs;
        if (inArgs instanceof Vector)
        {
            return (Map)(((Vector)inArgs).get(0));
        }
         throw new RequestExecutionException("Error: Wrong type passed to AFactoryPart. Map expected");
    }

}
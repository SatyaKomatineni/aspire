package com.ai.application.interfaces;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.parts.IgnoreResult;

/**
 * 
 * basedon: AFactoryPart
 * 
 * Why?
 * ****************************
 * I want the base class to understand a map returned as well
 * Experimental at this time
 * Try to use this going forward.
 *
 * 
 * Old notes from its previous  incarnation
 * ******************************************
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
public abstract class AFactoryPart1 implements ICreator
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
        			if (o instanceof Map)
        			{
        				//it is a map
        				//append it to the current map
        				addItToOriginalArgs(mArgs, (Map)o);
        			}
        			else
        			{
        				//it is not a map. 
        				mArgs.put(resultName.toLowerCase(),o);
        			}
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
    
	private void addItToOriginalArgs(Map targetArgs, Map newArgs)
	{
		//lowercase input args to put into targetargs
		for(Object key: newArgs.keySet())
		{
			String sKey = (String)key;
			targetArgs.put(sKey.toLowerCase(), newArgs.get(key));
		}
		//targetArgs.putAll(newArgs);
	}
}//eof-class
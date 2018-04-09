/*
 * Created on Nov 2, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.parts;

import java.util.Hashtable;
import java.util.Map;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.utils.TransformUtils;
import com.ai.common.TransformException;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AspireURLStringReaderPart 
{

    protected Object executeRequestForPart(String requestName, Map inArgs)
    throws RequestExecutionException
	{
	    try
	    {
		//mandatory args
	        String aspireURLName = AppObjects.getValue(requestName + ".aspireUrlName");
	        String s = TransformUtils.transform(aspireURLName, (Hashtable)inArgs);
	        return s;
	    }
	    catch(ConfigException x)
	    {
	        throw new RequestExecutionException("Error: ConfigException. See the embedded exception for details", x);
	    }
	    catch(TransformException x)
	    {
	        throw new RequestExecutionException("Error: TransformException", x);
	    }
	}
}//eof-class

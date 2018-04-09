package com.ai.parts;

import com.ai.application.interfaces.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.data.*;
import javax.servlet.http.*;
import com.ai.common.*;

/**
 * Reads a resouce and returns a stream
 */

public class ResourceReaderPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
    	try
    	{
	    	String resourceName = 
	    		AppObjects.getValue(requestName + ".resourceName");
	    	String substResourceName =
	    		SubstitutorUtils.urlencodeSubstitute(resourceName, inArgs);
	    	AppObjects.trace(this,"Reading from:%1s", substResourceName);
	    	return FileUtils.readResource(substResourceName);
    	}
    	catch(Exception x)
    	{
    		throw new RequestExecutionException("Could not read resource",x);
    	}
    }//eof-function
}//eof-class

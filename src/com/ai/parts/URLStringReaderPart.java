package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.utils.TransformUtils;
import com.ai.common.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Map;
import com.ai.servletutils.*;
/**
 * Takes a URL and reads its contents as string and returns it
 *
 * Additional property file arguments
 * 1. URL=Any internal or external url
 *
 * Output
 * 1.resultName: The content at the specified url as string
 *
 */

public class URLStringReaderPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
        throws RequestExecutionException
    {
       String substUrlString = null;
        try
        {
        //mandatory args
            String urlString = AppObjects.getValue(requestName + ".URL");
            substUrlString = ServletUtils.getSubstitutedURLUsingAMap(urlString,inArgs);
            java.net.URL url = new java.net.URL(substUrlString);
            InputStream is = url.openStream();
            String os = FileUtils.readStreamAsString(is);
            return os;
        }
        catch(ConfigException x)
        {
            throw new RequestExecutionException("Error: ConfigException. See the embedded exception for details", x);
        }
        catch(IOException x)
        {
            throw new RequestExecutionException("Error: reading url:" + substUrlString,x);
        }
    }
    public static String readFromUrl(String completeUrlString)
    throws RequestExecutionException
	{
	    try
	    {
	        java.net.URL url = new java.net.URL(completeUrlString);
	        InputStream is = url.openStream();
	        String os = FileUtils.readStreamAsString(is);
	        return os;
	    }
	    catch(IOException x)
	    {
	        throw new RequestExecutionException("Error: reading url:" + completeUrlString,x);
	    }
	}
}//eof-class

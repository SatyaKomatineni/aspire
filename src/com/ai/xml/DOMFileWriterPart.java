package com.ai.xml;
import com.ai.application.interfaces.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import com.ai.application.utils.*;

/**
 * Read a DOM from the hashtable and write it to a file
 * Additional property file arguments
 * 1. domname: Name of the DOM in the hashtable
 * 2. filename: Name of the file
 * Output
 * 1.resultName: Name of the file that is written to
 */

public class DOMFileWriterPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
        PrintWriter w = null;
        String filename = null;
        try
        {
            String domname = AppObjects.getValue(requestName + ".domname");
            filename = AppObjects.getValue(requestName + ".filename");

            Document doc = (Document)inArgs.get(domname.toLowerCase());
            if (doc == null)
            {
                throw new RequestExecutionException("Error: Input DOM not available for key:" + domname);
            }
            w = new PrintWriter(new FileOutputStream(filename));
            XMLUtils.output(doc,w);
            return filename;
        }
        catch(ConfigException x)
        {
            throw new RequestExecutionException("Error: ConfigException. See the embedded exception for details",x);
        }
        catch(java.io.IOException x)
        {
            throw new RequestExecutionException("Error: Error wrting to file:" + filename,x);
        }
        catch(com.ai.common.TransformException x)
        {
            throw new RequestExecutionException("Error: TransformException",x);
        }
        finally
        {
            if (w!=null) w.close();
        }


    }
}
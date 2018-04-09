package com.ai.xml;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import org.w3c.dom.*;
import java.util.*;
import javax.xml.parsers.*;

/**
 * Takes a hash table as input
 * Takes the following information from the properties file
 * 1. XML url
 * 2. optional XSD to validate the XML
 * 3. Name under which this DOM is registered
 * Create a DOM and add it to the hashtable under the specified name
 *
 */
public class GetDOMPart extends DOMFactory
{
    protected Document createDOM(
            String requestName,
            Map args)
            throws RequestExecutionException
    {
        String url = "";
        try
        {
            url = AppObjects.getValue(requestName + ".url");
            return DOMUtils.createDocument(url);
        }
        catch(ParserConfigurationException x)
        {
            throw new RequestExecutionException("Error:Could not create a document due to ParserExecption using URL:" + url,x);
        }
        catch(org.xml.sax.SAXException x)
        {
            throw new RequestExecutionException("Error:Could not create a document due to SAXException using URL:" + url,x);
        }
        catch(ConfigException x)
        {
            throw new RequestExecutionException("Error:Most likely 'url' argument not found. Check configuration file",x);
        }
        catch(java.io.IOException x)
        {
            throw new RequestExecutionException("Error:IOException while creating DOM from URL in GetDOMPart for url:" + url,x);
        }

    }
}
package com.ai.xml;
import com.ai.application.interfaces.*;
import org.w3c.dom.*;
import java.util.*;
import com.ai.application.utils.*;

/**
 * Read a DOM from the hashtable and write it to a file
 * Additional property file arguments
 * 1. nodeXSLTAddress: Name of the DOM in the hashtable
 * 2. nodeTextValue: Name of the file
 * Output
 * None
 */

public class DOMUpdaterPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
        try
        {
            String nodeXSLTAddress = AppObjects.getValue(requestName + ".nodeXPATHAddress");
            String nodeTextValueKey = AppObjects.getValue(requestName + ".nodeTextValueKey");
            String domname = AppObjects.getValue(requestName + ".domname");

            Document doc = (Document)inArgs.get(domname.toLowerCase());

            if (doc == null)
            {
                throw new RequestExecutionException("Error: Input DOM not available for key:" + domname);
            }
            String nodeTextValue = (String)inArgs.get(nodeTextValueKey.toLowerCase());
            DOMUtils.replaceValueForNode(doc,nodeXSLTAddress,nodeTextValue);
            return doc;
        }
        catch(ConfigException x)
        {
            throw new RequestExecutionException("Error: ConfigException. See the embedded exception for details",x);
        }
        catch(javax.xml.transform.TransformerException x)
        {
            throw new RequestExecutionException("Error: TransformerException. See the embedded exception for details",x);
        }
    }
}

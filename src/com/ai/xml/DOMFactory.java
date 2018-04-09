package com.ai.xml;

import com.ai.application.interfaces.*;
import java.util.*;

import org.w3c.dom.*;

public abstract class DOMFactory extends AFactoryPart
{
    protected abstract Document createDOM(
            String requestName,
            Map args)
            throws RequestExecutionException;

    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
        return createDOM(requestName, inArgs);
    }
} // eofc

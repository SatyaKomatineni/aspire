package com.ai.masterpage;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.common.*;

/**
 * Return an IMasterPage using the given Hashtable
 *
 * Input
 * Map
 *
 * Output
 * Returns an IMasterPage
 *
 */

public abstract class AMasterPageCreatorPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
       return create(requestName, inArgs);
    }//eof-function

    abstract protected IMasterPage create(String requestName, Map inArgs)
          throws RequestExecutionException;
}//eof-class


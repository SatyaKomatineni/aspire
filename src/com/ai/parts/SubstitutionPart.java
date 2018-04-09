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
 * Transforms a specified string with substitution arguments in it and returns it
 *
 * Additional property file arguments
 * 1. substitution=<string value with substitutions in it>
 *
 * Output
 * 1.resultName: The above value translated after substitution
 *
 */

public class SubstitutionPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
       try
       {
          String substString = AppObjects.getValue(requestName + ".substitution");
          String newString = SubstitutorUtils.generalSubstitute(substString,new MapDictionary(inArgs));
          return newString;
       }
       catch(ConfigException x)
       {
          throw new RequestExecutionException("Error:config errror",x);
       }
    }//eof-function
}//eof-class






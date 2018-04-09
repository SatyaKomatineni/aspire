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
 * Returns an input stream for a given string
 *
 * Additional property file arguments
 * 1. inputStringName=<Name of the key in the hashtable where the streamable string is present>
 *
 * Output
 * 1.resultName: InputStream of the string
 *
 */

public class StringToInputStreamPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
       try
       {
          String strName = AppObjects.getValue(requestName + ".inputStringName");
          String strValue = (String)inArgs.get(strName.toLowerCase());
          if (StringUtils.isEmpty(strValue))
          {
        	  AppObjects.trace(this,"empty input string");
        	  return null;
          }
          InputStream is = new StringBufferInputStream(strValue);
          return is;
       }
       catch(ConfigException x)
       {
          throw new RequestExecutionException("Error:config errror",x);
       }
    }//eof-function
}//eof-class








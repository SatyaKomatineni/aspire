package com.ai.parts;

import com.ai.application.interfaces.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.data.*;
import com.ai.sm.capturetags.CaptureTagUtils;

import javax.servlet.http.*;
import com.ai.common.*;

/**
 * Splits a string into a map using capture tags
 * The rest of the document outside the tags is kept in a separate key
 * 
 * Additional property file arguments
 * 1. stringToSplitName
 * 2. mainPartName
 * 3. originalStringName
 *
 * Output
 * 1. Old string that was split as resultName
 *
 */

public class SplitStringUsingTagsPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
       try
       {
          String stringToSplitName = AppObjects.getValue(requestName + ".stringToSplitName");
          String mainPartName = AppObjects.getValue(requestName + ".mainPartName");
          String originalStringName = AppObjects.getValue(requestName + ".originalStringName");
          String stringToSplit = (String)inArgs.get(stringToSplitName.toLowerCase());
          CaptureTagUtils.splitString(stringToSplit, inArgs, mainPartName.toLowerCase());
          inArgs.put(originalStringName.toLowerCase(), stringToSplit);
          return stringToSplit;
       }
       catch(ConfigException x)
       {
          throw new RequestExecutionException("Error:config errror",x);
       }
       catch(Exception x)
       {
           throw new RequestExecutionException("Parsing exception",x);
       }
    }//eof-function
}//eof-class






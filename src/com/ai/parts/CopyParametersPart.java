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
 * Copy/renamae parameters for downstream
 *
 * Additional property file arguments
 * 1. originalNames: comma separated list of key names that needs to be copied from
 * 2. newNames: comma separated list of keys to which the above key values will be copied to
 *
 * Output
 * 1.resultName: true if successful, exception otherwise
 */

public class CopyParametersPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
       try
       {
          String originalNames = AppObjects.getValue(requestName + ".originalNames");
          String newNames = AppObjects.getValue(requestName + ".newNames");

          Vector vOrig = Tokenizer.tokenize(originalNames,",");
          Vector vNew = Tokenizer.tokenize(newNames,",");

          //for each original name
          // read the value
         // put it under the new name as well
          int i=0;
          for(Enumeration e=vOrig.elements();e.hasMoreElements();i++)
          {
             String origName = (String)e.nextElement();
             if (origName == null) continue;

             Object origValue = inArgs.get(origName.toLowerCase());
             if (origValue == null)
             {
                AppObjects.log("Warn:value not found for key:" + origName);
                continue;
             }
             String newName = (String)vNew.elementAt(i);
             inArgs.put(newName.toLowerCase(),origValue);
          }
         return new Boolean(true);
       }
       catch(ConfigException x)
       {
          throw new RequestExecutionException("Error:config error",x);
       }
    }//eof-function
}//eof-class




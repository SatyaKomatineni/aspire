package com.ai.parts;

import com.ai.application.interfaces.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.data.*;
import com.ai.filters.FilterUtils;

import javax.servlet.http.*;
import com.ai.common.*;

/**
 * ReusablePart
 *	Calls another part or business logic, by translating the input arguments 
 *  to match the expected names of the receiver
 * 
 * Additional property file arguments
 * 1. originalNames: comma separated list of key names that needs to be copied from
 * 2. newNames: comma separated list of keys to which the above key values will be copied to
 * 3. propagateParams: yes|no: yes
 * 4. reuseRequestName: Name of the reusable target request
 *
 * if Propagateparams is "yes", then the incomign map is passed down
 * if it is set to "no" then a freshmap is created for the receiver
 * Use "no" with caution, as this will result in a new transaction
 * if the called parts have any database calls
 * 
 * Output
 * 1.resultName: What ever the target request returns
 */

public class ReusablePart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
       try
       {
          String originalNames = AppObjects.getValue(requestName + ".originalNames","");
          String newNames = AppObjects.getValue(requestName + ".newNames","");
		  String propagateParams = AppObjects.getValue(requestName + ".propagateParams","yes");
		  boolean bPropagateParams=FilterUtils.convertToBoolean(propagateParams);
		  
		  String reuseRequestName = AppObjects.getValue(requestName + ".reuseRequestName");

          Vector vOrig = Tokenizer.tokenize(originalNames,",");
          Vector vNew = Tokenizer.tokenize(newNames,",");
          
          Map targetArgs = null;
          
          if (bPropagateParams == true)
          {
          	//propagate the parasm
          	targetArgs = inArgs;
          }
          else
          {
          	//Dont propagate them
          	targetArgs = new HashMap();
          }

          //for each original name
          // read the value
         // put it under the new name as well
          int i=0;
          for(Enumeration e=vOrig.elements();e.hasMoreElements();i++)
          {
             String origName = (String)e.nextElement();
             if (origName == null) continue;

             Object origValue = inArgs.get(origName);
             if (origValue == null)
             {
                AppObjects.warn(this,"value not found for key:" + origName);
                continue;
             }
             String newName = (String)vNew.elementAt(i);
             targetArgs.put(newName.toLowerCase(),origValue);
          }
         //Call the reuse guy
         Object returnObj = AppObjects.getObject(reuseRequestName, targetArgs);
         return returnObj;
       }
       catch(ConfigException x)
       {
          throw new RequestExecutionException("Error:config error",x);
       }
	   catch(UnexpectedTypeException x)
	   {
		  throw new RequestExecutionException("Error: Boolean conversion error",x);
	   }
    }//eof-function
}//eof-class




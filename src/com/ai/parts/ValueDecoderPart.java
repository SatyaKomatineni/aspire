package com.ai.parts;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.common.*;

/**
 * 3/2/2012
 * ************
 * Deprecation Notes
 * There is a new part available
 * 
 * Older notes
 * *************
 * For a given key and value, decode the value similar to databases
 *
 * Additional property file arguments
 * 1. decodeKeyName=<key>
 * 2. defaultValue=<use this if the key does not exist>
 * 3. translate.<value1>=<new value with substitution support>
 * 4. n number of translates allowed
 * 5. If there is no translation for a value then nothing will get inserted
 *
 * Output
 * 1.resultName: Name of the key under which the above translation will go
 * If the resultName is the same then it gets overwritten
 *
 */

public class ValueDecoderPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
       try
       {
          String decodeKeyName = AppObjects.getValue(requestName + ".decodeKeyName");
          String defaultValue = AppObjects.getValue(requestName + ".defaultValue");

	    String decodeKeyValue = (String)inArgs.get(decodeKeyName.toLowerCase());
	    if (StringUtils.isEmpty(decodeKeyValue))
		{
	    	//the key is not there or if it is it is empty
			return SubstitutorUtils.generalSubstitute(defaultValue,new MapDictionary(inArgs));
		}

		//value exists
		String translatedValue = AppObjects.getValue(requestName + ".translate." + decodeKeyValue,null);
		if (translatedValue == null)
		{
			AppObjects.info(this,"No translation available for:%1s", decodeKeyValue);
			return null;
		}

		//translated Value exists
		return SubstitutorUtils.generalSubstitute(translatedValue ,new MapDictionary(inArgs));
       }
       catch(ConfigException x)
       {
          throw new RequestExecutionException("Error:config error",x);
       }
    }//eof-function
}//eof-class




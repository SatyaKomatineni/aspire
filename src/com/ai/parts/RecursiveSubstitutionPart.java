package com.ai.parts;

import java.util.Map;

import com.ai.application.interfaces.AFactoryPart;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.MapDictionary;
import com.ai.common.SubstitutorUtils;

/**
 * @author Satya Komatineni Jan 27, 2006
 */
public class RecursiveSubstitutionPart  extends AFactoryPart
{

    protected Object executeRequestForPart(String requestName, Map inArgs)
    throws RequestExecutionException
	{
		try
		{
		  String substString = AppObjects.getValue(requestName + ".substitution");
		  String sNumberOfTimes = AppObjects.getValue(requestName + ".numberOfTimes","1");
		  int numberOfTimes = Integer.parseInt(sNumberOfTimes);
		  
		  //If it is zero times, atleast do it once
		  if (numberOfTimes == 0) numberOfTimes = 1;
		      
		  //Separate the more common case of 1 and do it right
		  if (numberOfTimes == 1)
		  {
		      String newString = SubstitutorUtils.generalSubstitute(substString,new MapDictionary(inArgs));
		      return newString;
		  }
		  
		  //more times expected
		  String newString = substString;
		  for(int i=0;i<numberOfTimes;i++)
		  {
		      newString = SubstitutorUtils.generalSubstitute(newString,new MapDictionary(inArgs));
		  }
		  return newString;
		}
		catch(ConfigException x)
		{
		  throw new RequestExecutionException("Error:config errror",x);
		}
	}//eof-function

}

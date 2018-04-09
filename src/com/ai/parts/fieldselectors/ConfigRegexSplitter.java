package com.ai.parts.fieldselectors;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.utils.AppObjects;
import com.ai.parts.configobjects.ServiceObject;

/**
 * @author satya
 * 
 * Split a line or a row of string text in to a set of fields
 * Implementing classes will decide how to split
 * 
 * @See ServiceObject
 * 
 * This is a singleton
 * This is a service object
 * instance variables allowed
 *  
 */
public class ConfigRegexSplitter 
extends RegexSplitter
implements ServiceObject
{
	//Configuration will read this
	private String regex;
	
	//This comes from configuration
	public void initialize(String requestName)
	{
		try
		{
			regex = AppObjects.getValue(requestName + ".regex");
		}
		catch(ConfigException x)
		{
			throw new RuntimeException("Regular expression configuration cannot be empty",x);
		}
	}
	
	//implement this
	@Override
	protected String hookGetRegex()
	{
		if (regex == null)
		{
			AppObjects.error(this, "Regular expression configuration cannot be empty");
			throw new RuntimeException("Regular expression configuration cannot be empty");
		}
		return regex;
	}
}


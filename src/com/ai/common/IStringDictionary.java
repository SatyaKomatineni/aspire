package com.ai.common;

import com.ai.application.interfaces.ConfigException;
/**
 * Todo: Why do I need a string dictionary?
 *
 */
public interface IStringDictionary extends IDictionary
{
	public String getAsString(String key)
	throws ConfigException;
}

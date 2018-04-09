package com.ai.common.mapper;

import java.util.Hashtable;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.IDictionary;
import com.ai.common.IStringDictionary;

/**
 * Introduced to read key value pair mappings
 *
 */
public class GenericMapper 
{
	public static IStringDictionary getMapperForRequest(String requestname)
	throws RequestExecutionException
	{
		return (IStringDictionary)AppObjects.getObject(requestname, new Hashtable());
	}
}

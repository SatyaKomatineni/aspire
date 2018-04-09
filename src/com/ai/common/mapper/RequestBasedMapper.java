package com.ai.common.mapper;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.ISingleThreaded;
import com.ai.application.utils.AppObjects;
import com.ai.common.DDictionary;

public class RequestBasedMapper extends DDictionary
implements IInitializable, ISingleThreaded
{
	private String mappingPrefix = null;

	public void initialize(String requestName) 
	{
		mappingPrefix = requestName + ".mapping.";
	}

	@Override
	public Object internalGet(Object key) 
	{
		if (!(key instanceof String))
		{
			throw new RuntimeException("expecting String type");
		}
		try
		{
			return AppObjects.getValue(mappingPrefix + (String)key);
		}
		catch(ConfigException x)
		{
			throw new RuntimeException("Could not find key:" + (String)key,x);
		}
	}//eof-function
}//eof-class

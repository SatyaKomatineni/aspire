package com.ai.htmlgen.streamwriters;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.IStringDictionary;
import com.ai.common.StringUtils;
import com.ai.common.mapper.GenericMapper;
import com.ai.servlets.AspireServletException;

public class ExtensionToContentTypeMapping 
{
	private static IStringDictionary extensionToContentTypeDictionary;
	static 
	{
		try
		{
			extensionToContentTypeDictionary =
			GenericMapper.getMapperForRequest("aspire.contentTypeMapping");
		}
		catch(RequestExecutionException x)
		{
			throw new RuntimeException(
					"Could not get generic mapper for content type mapping",x);
		}
	}


	public static String getContentType(String fileExtensionWithoutDot) 
	throws AspireServletException 
	{
		try
		{
			if (StringUtils.isEmpty(fileExtensionWithoutDot))
			{
				return "application/octet-stream"; 
			}
			String contentType = 
				extensionToContentTypeDictionary
					.getAsString(fileExtensionWithoutDot);
			if (StringUtils.isValid(contentType))
			{
				return contentType;
			}
			//invalid content type or null
			return "application/octet-stream"; 
		}
		catch(ConfigException x)
		{
			throw new AspireServletException("Error:cannot get content type",x);
		}
	}

}

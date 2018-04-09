package com.ai.htmlgen.streamwriters;

import com.ai.servlets.AspireServletException;

public interface IExtensionToContentTypeMapping 
{
	/**
	 * Will return null if no mapping found
	 * @param fileExtensionWithoutDot
	 * @return
	 * @throws AspireServletException
	 */
	public String getContentType(String fileExtensionWithoutDot)
	throws AspireServletException;
}

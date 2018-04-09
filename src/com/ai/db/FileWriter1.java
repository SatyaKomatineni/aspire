/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;

import java.io.PrintWriter;
import java.util.Map;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.utils.AppObjects;
import com.ai.common.SubstitutorUtils;

/**
 * 
 * Config args
 * **************** 
 * stringToWrite
 * 
 * Derived config args
 * *********************
 * filename (Can accept substitutions, also aspire relative filename)
 * openmode (append, overwrite[default])
 * 
 */
public class FileWriter1 extends FileWriter 
{
	@Override
	protected void writeToUsingAMap(PrintWriter fileWriter, Map args, String requestName) 
	{
		try
		{
			//Use susbstitution
			String encodedValue = AppObjects.getValue(requestName + ".stringToWrite");
			String finalValue = SubstitutorUtils.generalSubstitute(encodedValue, args);
			fileWriter.println(finalValue);
		}
		catch(ConfigException x)
		{
			throw new RuntimeException("stringToWrite is a required property",x);
		}
	}//eom
}//eof-class 

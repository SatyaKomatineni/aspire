package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.Map;
import com.ai.servlets.AspireConstants;

/*
 * Note this class is a singleton.
 * Don't save state in instance variables.
 * Modeled also after DBProcedureObject
 * 
 * Goal
 * *****
 * Makes the AHttpPart NOT a singleton
 * So you can use initialize 
 * 
 * See other related parts
 * *************************
 * @See AHttpPart
 * @see ICreator
 * @see ISingleThreaded
 * @see IInitializable 
 * @see FilterEnabledFactory4
 * 
 */
public abstract class AHttpPartObject extends AHttpPart
implements IInitializable, ISingleThreaded
{
	@Override
	public void initialize(String requestName) 
	{
		try
		{
	    	m_requestName = requestName;
	    	readConfigParametersAndInitialize();
		}
		catch(ConfigException x)
		{
			throw new RuntimeException("Reading config values from AHttpPartObject",x);
		}
	}
	protected String m_requestName;
	protected String readConfigArgument(String argumentKey, String defaultArgumentValue)
	{
		return
		AppObjects.getValue(m_requestName + "." + argumentKey, defaultArgumentValue);
	}
	protected String readMandatoryConfigArgument(String argumentKey) 
	throws ConfigException
	{
		return
		AppObjects.getValue(m_requestName + "." + argumentKey);
	}
	
	@Override
    protected Object executeRequestForHttpPart(String requestName
	         ,HttpServletRequest request
	         ,HttpServletResponse response
	         ,HttpSession session
	         ,Map inArgs)
	         throws RequestExecutionException
    {
    	m_requestName = requestName;
    	return executeRequestForHttpPartObject(request, response, session, inArgs);
    }
   
    //Implement these methods as necessary
    protected abstract void readConfigParametersAndInitialize()
    throws ConfigException;
    protected abstract Object executeRequestForHttpPartObject(HttpServletRequest request
	         ,HttpServletResponse response
	         ,HttpSession session
	         ,Map inArgs)
	         throws RequestExecutionException;
}//eof-class
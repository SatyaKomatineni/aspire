package com.ai.parts;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.ISingleThreaded;
import com.ai.application.utils.AppObjects;
import com.ai.db.DBBaseJavaProcedure;
import com.ai.db.DBException;

/**
 * @author Satya Komatineni
 *
 * Status: Experimental/draft
 * 
 * This will be used as a base class to execute
 * database procedures independent of connections.
 * at the moment it is meant for updates.
 * Needs to be investigated for database reads.
 * 
 * Logic 2/11/2012
 * ****************
 * 1. Stuff the connection into the argument bucket
 * 2. ignore the unnecessary argument bConnectionCreator
 *
 * Caution
 * *********
 * This class is made multi-instance.
 * See if you can implement your solution with a single instance
 * variant of this class DBProcedure.
 * 
 * Key differentiation of this class
 * *************************************
 * You can maintain state in instance variables.
 * 
 * @see ICreator
 * @see ISingleThreaded
 * @see IInitializable
 * @see DBProcedure 
 * @see CollectionWorkSplitterObjectPart
 */
public abstract class DBProcedureObject 
extends DBBaseJavaProcedure 
implements ISingleThreaded 
{
	protected String thisRequestName = null;
	private Map<String,Object> m_argsMap;
	
	public Object executeProcedure(Connection con, 
			boolean bConnectionCreator,
			String requestName, Hashtable arguments) 
			throws DBException,
			SQLException 
    {
		thisRequestName = requestName;
		m_argsMap = arguments;
		//Set up the connection for the pipeline
		Object oldCon = arguments.get("aspire.reserved.jdbc_connection");
		if (oldCon == null)
		{
			AppObjects.info(this,"There is no previous jdbc connection here. It will be placed in there now.");
	        arguments.put("aspire.reserved.jdbc_connection",con);
		}
		else
		{
			AppObjects.info(this,"JDBC connection is already there. It won't be placed in there.");
		}
		return executeDBProcedure(requestName, arguments);
	}
	protected String readConfigArgument(String argumentKey, String defaultArgumentValue)
	{
		return
		AppObjects.getValue(thisRequestName + "." + argumentKey, defaultArgumentValue);
	}
	protected String readConfigArgument(String argumentKey) 
	throws ConfigException
	{
		return
		AppObjects.getValue(thisRequestName + "." + argumentKey);
	}
	protected String readInputStringArgument(String argumentKey, String defaultArgumentValue)
	{
		Object o = m_argsMap.get(argumentKey);
		if (o == null)
		{
			return defaultArgumentValue;
		}
		return (String)o;
	}
	protected String readMandatoryInputStringArgument(String argumentKey) 
	throws DBException
	{
		Object o = m_argsMap.get(argumentKey);
		if (o == null)
		{
			throw new DBException("Expected input argument not found for key:" + argumentKey);
		}
		return (String)o;
	}
	protected abstract Object executeDBProcedure(String requestName, Hashtable arguments) 
		throws DBException;
}

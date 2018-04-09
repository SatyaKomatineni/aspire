package com.ai.parts;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

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
 * This class is a singleton.
 * Don't maintain state in instance variables.
 * If you like that behavior inherit from DBProcedureObject
 * which is multi-instance and will give object semantics.
 * 
 * @see ICreator
 * @see ISingleThreaded
 * @see IInitializable 
 * @see FilterEnabledFactory4
 * @see DBProcedureObject
 * 
 */
public abstract class DBProcedure extends DBBaseJavaProcedure 
{
	public Object executeProcedure(Connection con, 
			boolean bConnectionCreator,
			String requestName, Hashtable arguments) 
			throws DBException,
			SQLException 
    {
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
	protected abstract Object executeDBProcedure(String requestName, Hashtable arguments) 
		throws DBException;
}

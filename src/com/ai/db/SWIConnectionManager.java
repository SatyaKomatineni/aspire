/*
 * Created on Nov 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db;

import java.sql.Connection;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SWIConnectionManager 
{
	private static IConnectionManager m_cm = null;
	static
    {
		try
		{
	      m_cm = (IConnectionManager)AppObjects.getObject(
		  		IConnectionManager.GET_CONNECTION_MANAGER_REQUEST
		  		,null);
		}
		catch(RequestExecutionException x)
		{
			AppObjects.error("SWIConnectionManager","Could not get a connection manager");
			throw new RuntimeException("Could not get a connection manager in swiconnectionmanager");
		}
    }
	public static Connection getConnection(String datasource ) 
	throws DBException
	{
		return m_cm.getConnection(datasource);
	}
	                            
	public static void putConnection(Connection inConnection)
	throws DBException
	{
		m_cm.putConnection(inConnection);
	}
}

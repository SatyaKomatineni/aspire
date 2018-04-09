package com.ai.resourcecleanup;

import java.sql.Connection;

import com.ai.data.DataException;
import com.ai.data.IClosableResource;
import com.ai.db.DBException;
import com.ai.db.DBUtils;

public class ConnectionResource implements IClosableResource 
{
	private Connection m_con = null;
	public ConnectionResource(Connection con)
	{
		m_con = con;
	}
	public void close() throws DataException 
	{
		try
		{
		DBUtils.putConnection(m_con);
		}
		catch(DBException x)
		{
			throw new DataException("Error: Could not return connection to the pool",x);
		}
	}
}//eof-class

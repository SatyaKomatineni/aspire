/*
 * Created on Nov 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;
import com.ai.db.SWIConnectionManager;

/**
 * @author Satya
 *
 * what is this class for
 * **********************
 * 1. Defines a current transactional context, probably on a thread
 * 2. will allow for multiple connections for multiple data sources
 * 3. Knows how to get transactions
 * 4. Knows how to commit and return transactions
 * 5. At a highlevel works like a composite connection
 * 6. A connection nevertheless
 */
public class TransactionalContext implements IDelayedReadResource
{
	
	private Map TConnectionsByDatasourceMap = new HashMap();
	private Map TConnectionsByConnectionsMap = new HashMap();
	private List TConnections = new ArrayList();
	
	public Connection getConnection(String datasourceName)
	throws DBException
	{
		//see if the connection is already there for this
		TransactionalConnection tc = this.getExistingConnection(datasourceName);
		if (tc == null)
		{
			AppObjects.trace(this,"No previous connection for " + datasourceName);
			AppObjects.trace(this,"Requesting a new connection from data source");
			tc = getNewConnection(datasourceName);
			AppObjects.trace(this,"Saving the connection for future use");
			saveAConnection(tc);
			return tc.m_connection;
		}
		else
		{
			AppObjects.trace(this,"returning an existing connection");
			return tc.m_connection;
		}
	}
	
	private TransactionalConnection getExistingConnection(String datasourcename)
	{
		Object ec = this.TConnectionsByDatasourceMap.get(datasourcename);
		return (TransactionalConnection)ec;
	}
	private TransactionalConnection getNewConnection(String datasourcename)
	throws DBException
	{
		try
		{
			Connection c  = SWIConnectionManager.getConnection(datasourcename);
			return new TransactionalConnection(datasourcename,c);
		}
		catch(SQLException x)
		{
			throw new DBException("Problem constructing transactional connection",x);
		}
	}
	private void saveAConnection(TransactionalConnection tc)
	{
		this.TConnectionsByConnectionsMap.put(tc.m_connection,tc);
		this.TConnectionsByDatasourceMap.put(tc.m_datasourceName,tc);
		this.TConnections.add(tc);
	}
	/**
	 * 1. for each connection
	 * 2. roll back if needed
	 *
	 */
	public void rollback() throws DBException
	{
		Iterator tconIter = TConnections.iterator();
		while(tconIter.hasNext())
		{
			TransactionalConnection tc = (TransactionalConnection)tconIter.next();
			rollbackWithoutException(tc);
		}
	}
	
	private void rollbackWithoutException(TransactionalConnection tcon)
	{
		try
		{
			AppObjects.trace(this,"Rolling back tconnection:" + tcon);
			tcon.m_connection.rollback();
		}
		catch(Throwable t)
		{
			AppObjects.error(this,"Problem rollingback connection " + tcon);
		}
	}
	public void commit() throws DBException
	{
		Iterator tconIter = TConnections.iterator();
		while(tconIter.hasNext())
		{
			TransactionalConnection tc = (TransactionalConnection)tconIter.next();
			commitWithoutException(tc);
		}
	}
	
	private void commitWithoutException(TransactionalConnection tcon)
	{
		try
		{
			if (tcon.isCommitNeeded)
			{
				AppObjects.trace(this,"Commit tconnection:" + tcon);
				tcon.m_connection.commit();
			}
			else
			{
				AppObjects.trace(this,"Commit needed flag is false. No commit will happen");
			}
		}
		catch(Throwable t)
		{
			AppObjects.log("Error: Problem commiting connection " + tcon, t);
		}
	}
	public void releaseConnections() throws DBException
	{
		try
		{
			Iterator tconIter = TConnections.iterator();
			while(tconIter.hasNext())
			{
				TransactionalConnection tc = (TransactionalConnection)tconIter.next();
				tc.restoreAutoCommit();
				SWIConnectionManager.putConnection(tc.m_connection);
			}
		}
		catch(SQLException x)
		{
			throw new DBException("Most likely not able to restore auto commit",x);
		}
		
	}
	public void tagAConnectionForCommit(Connection con) throws DBException
	{
		AppObjects.trace(this,"Marking a connection for update:" + con);
		TransactionalConnection tc = this.getTCForConnection(con);
		tc.tagForCommit();
	}
	
	private TransactionalConnection getTCForConnection(Connection con) throws DBException
	{
		return (TransactionalConnection)this.TConnectionsByConnectionsMap.get(con);
	}
	
	//Implementation methods from IDelayedReadResource
	
	/**
	 * Go ahead and release connections from this transactional context
	 */
	public void release() throws DBException
	{
		AppObjects.trace(this,"Call from the delayed read. Releasing connections");
		this.commit();
		this.releaseConnections();
	}

}//eof-class

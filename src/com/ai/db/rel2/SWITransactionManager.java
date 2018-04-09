/*
 * Created on Nov 27, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.sql.Connection;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;

/**
 * @author Satya
 * 1. see ITransactionManager and TransactionManager for documentation
 * 2. A static wrapper for single instance of an ITransactionManager
 */
public class SWITransactionManager 
{
	private static ITransactionManager m_tm = null;
	static
	{
		try
		{
		m_tm = (ITransactionManager)
		AppObjects.getObject(ITransactionManager.NAME,null);
		}
		catch(RequestExecutionException x)
		{
			throw new RuntimeException(x);
		}
	}
	public static Connection getConnection(String datasourceName)
	throws DBException
	{
		return m_tm.getConnection(datasourceName);
	}
	
	public static void tagConnectionForCommit(Connection con)
	throws DBException
	{
		m_tm.tagConnectionForCommit(con);
	}
	//connection like methods. All work on the current context
	public static void commit() throws DBException
	{
		m_tm.commit();
	}
	public static void rollback() throws DBException
	{
		m_tm.rollback();
	}
	public static void release() throws DBException
	{
		m_tm.release();
	}
	
	//methods to deal with current context
	public static void startContext() throws DBException
	{
		m_tm.startContext();
	}
	public static void endContext() throws DBException
	{
		m_tm.endContext();
	}

	//method to deal with initializing the 
	//thread ready for transaction facility
	public static void open() throws DBException
	{
		m_tm.open();
	}
	public static void close() throws DBException
	{
		m_tm.close();
	}
	public static boolean isClosed() throws DBException
	{
		return m_tm.isClosed();
	}
	
	public static boolean isTransactionInPlace() throws DBException
	{
		return m_tm.isTransactionInPlace();
	}
	public static TransactionalContext getCurrentTransactionalContext() throws DBException
	{
		return m_tm.getCurrentTransactionalContext();
	}
	
}

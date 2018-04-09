/*
 * Created on Nov 27, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.sql.Connection;

import com.ai.db.DBException;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ITransactionManager 
{
	public static String NAME="Aspire.TransactionManager";
	
	/**
	 * 1. Make sure you get the same database connection
	 * for a given data source and thread.
	 *  
	 * @param datasourceName
	 * @return
	 * @throws DBException
	 */
	public Connection getConnection(String datasourceName)
	throws DBException;
	
	/**
	 * Indicate that an update took place on this connection
	 * @param con
	 */
	public void tagConnectionForCommit(Connection con)
	throws DBException;

	//connection like methods. All work on the current context
	public void commit() throws DBException;
	public void rollback() throws DBException;
	public void release() throws DBException;
	
	//methods to deal with current context
	public void startContext() throws DBException;
	public void endContext() throws DBException;

	//method to deal with initializing the 
	//thread ready for transaction facility
	public void open() throws DBException;
	public void close() throws DBException;
	public boolean isClosed() throws DBException;
	public boolean isTransactionInPlace() throws DBException;
	public TransactionalContext getCurrentTransactionalContext() throws DBException;
}

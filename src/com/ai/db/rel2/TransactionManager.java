/*
 * Created on Nov 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.sql.Connection;

import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;

/**
 * @author Satya
 *
 * Important
 * **********
 * 1. Remember this is a singleton
 * 2. It infact has a static wrapper
 * 3. No local variables for the class
 */
public class TransactionManager implements ITransactionManager 
{
	public static ThreadLocal s_tl = new ThreadLocal();
	
	/**
	 * 1. get a connection from the current transactional context
	 * 2. Start a new  
	 */
	public Connection getConnection(String datasourceName) throws DBException 
	{
		AppObjects.trace(this,"Requesting a connection from transaction manager");
		TransactionalContext tc = this.getCurrentContext();
		if (tc == null)
		{
			AppObjects.trace(this,"There is no context active. will be creating a new one");
			this.startContext();
			tc = this.getCurrentContext();
		}
		AppObjects.trace(this,"Obtaining a connection from context:" + tc);
		return tc.getConnection(datasourceName);
	}

	/* (non-Javadoc)
	 * @see com.ai.db.rel2.ITransactionManager#tagConnectionForCommit(java.sql.Connection)
	 */
	public void tagConnectionForCommit(Connection con) 
	throws DBException
	{
		// TODO Auto-generated method stub
		this.getCurrentContext().tagAConnectionForCommit(con);

	}
	/**
	 * Remove the transaction manager from the thread
	 * so that the thread can be recycled
	 * or any such operation
	 * @throws DBException
	 */
	public void close()	throws DBException
	{
		AppObjects.trace(this,"Closing transaction manager. setting thread local to null");
		s_tl.set(null);
	}
	public boolean isClosed() throws DBException
	{
		if (s_tl.get() != null)
		{
			//Thread local has a valid object
			//so the manager is active
			return false;
		}
		else
		{
			//thread local object is null
			return true;
		}
	}
	
	/**
	 * 1. retrieve the transaction facility object from thread local
	 * 2. if it is not there return null
	 * @return
	 */
	private TransactionFacilityTL getTransactionFacilityTL()
	{
		return (TransactionFacilityTL)s_tl.get();
	}
	/**
	 * Start the thread local with a transaction facility
	 *
	 */
	public void open() throws DBException
	{
		AppObjects.trace(this,"Opening up a transaction manager for this thread.");
		s_tl.set(new TransactionFacilityTL());
	}
	
	/**
	 * 1. Starts a new context on this thread
	 * 2. The new context will have its own set of connections 
	 * 		separate from the previous context
	 * 3.
	 * 
	 * Doubts
	 * *******
	 * 1. this method could have returned a context object
	 * 2. then the client will be exposed to that object
	 * 3. May be I will expose it as an interface in the future
	 * 4. This is for experimentation for now
	 *
	 */
	public void startContext() throws DBException
	{
		if (this.isClosed())
		{
			AppObjects.warn(this,"startcontext called when the transaction manager is not open");
			AppObjects.warn(this,"manager will be open now");
			this.open();
		}
		this.getTransactionFacilityTL().startContext(new TransactionalContext());
	}
	
	private TransactionalContext getCurrentContext() throws DBException
	{
		if (this.isClosed())
		{
			//transaction facility is not active yet
			return null;
		}
		else
		{
			return this.getTransactionFacilityTL().getCurrentContext();
		}
	}

	/**
	 * 1. End the current context
	 * @throws DBException
	 */
	public void endContext() throws DBException
	{
		this.getTransactionFacilityTL().endCurrentContext();
	}
	//connection like methods. All work on the current context
	public void commit() throws DBException
	{
		this.getCurrentContext().commit();
	}
	public void rollback() throws DBException
	{
		this.getCurrentContext().rollback();
	}
	
	public void release() throws DBException
	{
		this.getCurrentContext().releaseConnections();
	}
	public boolean isTransactionInPlace() throws DBException
	{
		if (this.getCurrentContext() != null)
		{
			return true;
		}
		return false;
	}//eof-function
	
	public TransactionalContext getCurrentTransactionalContext() throws DBException
	{
		return this.getCurrentContext();
	}
}//eof-class

/*
 * Created on Nov 25, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.CommonException;
import com.ai.db.DBException;

/**
 * @author Satya
 * 
 * Main comments
 * ********************************
 * 1. This is the primary piece of code where the transaction semantics are implemented
 * 
 * what is supposed to happen
 * ******************************
 * 1. get the database name
 * 2. get a connection
 * 3. call the derived and return
 * 
 * But ...
 * *********************************
 * 1. If I am the transaction owner, release it at the end
 * 2. IT doesn't matter if it is rolledback or otherwise
 * 3. It doesn't matter if it is transferred to someone else like a delayed collection or not
 * 4. If I am not the transaction owner, then just don't touch it, and let it pass
 * 
 * Additional Notes
 * *********************************
 * 1. I have to worry about setting the autocommit to false
 * 2. Should I restore the autocommit to a previous value
 * 3. tag for update
 * 4. A commit is done only if the owner is of type update
 * 5. otherwise even though an update is pending, a commit is not in place
 *
 */
public class TMBaseJavaProcedure implements IProcedureBaseExtender
{
	   public Object executeRequestForPart(DBBaseJavaProcedure3 proc
	   		,String requestName
			, Map inArgs)
	   throws RequestExecutionException, SQLException
	   {
	   		boolean bTxnOwner = startTxIfNeeded();
			boolean bExceptionDetected = false;
	   		Object returnObject = null;
	   		try
			{
	   			returnObject =
	   				this.realExecuteRequestForPart1(bTxnOwner,proc,requestName,inArgs);
	   			return returnObject;
			}
	   		catch(Throwable t)
			{
	   			bExceptionDetected = true;
	   			if (t instanceof RequestExecutionException)
	   			{
	   				throw (RequestExecutionException)t;
	   			}
	   			else
	   			{
	   				throw new RequestExecutionException("Error:rethrowing as ref",t);
	   			}
			}
	   		finally
			{
	   			if (bTxnOwner == true)
	   			{
	   				AppObjects.trace(this,"I am the txn owner. Need to release and close the txn");
	   				if (bExceptionDetected == true)
	   				{
	   					this.releaseAndCloseContextOnExceptionNEF();
	   				}
	   				else
	   				{
	   					//Release resources conditionally
	   					releaseAndCloseContextNEF(returnObject);
	   				}
	   			}
	   			else
	   			{
	   				AppObjects.trace(this,"I am not the txn owner. Nothing to do. No need to release resources or close context");
	   			}
			}
	   }//eof-function
	   
	   /**
	    * what is supposed to happen
	    * ***************************
	    * 1. Release the context
	    * 2. It is assumed that this part is the owner already
	    * 3. Release it right away, or close, if the returned object is not a delayed read
	    * 4. If it is a delayed read 
	    */
	   private void releaseAndCloseContextNEF(Object returnObject)
	   {
	   		try
			{
	   			if (returnObject instanceof ITransactionExtender)
	   			{
	   				this.releaseAndCloseContextTransactionExtender(returnObject);
	   			}
	   			else if (returnObject instanceof IDelayedRead)
	   			{
	   				this.releaseAndCloseContextDelayedRead(returnObject);
	   			}
	   			else
	   			{
	   				this.releaseAndCloseContextPlainObject(returnObject);
	   			}
			}
	   		catch (Throwable x)
			{
	   			AppObjects.log("Error:Unexpected exception trying to release contexts. Being in finally this exception is not propagated.",x);
			}
	   }
	   private void releaseAndCloseContextOnExceptionNEF() 
	   {
	   	try
		{
			AppObjects.trace(this,"Error: exception detected while getting the object.");
			SWITransactionManager.release();
			//Closing context or remove context from the thread
			AppObjects.trace(this,"Ending context or remove context from the thread");
			SWITransactionManager.endContext();
			return;
		}
   		catch (Throwable x)
		{
   			AppObjects.log("Error:Unexpected exception trying to release contexts. Being in finally this exception is not propagated.",x);
		}
	   }
	   
	   private void releaseAndCloseContextPlainObject(Object returnObject) 
	   throws DBException
	   {
			AppObjects.trace(this,"Returned object is a plain object. Releasing resources");
			SWITransactionManager.release();
			//Closing context or remove context from the thread
			AppObjects.trace(this,"Ending context or remove context from the thread");
			SWITransactionManager.endContext();
			return;
	   }
	   
	   private void releaseAndCloseContextDelayedRead(Object returnObject) 
	   throws DBException
	   {
			AppObjects.trace(this,"Returned object is a delayed read");
			TransactionalContext tc = SWITransactionManager.getCurrentTransactionalContext();
			
			IDelayedRead idr = (IDelayedRead)returnObject;
			if (idr.isDelayedReadActive() == false)
			{
				AppObjects.trace(this,"Delayed read is currently not active. Releasing resources");
				SWITransactionManager.release();
				//Closing context or remove context from the thread
				AppObjects.trace(this,"Ending context or remove context from the thread");
				SWITransactionManager.endContext();
				return;
			}
			
			AppObjects.trace(this,"Delayed read is active. Transfering context to the object. Not releasing resources");
			idr.registerResource(tc);
			
			AppObjects.trace(this,"Ending context or remove context from the thread");
			SWITransactionManager.endContext();
	   }

	   private void releaseAndCloseContextTransactionExtender(Object returnObject) 
	   throws DBException
	   {
			AppObjects.trace(this,"Returned object is a transaction extender. ownership is going to be transferred");
			
			ITransactionExtender idr = (ITransactionExtender)returnObject;
			if (idr.isExtensionRequired() == false)
			{
				AppObjects.trace(this,"Extender is not active. Releasing resources");
				SWITransactionManager.release();
				//Closing context or remove context from the thread
				AppObjects.trace(this,"Ending context or remove context from the thread");
				SWITransactionManager.endContext();
				return;
			}
			
			AppObjects.trace(this,"Extender is active. Enabling and transfering ownership");
			idr.enableExtension();
	   }
	 	   
	   private boolean startTxIfNeeded() throws RequestExecutionException
	   {
	   		try
			{
	   		boolean tinp = SWITransactionManager.isTransactionInPlace();
	   		if (tinp == true)
	   		{
	   			AppObjects.trace(this,"Transaction is already in place");
	   			return false;
	   		}
	   		else
	   		{
	   			AppObjects.trace(this,"New transaction context is going to be started");
	   			SWITransactionManager.startContext();
	   			return true;
	   		}
			}
	   		catch(DBException x)
			{
	   			throw new RequestExecutionException("Error: Problem starting transactional context",x);
			}
	   }//eof-function startTxIfNeeded

	   /**
	    * Close context and remove it from the thread
	    * @throws RequestExecutionException
	    */
	   private void closeContextDeprecated() throws RequestExecutionException
	   {
	   		try
			{
	   			SWITransactionManager.endContext();
			}
	   		catch(DBException x)
			{
	   			throw new RequestExecutionException("Could not end context",x);
			}
	   }
	   /**
	    * will be called from finall and no exception is expected back
	    * NEF stands for : No exception, called from finally
	    * 
	    * 1. Call the rollback on the current transaction
	    * 2. It is possible that the rollback might conditionally do it based on tagging
	    *
	    */
	   private void rollbackChangesNEF() 
	   {
	   		try
			{
	   			SWITransactionManager.rollback();
			}
	   		catch(DBException x)
			{
	   			AppObjects.log("Error: Failed to rollback. Due to finally this exception is not propagated",x);
			}
	   }
	   private void commitChangesNEF()
	   {
	   		try
			{
	   			SWITransactionManager.commit();
			}
	   		catch(DBException x)
			{
	   			AppObjects.log("Error: Failed to comiit. Due to finally this exception is not propagated",x);
			}
	   }
	   
	   /**
	    * 
	    * @param txnOwner
	    * @param proc
	    * @param requestName
	    * @param inArgs
	    * @return
	    * @throws RequestExecutionException
	    * @throws SQLException
	    * 
	    * 1. Rollback or commit
	    * 2. will call the real proc
	    * 
	    */
	   public Object realExecuteRequestForPart1(boolean txnOwner
	   		,DBBaseJavaProcedure3 proc
	   		,String requestName
			, Map inArgs)
	   throws RequestExecutionException, SQLException
	   {
   		boolean bExceptionDetected = false;
   		Object returnObject = null;
   		String queryType = AppObjects.getValue(requestName + ".query_type","query");
   		try
		{
   			returnObject = realExecuteRequestForPart(proc,requestName,inArgs);
   			return returnObject;
		}
   		catch(Throwable t)
		{
   			bExceptionDetected = true;
   			//throw new RequestExecutionException("Error: Problem executing a procedure",t);
			if (t instanceof RequestExecutionException)
			{
				throw (RequestExecutionException)t;
			}
			else
			{
				throw new RequestExecutionException("Error: Problem executing a procedure realExecuteRequestForPart",t);
			}
		}
   		finally
		{
   			if (!txnOwner)
   			{
   				AppObjects.trace(this,"I am not the transaction owner. No need to commit or rollback");
   			}
   			else
   			{
   				AppObjects.trace(this,"I am the transaction owner");
   				if (bExceptionDetected == true)
   				{
   					AppObjects.trace(this,"Exception detected rolling back");
   					this.rollbackChangesNEF();
   				}//exception
   				else
   				{
   					AppObjects.trace(this,"No exception. Going to commit if not a delayed read");
					if (!isCommitNeededNow(returnObject))
   					//if (returnObject instanceof IDelayedRead)
   					{
						AppObjects.trace(this,"Commit will happen later");
   					}
   					else
   					{
						AppObjects.trace(this,"All work done. Commit if needed");
						this.commitChangesNEF();
   					}
   				}//no exception
   			}//not a transaction owner
		}//eof-finally
	   }//eof-function
	   
	   private boolean isCommitNeededNow(Object returnObject)
	   {
	   		if (returnObject instanceof IDelayedRead)
	   		{
	   			AppObjects.trace(this,"This is a delayed read. commit postponed");
	   			return false;
	   		}
	   		if (returnObject instanceof ITransactionExtender)
	   		{
	   			AppObjects.trace(this,"This is a Transaction extender. commit postponed");
	   			return false;
	   		}
   			AppObjects.trace(this,"This is neither a transactione extender or a delayed read");
	   		return true;
	   }
	   

	   /**
	    * 1. Actual execution
	    * 2. Gets the connection
	    * 3. Tags it for update or query
	    */
	   public Object realExecuteRequestForPart(DBBaseJavaProcedure3 proc
	   		,String requestName
			, Map inArgs)
	   throws RequestExecutionException, SQLException
	   {
	   	
   		try
		{
	   		String datasourceName = AppObjects.getValue(requestName + ".db");
	   		String queryType = AppObjects.getValue(requestName + ".query_type","query");
	   		Connection con = SWITransactionManager.getConnection(datasourceName);
	   		Object returnObj = proc.executeProcedure(con,requestName,(Hashtable)inArgs);
	   		if (queryType.equalsIgnoreCase("update"))
	   		{
	   			AppObjects.trace(this,"commit recognized");
	   			SWITransactionManager.tagConnectionForCommit(con);
	   		}
	   		return returnObj;
		}
   		catch(CommonException x)
		{
   			throw new RequestExecutionException("One of Aspires exceptions",x);
		}
	   }
}//eof-class

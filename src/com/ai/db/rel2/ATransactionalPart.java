/*
 * Created on Dec 14, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.sql.SQLException;
import java.util.Map;

import com.ai.application.interfaces.AFactoryPart;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;

/**
 * @author Satya
 * Anything inherited will be wrapped with a transactional context
 * 
 */
public abstract class ATransactionalPart extends AFactoryPart 
{
	protected abstract Object executeRequestForPartTM(String requestName, Map inArgs)
			throws RequestExecutionException;
	
	protected Object executeRequestForPart(String requestName, Map inArgs)
	throws RequestExecutionException 
	{
   		boolean bTxnOwner = startTxIfNeeded();
		boolean bExceptionDetected = false;
   		Object returnObject = null;
   		try
		{
   			returnObject =
   				this.realExecuteRequestForPart1(bTxnOwner,requestName,inArgs);
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
	   		,String requestName
			, Map inArgs)
	   throws RequestExecutionException
	   {
			boolean bExceptionDetected = false;
			Object returnObject = null;
			try
			{
				returnObject = executeRequestForPartTM(requestName,inArgs);
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
					throw new RequestExecutionException("Error: Problem executing a procedure executeRequestForPartTM",t);
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
					}
					else
					{
						AppObjects.trace(this,"No exception. Going to commit if not a delayed read");
						if (!isCommitNeededNow(returnObject))
						{
							AppObjects.trace(this,"Commit will happen later");
						}
						else
						{
							AppObjects.trace(this,"All work done. Commit if needed");
							this.commitChangesNEF();
						}
					}
				}//case: transaction owner
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
}//eof-class

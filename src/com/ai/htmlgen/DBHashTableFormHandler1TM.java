/*
 * Created on Dec 15, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.htmlgen;

import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;
import com.ai.db.rel2.IDelayedRead;
import com.ai.db.rel2.IDelayedReadResource;
import com.ai.db.rel2.ITransactionExtender;
import com.ai.db.rel2.SWITransactionManager;

/**
 * @author Satya
 *
 */
public class DBHashTableFormHandler1TM extends DBHashTableFormHandler1
implements IDelayedRead, ITransactionExtender
{
	public boolean isDelayedReadActive() throws DBException
	{
		return true;
	}
	
	private IDelayedReadResource m_delayedReadResource = null;
	public void registerResource(IDelayedReadResource resource) throws DBException
	{
		m_delayedReadResource = resource;
	}
	
	/**
	 * Remember this is going to be called from finally
	 */
	public void formProcessingComplete()
	{
		super.formProcessingComplete();
		try
		{
			if (bExtend == true)
			{
				this.closeSuccess();
			}
		}
		catch(DBException x)
		{
			AppObjects.log("Error: Closing an hds",x);
		}
	}
 
	private boolean bExtend = false;
	public void enableExtension() throws DBException
	{
		bExtend = true;
	}
	
	public boolean isExtensionRequired() throws DBException
	{
		return true;
	}
	public void closeSuccess() throws DBException
	{
		//commit
		//release
		//close
		SWITransactionManager.commit();
		SWITransactionManager.release();
		SWITransactionManager.endContext();
	}
	public void closeFailure() throws DBException
	{
		closeSuccess();
	}
	
}//eof-class

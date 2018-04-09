/*
 * Created on Dec 15, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import com.ai.db.DBException;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ITransactionExtender 
{
	public void enableExtension() throws DBException;
	public boolean isExtensionRequired() throws DBException;
	public void closeSuccess() throws DBException;
	public void closeFailure() throws DBException;
}

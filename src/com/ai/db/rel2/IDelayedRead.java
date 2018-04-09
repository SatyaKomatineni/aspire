/*
 * Created on Dec 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import com.ai.db.DBException;

/**
 * @author Satya
 *
 */
public interface IDelayedRead 
{
	public boolean isDelayedReadActive() throws DBException;
	public void registerResource(IDelayedReadResource resource) throws DBException;
}

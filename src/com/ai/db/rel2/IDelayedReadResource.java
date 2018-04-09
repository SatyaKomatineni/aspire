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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IDelayedReadResource 
{
	public void release() throws DBException;
}

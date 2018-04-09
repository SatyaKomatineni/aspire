/*
 * Created on Nov 7, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.ps;

import com.ai.data.DataException;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ISpecificTypeConverter 
{
	public Object convert(Object srcObject)
	throws DataException;
}

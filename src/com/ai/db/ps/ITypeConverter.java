/*
 * Created on Nov 7, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.ps;

import com.ai.data.DataException;

/**
 * To convert one type to another
 * especially from a string to that type
 * Initially meant to serve database conversions
 */
public interface ITypeConverter 
{
	public Object convert(Object srcObject, String hint)
	throws DataException;
}

/*
 * Created on Nov 9, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.ps;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.data.DataException;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TypeConverterUtility 
{
	static ITypeConverter genericTypeConverter = null;
	static
	{
		try
		{
			genericTypeConverter = 
				(ITypeConverter)
				AppObjects.getObject("aspire.generictypeconverter",null);
		}
		catch(RequestExecutionException x)
		{
			throw new RuntimeException("Error:can not obtain generic type converter",x);
		}
			
	}
	public static Object convert(Object srcObject, String hint)
	throws DataException
	{
		return genericTypeConverter.convert(srcObject,hint);
	}
}

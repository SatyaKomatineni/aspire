/*
 * Created on Nov 7, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.ps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.ISingleThreaded;
import com.ai.application.utils.AppObjects;
import com.ai.data.DataException;

/**
 * @author Satya
 *
 */
public class GenericTypeConverter implements ITypeConverter, IInitializable, ISingleThreaded
{
	private String requestName = null;
	private String locale=null;
	private String dateFormat=null;
	private String dateTimeFormat=null;
	
	public void initialize(String inRequestName) 
	{
		requestName = inRequestName;
		locale=this.getLocale();
		dateFormat=this.getDateFormatString();
		dateTimeFormat=this.getDateTimeFormatString();
	}
	private String getDateFormatString()
	{
		return AppObjects.getValue(requestName + ".dateFormat","MM/dd/yy");
	}
	
	private String getDateTimeFormatString()
	{
		return AppObjects.getValue(requestName + ".dateTimeFormat","MM/dd/yy hh:mm a");
	}
	
	private String getLocale()
	{
		String curLocale = Locale.getDefault().toString();
		return AppObjects.getValue(requestName + ".locsale",curLocale);
	}
	/**
	 * Virtual method inherited from ITypeConverter
	 */
	public Object convert(Object srcObject, String hint) 
	throws DataException 
	{
		if (hint.equalsIgnoreCase(TypeEnum.INT))
		{
			return convertToInt(srcObject);
		}
		else if (hint.equalsIgnoreCase(TypeEnum.LONG))
		{
			return convertToLong(srcObject);
		}
		else if (hint.equalsIgnoreCase(TypeEnum.SHORT))
		{
			return convertToShort(srcObject);
		}
		else if (hint.equalsIgnoreCase(TypeEnum.FLOAT))
		{
			return convertToFloat(srcObject);
		}
		else if (hint.equalsIgnoreCase(TypeEnum.DOUBLE))
		{
			return convertToDouble(srcObject);
		}
		else if (hint.equalsIgnoreCase(TypeEnum.DATE))
		{
			return convertToDate(srcObject);
		}
		else if (hint.equalsIgnoreCase(TypeEnum.DATETIME))
		{
			return convertToDatetime(srcObject);
		}
		throw new DataException("Invalid type for hint:" + hint);
	}//eof-function
	
//************************************************************
//* Specific conversion methods
//************************************************************	
	private Integer convertToInt(Object srcObject)
	{
		String s = (String)srcObject;
		return new Integer(s);
	}
	private Long convertToLong(Object srcObject)
	{
		String s = (String)srcObject;
		return new Long(s);
	}
	private Short convertToShort(Object srcObject)
	{
		String s = (String)srcObject;
		return new Short(s);
	}
	private Float convertToFloat(Object srcObject)
	{
		String s = (String)srcObject;
		return new Float(s);
	}
	private Double convertToDouble(Object srcObject)
	{
		String s = (String)srcObject;
		return new Double(s);
	}
	
	private Date convertToDate(Object srcObject) throws DataException
	{
		try
		{
			String s = (String)srcObject;
			SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat, 
					new Locale(this.locale)); 
			return sdf.parse(s);
		}
		catch(ParseException x)
		{
			throw new DataException("Error:Problem parsing date:" + (String)srcObject,x);
		}
	}
	private Date convertToDatetime(Object srcObject) throws DataException
	{
		try
		{
			String s = (String)srcObject;
			SimpleDateFormat sdf = new SimpleDateFormat(this.dateTimeFormat, 
					new Locale(this.locale)); 
			return sdf.parse(s);
		}
		catch(ParseException x)
		{
			throw new DataException("Error:Problem parsing date time:" + (String)srcObject,x);
		}
	}
}//eof-class


package com.ai.reflection;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils 
{
    private static DateFormat dateFormatter 
				= DateFormat.getDateInstance(DateFormat.LONG);

    private static DateFormat dateTimeFormatter 
				= DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG);
    
    
    private static Map<String,String> fundamentalTypeNamesMap = new HashMap<String,String>();
    static 
    {
        fundamentalTypeNamesMap.put("int","int");
        fundamentalTypeNamesMap.put("short","short");
        fundamentalTypeNamesMap.put("long","long");
        fundamentalTypeNamesMap.put("float","double");
        fundamentalTypeNamesMap.put("java.lang.String","java.lang.String");
        fundamentalTypeNamesMap.put("java.util.Date","java.util.Date");
        fundamentalTypeNamesMap.put("java.util.Calendar","java.util.Calendar");
    }
	
    public static boolean amIAFundamentalType(String typeName)
    {
        Object name = ReflectionUtils.fundamentalTypeNamesMap.get(typeName);
        return (name != null);
    }
	//This can be further optimized using functors
	//For now this will do
	public static Object getStringAsObject(String typeName, String value)
	{
		if (typeName.equals("java.lang.String"))
		{
			return value;
		}
		if (typeName.equals("int"))
		{
			return getStringAsInteger(value);
		}
		
		if (typeName.equals("java.util.Calendar"))
		{
			return getStringAsCalendar(value);
		}
		
		if (typeName.equals("long"))
		{
			return getStringAsLong(value);
		}
		if (typeName.equals("double"))
		{
			return getStringAsDouble(value);
		}
		if (typeName.equals("float"))
		{
			return getStringAsFloat(value);
		}
		throw new RuntimeException("Unrecognized type encountered:" + typeName);
	}
	
	public static Integer getStringAsInteger(String value)
	{
		return new Integer(Integer.parseInt(value));
	}

	public static Long getStringAsLong(String value)
	{
		return new Long(Long.parseLong(value));
	}
	
	public static Double getStringAsDouble(String value)
	{
		return new Double(Double.parseDouble(value));
	}
	
	public static Float getStringAsFloat(String value)
	{
		return new Float(Float.parseFloat(value));
	}
	
	public static Calendar getStringAsCalendar(String value)
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(ReflectionUtils.dateTimeFormatter.parse(value));
			return cal;
		}
		catch(ParseException x)
		{
			RuntimeException rt = 
				new RuntimeException("Could not parse a timestamp:" + value);
			throw rt;
		}
	}
	
	public static String getValueAsString(Object o)
	{
		if (o instanceof Calendar)
		{
			//do this
			Calendar cal = (Calendar)o;
	        return dateTimeFormatter.format(cal.getTime());
		}
		return o.toString();
	}
	public static String convertObjectToString(Object o) throws ReflectionException
	{
	    BeanInfo bi = BeanInfoRegistry.getBeanInfo(o.getClass());
	    return bi.convertToString(o);
	}
}//eof-class

package com.ai.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FieldInfo 
{
	//For clarity of code I am not generating the getters and setters
	//Generate them before production
	public Method m_getAccessor = null;
	public Method m_setAccessor = null;
	public Field  m_field = null;
	public String m_name = null;
	public String m_xmlName = null;
	public boolean m_attribute = false;
	private Class m_class = null;
	private Class m_fieldType = null;
	private boolean m_bFundamentalType = false;
	
	
	
	public FieldInfo(Class fieldClass, String name, String xmlName, boolean isAttribute)
		throws NoSuchMethodException, NoSuchFieldException
	{
		m_name = name;
		m_xmlName = xmlName;
		m_attribute = isAttribute;
		m_class = fieldClass;
		m_fieldType = getFieldType(name);
		m_getAccessor = getAccessorGetMethod(name);
		m_setAccessor = getAccessorSetMethod(name);
		m_field = this.getPublicField(name);
		m_bFundamentalType = this.getIsFundamentalType();
	}
	
	
	public FieldInfo(Class fieldClass,String name, String xmlName) 
	throws NoSuchMethodException, NoSuchFieldException
	{
		this(fieldClass,name,xmlName,false);
	}
	
	public FieldInfo(Class fieldClass, String name) 
	throws NoSuchMethodException, NoSuchFieldException
	{
		this(fieldClass,name,name,false);
	}
	
	public String getValueAsString(Object o)
	throws InvocationTargetException, IllegalAccessException, ReflectionException
	{
	    if (m_getAccessor != null)
	    {
	        return getValueAsStringUsingGetMethod(o);
	    }
	    //getaccessor is null.
	    //There is no get method
	    //use the public field to get it
	    if (m_field != null)
	    {
	        Object value = m_field.get(o);
	        return value.toString();
	    }
	    throw new ReflectionException("Error:No get method or public member with this name:" + m_name);
	}
	
	public String getValueAsStringUsingGetMethod(Object o)
	throws InvocationTargetException, IllegalAccessException
	{
		Object reply = m_getAccessor.invoke(o,null);
		if (reply != null)
		   return reply.toString();
	    else 
	       return ""; 	
	}
	
	private Method getAccessorGetMethod(String fieldName)
		throws NoSuchMethodException
	{
	    try
	    {
			String firstPart = fieldName.substring(0,1);
			String secondPart = fieldName.substring(1);
			String getMethodName = "get" + firstPart.toUpperCase() + secondPart;
			//System.out.println(getMethodName);
			return m_class.getDeclaredMethod(getMethodName,null);
	    }
	    catch(NoSuchMethodException x)
	    {
	        return null;
	    }
	}
	
	private Field getPublicField(String fieldName)
	{
	    try
	    {
	        Field fi = m_class.getDeclaredField(fieldName);
	        return fi;
	    }
	    catch(NoSuchFieldException x)
	    {
	        return null;
	    }
	}
	private Method getAccessorSetMethod(String fieldName)
	throws NoSuchMethodException
	{
	    try
	    {
			String firstPart = fieldName.substring(0,1);
			String secondPart = fieldName.substring(1);
			String getMethodName = "set" + firstPart.toUpperCase() + secondPart;
			//System.out.println(getMethodName);
			Class parameters[] = new Class[1];
			parameters[0] = this.m_fieldType;
			
			return m_class.getDeclaredMethod(getMethodName,parameters);
	    }
	    catch(NoSuchMethodException x)
	    {
	        return null;
	    }
	}
	
	private Class getFieldType(String fieldName)
		throws NoSuchFieldException
	{
		Field field = m_class.getDeclaredField(fieldName);
		Class fieldClass = field.getType();
		return fieldClass;
	}
	
	private String getTypeName(String fieldName)
	{
		return this.m_fieldType.getName();
	}
	
	public void setValueUsingString(Object o, String fieldValue)
	throws ReflectionException
	{
	    if (m_setAccessor != null)
	    {
	        setValueUsingStringUsingSetMethod(o,fieldValue);
	        return;
	    }
	    //set accessor is null
	    //try it with a public field
	    
	    if (m_field != null)
	    {
	        //public field is available
	        setValueUsingStringUsingPublicField(o,fieldValue);
	        return;
	    }
	    //There is no public field
	    throw new ReflectionException("Error:No public member or a getmethod for this name:" + this.m_name);
	}
	
	public void setValueUsingStringUsingPublicField(Object o, String fieldValue)
	throws ReflectionException
	{
		try
		{
			Object parameter = 
			    ReflectionUtils.getStringAsObject(m_fieldType.getName(),fieldValue);
		    m_field.set(o,parameter);
		}
		catch(IllegalAccessException x)
		{
			throw new ReflectionException("Error:IllegalAccessException",x);
		}
	}
	
	public void setValueUsingStringUsingSetMethod(Object o, String fieldValue)
	throws ReflectionException
	{
		try
		{
			Object parameter = ReflectionUtils.getStringAsObject(m_fieldType.getName(),fieldValue);
			Object paramArray[] = new Object[1];
			paramArray[0] = parameter;
			Object reply = m_setAccessor.invoke(o,paramArray);
		}
		catch(InvocationTargetException x)
		{
			throw new ReflectionException("Error:InvocationTargetException",x);
		}
		catch(IllegalAccessException x)
		{
			throw new ReflectionException("Error:IllegalAccessException",x);
		}
	}
	
	private boolean getIsFundamentalType()
	{
	    return ReflectionUtils.amIAFundamentalType(m_fieldType.getName());
	}
	
	public boolean isFundamentalType()
	{
	    return m_bFundamentalType;
	}
}

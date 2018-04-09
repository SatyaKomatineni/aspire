package com.ai.reflection;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BeanInfo 
{
	private Class m_class;

	private List<String> m_fieldList = new ArrayList<String>();
	private List<FieldInfo> m_FieldInfoList = new ArrayList<FieldInfo>();
	private Map<String, FieldInfo>  m_FieldInfoMap = new HashMap<String,FieldInfo>();
	
	private static boolean xmlMapInitialized = false;
	private static Map<String,FieldInfo> m_xmlNameMap = new HashMap<String,FieldInfo>();
	
	private String xmlNodeName = null;
	
	/**
	 * @return Returns the xmlNodeName.
	 */
	public String getXmlNodeName() {
		return xmlNodeName;
	}
	/**
	 * @param xmlNodeName The xmlNodeName to set.
	 */
	public void setXmlNodeName(String xmlNodeName) {
		this.xmlNodeName = xmlNodeName;
	}
	public BeanInfo(Object bean) 
	throws NoSuchMethodException, NoSuchFieldException
	{
		this(bean.getClass());
	}
	public Object constructObject() throws ReflectionException
	{
		try
		{
			return m_class.newInstance();
		}
		catch(IllegalAccessException x)
		{
			throw new ReflectionException("Error:Illegal Access Exception",x);
		}
		catch(InstantiationException x)
		{
			throw new ReflectionException("Error:InstantiationException",x);
		}
	}
	public BeanInfo(Class inClass) 
		throws NoSuchMethodException, NoSuchFieldException
	{
		m_class = inClass;
		
		Field fieldArray[] = m_class.getDeclaredFields();
		for(int i=0;i<fieldArray.length;i++)
		{
			Field thisField = fieldArray[i];
			if (!isStatic(thisField))
						{
						String fieldName = thisField.getName();
						registerField(fieldName);
						}
			
		}
		
		xmlNodeName = m_class.getName();
	}
	
	public Iterator<String> getFieldNames()
	{
		return m_fieldList.iterator();
	}
	
	public Iterator<FieldInfo> getFieldInfos()
	{
		return m_FieldInfoList.iterator();
	}
	
	private boolean isStatic(Field field)
		{
			int modifier = field.getModifiers();
			return Modifier.isStatic(modifier);
		}


	
	private void registerField(String fieldName)
		throws NoSuchMethodException, NoSuchFieldException
	{
		m_fieldList.add(fieldName);
		FieldInfo fi = new FieldInfo(m_class,fieldName);
		this.m_FieldInfoMap.put(fieldName, fi);
		m_FieldInfoList.add(fi);
	}
	
	public String getValueAsString(String fieldName, Object o)
		throws InvocationTargetException, IllegalAccessException, ReflectionException
	{
		FieldInfo fi = (FieldInfo)m_FieldInfoMap.get(fieldName);
		return fi.getValueAsString(o);
		
	}
	public void registerMapping(String fieldName, String xmlName)
	{
		registerMapping(fieldName, xmlName, false);
	}
	
	public void registerMapping(String fieldName, String xmlName, boolean isAttribute)
	{
		FieldInfo fi = (FieldInfo)m_FieldInfoMap.get(fieldName);
		if (fi == null)
		{
			throw new RuntimeException("No field information found for field:" + fieldName);
		}
		fi.m_xmlName=xmlName;
		fi.m_attribute = isAttribute;
	}
	
	public void unRegisterMapping(String fieldName)
	{
		FieldInfo fi = (FieldInfo)m_FieldInfoMap.get(fieldName);
		if (fi == null)
		{
			throw new RuntimeException("No field information found for field:" + fieldName);
		}
		m_FieldInfoMap.remove(fi);
	}
	
	public FieldInfo getFieldInfo(String xmlName)
	{
		initializeXmlMap();
		FieldInfo fi = (FieldInfo)m_xmlNameMap.get(xmlName);
		return fi;
	}
	
	private void initializeXmlMap()
	{
		if (xmlMapInitialized == true) return;
		
		Iterator<FieldInfo> fieldInfos = getFieldInfos();
		while(fieldInfos.hasNext())
		{
			FieldInfo fi = (FieldInfo)fieldInfos.next();
			String xmlName = fi.m_xmlName;
			m_xmlNameMap.put(xmlName,fi);
		}
	}
	
	public String convertToString(Object o)
	{
		//Make sure the types are right 
		String myClassName = m_class.getName();
		String inClassName = o.getClass().getName();
		if (!(myClassName.equals(inClassName)))
		{
			throw new RuntimeException("Unexpected classname. Expecting " + myClassName + " but got " + inClassName);
		}
		
		//Get a buffer
		StringBuffer sbuf = new StringBuffer();
		
		//Get the header
		sbuf.append("\nPrinting " + myClassName);
		
		//good to go
		//Get all public fields
		Iterator<FieldInfo> fieldInfos = this.getFieldInfos();
		while(fieldInfos.hasNext())
		{
			FieldInfo fi = (FieldInfo)fieldInfos.next();
			String fieldName = fi.m_name;
			String fieldXmlName = fi.m_xmlName;
			String value = "";
			try
			{
				value = fi.getValueAsString(o);
			}
			catch(Exception x)
			{
				x.printStackTrace();
				value="Exception. The message is:" + x.getMessage();
			}
			sbuf.append("\n<" + fieldName + ">" + value + "</" + fieldName + ">");
		}
		return sbuf.toString();
	}
	
	public void optimize()
	{
		initializeXmlMap();
	}
}//eof-class

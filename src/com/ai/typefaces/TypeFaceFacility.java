/*
 * Created on Oct 1, 2005
 */
package com.ai.typefaces;

import java.util.Iterator;

import com.ai.application.interfaces.IValidation;
import com.ai.application.interfaces.ValidationException;
import com.ai.common.IDictionary;
import com.ai.reflection.BeanInfo;
import com.ai.reflection.BeanInfoRegistry;
import com.ai.reflection.FieldInfo;
import com.ai.reflection.ReflectionException;

/**
 *
 * At the moment this only applies to a dictionary of strings
 */
public class TypeFaceFacility implements ITypeFaceFacility 
{
	private static TypeFaceFacility s_self = new TypeFaceFacility();  
	public Object castTo(Class objectClass, IDictionary dictionary) 
	throws ReflectionException
	{
		//Instantiate the object based on the class
		//instantiate a bean info for this object
		//For each public field infos
		//		get its name
		//		get its value from dictionary
		//		set the value using get or directly on the field info
		// return the object
		BeanInfo bi = BeanInfoRegistry.getBeanInfo(objectClass);
		Object targetObject = bi.constructObject();
		Iterator<FieldInfo> fieldInfoItr = bi.getFieldInfos();
		while(fieldInfoItr.hasNext())
		{
			//for each field info
			FieldInfo fi = fieldInfoItr.next();
			if (fi.isFundamentalType())
			{
			    //it is a fundamental type
			    Object fieldValue = dictionary.get(fi.m_name);
			    if (fieldValue != null)
			    {
			        fi.setValueUsingString(targetObject,(String)fieldValue);
			    }
			}
		}
		validateObject(targetObject);
		return targetObject;
	}
	
	private void validateObject(Object o) throws ReflectionException
	{
		try
		{
			if (o instanceof IInitializableTypeFace)
			{
				((IInitializableTypeFace)o).initialize();
			}
			if (o instanceof IValidation)
			{
				((IValidation)o).validateWithException();
			}
			
		}
		catch(ValidationException x)
		{
			throw new ReflectionException("Dynamic Object Validation failed",x);
		}
	}
	
	public static TypeFaceFacility self()
	{
		return s_self;
	}
}

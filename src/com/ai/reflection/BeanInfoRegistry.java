/*
 * Created on Oct 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.reflection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author a3le
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeanInfoRegistry 
{
	static private Map beanInfoMap = new HashMap();
	static private String lockString = "lock"; 
	
	static public BeanInfo getBeanInfo(Class classObject)
	throws ReflectionException
	{
		try
		{
			Object thisObject = beanInfoMap.get(classObject.getName()); 
			if (thisObject != null)
			{
				return (BeanInfo)thisObject;
			}
			synchronized(lockString)
			{
				//recheck again after obtaining the lock
				thisObject = beanInfoMap.get(classObject.getName()); 
				if (thisObject != null)
				{
					return (BeanInfo)thisObject;
				}
				thisObject = new BeanInfo(classObject);
				beanInfoMap.put(classObject.getName(),thisObject);
				return (BeanInfo)thisObject;
			}
		}
		catch(NoSuchMethodException x)
		{
			throw new ReflectionException("Error:getBeanInfo. No such method",x);
		}
		catch(NoSuchFieldException x)
		{
			throw new ReflectionException("Error:getBeanInfo.No such field",x);
		}
	}//eof-function-getbeaninfo
}//eof-class

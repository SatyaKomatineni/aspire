/*
 * Created on Oct 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.typefaces;
import com.ai.common.IDictionary;
import com.ai.reflection.ReflectionException;

/**
 * @author a3le
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ITypeFaceFacility 
{
	Object castTo(Class objectClass, IDictionary dictionary) throws ReflectionException;
}

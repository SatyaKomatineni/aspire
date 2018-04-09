package com.ai.typefaces;

import java.util.HashMap;

import com.ai.common.IDictionary;
import com.ai.common.MapDictionary;
import com.ai.reflection.ReflectionException;
import com.ai.reflection.ReflectionUtils;

/**
 * @author Satya Komatineni
 */
public class Test 
{
	static public void main(String args[])
	{
	    try
	    {
	    System.out.println("hello");
	    testPrinting();
	    test1();
	    testPublicFields();
	    testMorePublicFields();
	    }
	    catch(Throwable t)
	    {
	        t.printStackTrace();
	    }
	}
	public static void testPrinting() throws ReflectionException
	{
	    Object o = new TestClass();
	    String s = ReflectionUtils.convertObjectToString(o);
	    System.out.println(s);
	}
	public static void test1() 
	throws ReflectionException, ClassNotFoundException
	{
	    //Create a dictionary of key value pairs
	    HashMap map = new HashMap();
	    map.put("f1","f1-stuff");
	    map.put("f2","f2-stuff");
	    IDictionary dict = new MapDictionary(map);
	    
	    //Get a typeface facility
	    ITypeFaceFacility tff = new TypeFaceFacility();
	    
	    //Cast the dictionary to an object of your choosing
	    TestClass tc = (TestClass)tff.castTo(Class.forName("com.ai.typefaces.TestClass"),dict);
	    
	    //Print the object as xml
	    String s = ReflectionUtils.convertObjectToString(tc);
	    System.out.println(s);
	}
	
	public static void testPublicFields() 
	throws ReflectionException, ClassNotFoundException
	{
	    //Create a dictionary of key value pairs
	    HashMap map = new HashMap();
	    map.put("f1","f1-stuff");
	    map.put("f2","f2-stuff");
	    IDictionary dict = new MapDictionary(map);
	    
	    //Get a typeface facility
	    ITypeFaceFacility tff = new TypeFaceFacility();
	    
	    //Cast the dictionary to an object of your choosing
	    TestClass1 tc = (TestClass1)tff.castTo(Class.forName("com.ai.typefaces.TestClass1"),dict);
	    
	    //Print the object as xml
	    String s = ReflectionUtils.convertObjectToString(tc);
	    System.out.println(s);
	}
	public static void testMorePublicFields() 
	throws ReflectionException, ClassNotFoundException
	{
	    //Create a dictionary of key value pairs
	    HashMap map = new HashMap();
	    map.put("f1","f1-stuff");
	    map.put("f2","f2-stuff");
	    IDictionary dict = new MapDictionary(map);
	    
	    //Get a typeface facility
	    ITypeFaceFacility tff = new TypeFaceFacility();
	    
	    //Cast the dictionary to an object of your choosing
	    TestClass2 tc = (TestClass2)tff.castTo(Class.forName("com.ai.typefaces.TestClass2"),dict);
	    
	    //Print the object as xml
	    String s = ReflectionUtils.convertObjectToString(tc);
	    System.out.println(s);
	}
}


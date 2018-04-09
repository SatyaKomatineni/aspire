/*
 * Created on Nov 7, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.ps;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ParamSpec 
{
	public String key;
	public String type;
	public String converterName;//can be null
	
	public ParamSpec(String inKey
			,String inType
			,String inConverterName)
	{
		key = inKey;
		type = inType;
		converterName = inConverterName;
	}
	
	public ParamSpec(String inKey
			,String inType)
	{
		this(inKey,inType,null);
	}

	public ParamSpec(String inKey)
	{
		this(inKey,null,null);
	}
	
	static List parseString(String s)
	{
		List paramSpecList = new ArrayList();
		
		Vector v = Tokenizer.tokenize(s,"|");
		AppObjects.trace("param parser","Number of parameters:" + v.size());
		Enumeration e = v.elements();
		while(e.hasMoreElements())
		{
			String curElem = (String)e.nextElement();
			ParamSpec ps = getParamSpec(curElem);
			paramSpecList.add(ps);
		}
		return paramSpecList;
	}
	private static ParamSpec getParamSpec(String s)
	{
		//key,type,convertername
		AppObjects.trace("param parser","Parsing:" + s);
		Vector v = Tokenizer.tokenize(s,",");
		String key = (String)v.elementAt(0);
		String type = null;
		String converterName = null;
		
		if (v.size() > 1)
		{
			type = (String)v.elementAt(1);
		}
		if (v.size() > 2)
		{
			converterName = (String)v.elementAt(2);
		}
		return new ParamSpec(key,type,converterName);
	}
}//eof-class

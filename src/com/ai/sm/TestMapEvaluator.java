/*
 * Created on Jan 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.sm;

import java.util.List;
import java.util.Map;

public class TestMapEvaluator implements IFunctionEvaluator
{
	Map m_map = null;
	public TestMapEvaluator(Map map) 
	{
		m_map = map;
	}
	public String evaluate(List functionAndArgs)
	{
		
		return "test";
	}

}

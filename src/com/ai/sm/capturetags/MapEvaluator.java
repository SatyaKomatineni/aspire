package com.ai.sm.capturetags;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ai.common.Tokenizer;
import com.ai.sm.CompositeState;
import com.ai.sm.DefaultEvaluator;
import com.ai.sm.State;

public class MapEvaluator extends DefaultEvaluator
{
	Map map = new HashMap();
	public MapEvaluator()
	{
		this(null);
	}
	public MapEvaluator(Map inMap)
	{
		if (inMap != null)
		{
			map = inMap;
			return;
		}
		map = new HashMap();
	}
	public Map getMap()
	{
		return map;
	}
	public String evaluate(State state)
	{
		CompositeState tagSpan = (CompositeState)state;
		String captureName = getCaptureName(tagSpan);
		map.put(captureName.toLowerCase(), getCaptureBody(tagSpan));
		return "";
	}
	
	private String getCaptureName(CompositeState cs)
	{
		List stateList = cs.getChildrenStateList();
		State fc = (State)stateList.get(0);
		String s = fc.getBody();
		s = s.trim();
		Vector v = Tokenizer.tokenize(s, " \t");
		return (String)v.get(1);
	}
	private String getCaptureBody(CompositeState cs)
	{
		List stateList = cs.getChildrenStateList();
		State fc = (State)stateList.get(1);
		return fc.getBody();
	}
}//eof-class

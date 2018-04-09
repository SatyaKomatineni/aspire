package com.ai.sm.capturetags;

import java.util.Iterator;

import com.ai.sm.DefaultEvaluator;
import com.ai.sm.State;

public class TagSpanRecognizedEvaluator extends DefaultEvaluator
{
	public String evaluate(State state)
	{
		return "<tag span recognized>";
	}
}//eof-class

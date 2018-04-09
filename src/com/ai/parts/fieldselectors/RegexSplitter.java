package com.ai.parts.fieldselectors;

import java.util.List;

/**
 * @author satya
 * 
 * Split a line or a row of string text in to a set of fields
 * Implementing classes will decide how to split 
 */
public abstract class RegexSplitter extends AbstractRowSplitter 
{
	public String[] splitAsAnArray(String s)
	{
		// equivalent regex: \s
		// \s stands for whitespace
		return s.split(hookGetRegex());
	}
	
	protected abstract String hookGetRegex();
}


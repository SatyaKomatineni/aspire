package com.ai.parts.fieldselectors;

import java.util.Arrays;
import java.util.List;

/**
 * @author satya
 * 
 * Split a line or a row of string text in to a set of fields
 * Implementing classes will decide how to split 
 */
public abstract class AbstractRowSplitter implements IRowSplitter 
{
	public List<String> split(String s)
	{
		return Arrays.asList(splitAsAnArray(s.trim()));
	}
	public abstract String[] splitAsAnArray(String s);
}

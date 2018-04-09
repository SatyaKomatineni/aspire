package com.ai.parts.fieldselectors;

import java.util.List;

/**
 * @author satya
 * 
 * Split a line or a row of string text in to a set of fields
 * Implementing classes will decide how to split 
 */
public interface IRowSplitter {
	public List<String> split(String s);
	public String[] splitAsAnArray(String s);
}

package com.ai.parts.fieldselectors;

/**
 * @author satya
 * 
 * Split a line or a row of string text in to a set of fields
 * Splits a sentence into words by white space
 * 
 * You can also use this as a ServiceObject as follows
 * No config args necessary.
 * 
 * request.wordsplitter.classname=\
 * com.ai.parts.fieldselectors.WordSplitter
 *  
 */
public class WordSplitter extends RegexSplitter 
{
	protected String hookGetRegex() 
	{ 
		// equivalent regex: \s+
		// \s stands for whitespace
		// + means one or more
		// \s+ means one or more white space characters
		return "\\s+"; 
	}
	
	//Testing support
    static public void main(String args[])
    {
    	test1();
    }
    static private void test1()
    {
    	WordSplitter ws = new WordSplitter();
    	String s1 = "Simple FirstWord ddddd";
    	String s2 = "  SimpleSpaces FirstWord   ddddd  ";
    	String s3 = "  WithTabs FirstWord   		\tddddd  ";
    	String s4 = "  WithTabs FirstWord   		\tddddd \nAfterNewLine second  ";
        System.out.println(ws.split(s1));
        System.out.println(ws.split(s2));
        System.out.println(ws.split(s3));
        System.out.println(ws.split(s4));
    }
}

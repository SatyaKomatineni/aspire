package com.ai.sm;

public class StringBingo implements IBingo
{
	private String pattern = null;
	private char nextChar;
	private int nextCharIndex=0;
	private int len = 0;
	public StringBingo(String inPattern)
	{
		pattern = inPattern;
		nextChar = inPattern.charAt(0);
		len = inPattern.length();
	}
	public IBingo recast()
	{
		return new StringBingo(pattern);
	}
	public int processChar(char c)
	{
		if (c == nextChar)
		{
			if (nextCharIndex == len-1)
			{
				//this is the last character
				reset();
				return IBingo.BINGO;
			}
			else
			{
				//this is not the last character
				//more chars to match
				moveToNextChar();
				return IBingo.NO_BINGO;
			}
		}
		else
		{
			//doesn't match
			reset();
		}
		return IBingo.NO_BINGO;
	}//eof-function
	private void moveToNextChar()
	{
		this.nextCharIndex++;
		this.nextChar = this.pattern.charAt(nextCharIndex);
	}
	private void reset()
	{
		this.nextCharIndex=0;
		this.nextChar = this.pattern.charAt(0);
	}
	public int getLength()
	{
		return this.len;
	}
	public String toString()
	{
		return this.pattern;
	}
}//eof-class

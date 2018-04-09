package com.ai.sm;

public class CharacterBingo implements IBingo
{
	private char c;
	public CharacterBingo(char inc)
	{
		c = inc;
	}
	public IBingo recast()
	{
		return this;
	}
	public int processChar(char inc)
	{
		if (c == inc)
		{
				return IBingo.BINGO;
		}
		else
		{
			//doesn't match
			return IBingo.NO_BINGO;
		}
	}//eof-function
	
	public int getLength()
	{
		return 1;
	}
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append(c);
		return s.toString();
	}
}//eof-class

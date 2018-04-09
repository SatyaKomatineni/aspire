package com.ai.sm;

public interface IBingo 
{
	public static int BINGO = 1;
	public static int NO_BINGO = 0;
	public int processChar(char c);
	public int getLength();
	public IBingo recast();
	public String toString();
}

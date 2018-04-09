package com.ai.common.console;

public class DefaultConsole implements IConsole
{
    public void prompt(String promptString)
    {
    	System.err.println(promptString);
    }

    public void info(String promptString)
    {
    	System.err.println(promptString);
    }
    public void error(String promptString)
    {
    	System.err.println(promptString);
    }
	public void help(String promptString)
	{
		info(promptString);
	}
    public void output(String outputString)
    {
    	System.out.println(outputString);
    }
}

package com.ai.common.console;

/**
 * @author satya
 * 
 * To provide support for console applications.
 * I want to avoid wrting to stdout every time 
 * To allow for pipe commands
 *
 */
public interface IConsole
{
	//when you want to prompt for input: stderr 
	public void prompt(String promptString);
	
	//Informational messages: stderr
	public void info(String promptString);
	
	//Help Informational messages: stderr
	public void help(String promptString);
	
	//Of course any errors or diagnostic: stderr
	public void error(String promptString);
	
	//Actual legitimate output a user would like to see
	public void output(String outputString);
}

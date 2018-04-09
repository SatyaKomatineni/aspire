package com.ai.common.console;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;

/**
 * 
 * @author satya
 *
 * request.console.classname=com.ai.common.console.DefaultConsole
 */
public class CLIConsole
{
	private static String CLI_CONSOLE = "console";
	private static IConsole m_self;
	
	//Use this when the system is not initialized yet.
    public static void base_info(String promptString)
    {
    	System.err.println(promptString);
    }
	
    private static void init()
    {
    	if (m_self != null) return;
		try
		{
			m_self = (IConsole)AppObjects.getObject(CLI_CONSOLE, null);
		}
		catch(RequestExecutionException x)
		{
			m_self = new DefaultConsole(); 
		}    	
    }
    private static IConsole getSelf()
    {
    	init();
    	return m_self;
    }
    public static void prompt(String promptString)
    {
    	getSelf().prompt(promptString);
    }
    public static void info(String promptString)
    {
    	getSelf().info(promptString);
    }
    public static void help(String promptString)
    {
    	getSelf().help(promptString);
    }
    public static void error(String promptString)
    {
    	getSelf().error(promptString);
    }
    public static void output(String outputString)
    {
    	getSelf().output(outputString);
    }
}

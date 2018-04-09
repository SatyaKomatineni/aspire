package com.ai.test;
import java.util.HashMap;
import java.util.Map;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.parts.configobjects.SimpleDataObject;

/**
 * 7/8/16
 * @author satya
 *
 * request.TestCollection.PartialCommandRequest.classname=\
 * com.ai.test.PartialCommandRequestSDO
 * request.TestCollection.PartialCommandRequest.command=command_name
 * request.TestCollection.PartialCommandRequest.key=main_key_name
 * 
 * Note:
 * *********
 * Interestingly this being a singleton for now, 
 * could have been implemented with out being an SDO
 *
 */
public class PartialCommandRequestSDO extends SimpleDataObject
{
   private String m_command;
   private String key;
   
   private static final String SELF="TestCollection.PartialCommandRequest"; 
   
	@Override
	public void initialize(String requestName) {
		try
		{
			m_command = AppObjects.getValue(requestName + ".command");
			key = AppObjects.getValue(requestName + ".key");
		}
		catch(ConfigException x)
		{
			throw new RuntimeException("Config error",x);
		}
	}
	public CommandRequest createCommandRequest(String value)
	{
		Map<String,String> argsMap = new HashMap<String,String>();
		argsMap.put(key.toLowerCase(), value);
		return new CommandRequest(m_command,argsMap);
	}
	
	private static PartialCommandRequestSDO m_self = null;
	private static boolean isTriedOnce = false;
	public static PartialCommandRequestSDO getSingleInstance()
	{
		if (m_self != null) return m_self;
		
		if (isTriedOnce == false)
		{
			isTriedOnce = true;
			try {
				m_self = (PartialCommandRequestSDO)AppObjects.getObject(SELF, null);
				if (m_self != null)
				{
					AppObjects.info("PartialCommandRequestSDO", "Command Object in place");
				}
			}
			catch(RequestExecutionException x)  {
				m_self = null;
			}
		}//eof-if
		return m_self;
	}
	public static boolean isPartialCommandInPlace()
	{
		if (PartialCommandRequestSDO.getSingleInstance() != null)
		{
			return true;
		}
		return false;
	}
}//eof-class

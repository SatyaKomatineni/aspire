/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.sm;

/**
 * @author a3le
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StringReceiver implements IReceiver
{
	private StringBuffer m_buffer = new StringBuffer();
	public void accept(String token)
	{
		m_buffer.append(token);
	}
	public String getString()
	{
		return m_buffer.toString();
	}
	public void accept(char token)
	{
		m_buffer.append(token);
	}
}

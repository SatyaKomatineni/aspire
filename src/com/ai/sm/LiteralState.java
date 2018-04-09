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
public class LiteralState extends DState
{
	public LiteralState(String name, IReceiver receiver) {
		super(name, receiver);
	}
	
	public State newInstance()
	{
		return new LiteralState();
	}
	LiteralState() {}
}

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
public class DollarState extends DState {

	public DollarState(String name, IReceiver receiver) {
		super(name, receiver);
	}
	public String evaluate()
	{
		return m_body.toString();
	}
	
	public void exited(IBingo token)
	{
		super.exited(token);
		m_receiver.accept(evaluate());
	}
	public State newInstance()
	{
		return new DollarState();
	}
	DollarState(){}
}

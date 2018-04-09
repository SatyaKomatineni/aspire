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
public class ImplicitLiteralState extends LiteralState
{
	public ImplicitLiteralState(String name, IReceiver receiver) {
		super(name, receiver);
	}
	public void entered(IBingo bingo, State previousState)
	{
		m_body.append(bingo.toString());
		super.entered(bingo,previousState);
	}
	public State newInstance()
	{
		return new ImplicitLiteralState();
	}
	ImplicitLiteralState(){}
}

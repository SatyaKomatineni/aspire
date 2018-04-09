/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.sm;

import java.util.Iterator;

/**
 * @author a3le
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FunctionState extends CompositeState 
{
	
	public FunctionState(String name, IReceiver receiver, State initialState) 
	{
		super(name, receiver, initialState);
	}
	public String evaluate()
	{
		StringBuffer outbuf = new StringBuffer();
		Iterator itr = this.stateList.iterator();
		while(itr.hasNext())
		{
			State st = (State)itr.next();
			outbuf.append(st.evaluate()).append(",");
		}
		return outbuf.toString();
	}
	public State newInstance() { return new FunctionState(); }
	FunctionState() {}
}

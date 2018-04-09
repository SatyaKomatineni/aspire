package com.ai.sm;

import java.util.Iterator;

public class DefaultEvaluator implements IEvaluator
{
	public String evaluate(State state)
	{
		if (state instanceof CompositeState)
		{
			return evalForComposite((CompositeState)state);
		}
		return evalForDefaultState(state);
	}
	
	private String evalForDefaultState(State state)
	{
		return state.getBody();
	}
	
	private String evalForComposite(CompositeState cs)
	{
		StringBuffer outbuf = new StringBuffer();
		//outbuf.append("Default composite evaluation for " + cs.getName() + "\n");
		Iterator itr = cs.getChildrenStateList().iterator();
		while(itr.hasNext())
		{
			State st = (State)itr.next();
			outbuf.append(st.evaluate());
		}
		return outbuf.toString();
	}
	public void entered(State state, IBingo token, State previousState)
	{
		//System.out.println("<entered state:" + state.getName() + ">");
	}

	//Evaluate to see what comes out
	//send it to the receiver
	public void exited(State state, IBingo token)
	{
		//System.out.println("exited " + state.getName() + " with body:" + state.getBody());
		//DState ds = (DState)state;
		//ds.getReceiver().accept(ds.evaluate());
	}
	
}//eof-class

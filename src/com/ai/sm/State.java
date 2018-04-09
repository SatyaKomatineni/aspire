/*
 * Created on Jan 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.sm;

import java.util.HashMap;

/**
 * @author a3le
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface State 
{
	
	public String getBody();
	
	//registering next states
	public void registerNextState(IBingo bingo,State nextState);
	public void registerStatesComplete();
	
	public State processChar(char inChar);
	public void nomoreChars();
	public State getPreviousState();
	public String getName();
	public State getParentState();
	public void setParentState(State parentState);
	public void restore();
	
	//Purely for duplicating functionality
	public State newInstance();
	public State recast();
	
	//callbacks
	public void entered(IBingo token, State previousState);
	public void exited(IBingo token);
	public String evaluate();
}


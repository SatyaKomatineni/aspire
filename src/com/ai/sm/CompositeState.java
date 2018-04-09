/*
 * Created on Jan 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.sm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author a3le
 * Seem to represent a collection of sub states
 * CompoisteState
 *   	State1
 *   	State2
 *   	State3
 *   
 */
public class CompositeState extends DState
{
	State m_initialState = null;
	
	//lets make these list of states
	//List<State>
	protected List stateList = new ArrayList();
	
	State m_curState = null;
	
	CompositeState(){}
	public State newInstance()
	{
		return new CompositeState();
	}
	
	public State recast()
	{
		CompositeState st = (CompositeState)super.recast();
		st.dupSetInitialState(m_initialState);
		return st;
	}
	private void dupSetInitialState(State initState)
	{
		State newInitState = initState.recast();
		newInitState.setParentState(this);
		m_initialState = newInitState;
		m_curState = newInitState;
	}
	public void restore()
	{
		m_curState = this.m_initialState;
		m_curState.restore();
		stateList = new ArrayList();
		super.restore();
	}
	//constructor
	public CompositeState(String name, IReceiver receiver, IEvaluator eval, State initialState)
	{
		super(name,receiver,eval);
		initialState.setParentState(this);
		m_curState = initialState;
		m_initialState = initialState;
	}
	public CompositeState(String name, IReceiver receiver, State initialState)
	{
		super(name,receiver);
		initialState.setParentState(this);
		m_curState = initialState;
		m_initialState = initialState;
	}
	
	public State processChar(char inChar)
	{
		if (m_curState != null)
		{
			return processForSubStates(inChar);
		}
		//System.out.println("executing processchar");
		return super.processChar(inChar);
	}
	
	private State processForSubStates(char inChar)
	{
		State nextSubState = m_curState.processChar(inChar);
		if (nextSubState == null)
		{
			return null;
		}
		//nextSubstate is not null
		//System.out.println("\tcurstate:" + m_curState.getName());
		//System.out.println("\t\tnextstate:" + nextSubState.getName());
		if (!(m_curState.getName().equals("end")))
		{
			//there is a new substate
			//it is not the end state
			//save the current state
			this.stateList.add(m_curState);
			m_curState = nextSubState;
			m_curState.setParentState(this);
			return null;
		}
		//curstate is the end state
		this.stateList.add(m_curState);
		//Next substate is an end state for the sub states
		//set the cur state to null
		m_curState = null;
		return nextSubState;
	}
	public void nomoreChars()
	{
		this.stateList.add(m_curState);
		m_curState = null;
	}
	public void exited(IBingo bingo)
	{
		super.exited(bingo);
		if (getParentState() == null)
		{
			//This is the outer composite state
			m_receiver.accept(evaluate());
		}
	}
	public List getChildrenStateList()
	{
		return this.stateList;
	}
}//eof-class

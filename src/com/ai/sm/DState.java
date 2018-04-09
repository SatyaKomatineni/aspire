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
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DState implements State
{
	//Definition variables
	
	//List of next possible states
	//List<State>
	private List m_exitStateList = new ArrayList();
	protected IReceiver m_receiver = null;
	
	//evaluator
	protected IEvaluator evaluator = null;
	
	//List<IBingo>
	//A list of tokens leading to next set of states
	List m_tokenList = new ArrayList();
	//Iterator m_tokenListItr = null;
	
	//?
	String m_name = null;
	
	//How this state was entered from a prev state
	State m_previousState = null;
	
	//A place holder to keep the characters as they go by
	protected StringBuffer m_body = new StringBuffer("");
	//set this up after registering all tokens
	//this will be equal to the 
	//protected LookAheadBuffer lheadBuffer = null;
	
	//?
	private State m_parentState = null;
	
	//?
	//when is this called?
	public void restore()
	{
		m_body = new StringBuffer("");
		setParentState(null);
	}
	
	DState(){}

	//Create a new derived class of specific type
	public State newInstance()
	{
		return new DState();
	}

	//make sure you have replicated objects
	public State recast()
	{
		DState dState = (DState)newInstance();
		dState.dupSetBingoList(this.m_tokenList);
		dState.dupSetReceiver(m_receiver);
		dState.dupSetName(m_name);
		dState.dupSetExitStateList(m_exitStateList);
		dState.dupSetEvaluator(evaluator);
		return dState;
	}
	
	private void dupSetEvaluator(IEvaluator eval)
	{
		evaluator = eval;
	}	
	private void dupSetReceiver(IReceiver receiver)
	{
		m_receiver = receiver;
	}	
	private void dupSetBingoList(List bingoList)
	{
		Iterator itr = bingoList.iterator();
		while(itr.hasNext())
		{
			IBingo o = (IBingo)itr.next();
			Object no = o.recast();
			this.m_tokenList.add(no);
		}
		//this.m_tokenListItr = this.m_tokenList.iterator();
	}
	
	private void dupSetName(String name)
	{
		m_name = name;
	}
	
	private void dupSetExitStateList(List list)
	{
		m_exitStateList = list;
	}	
	
	//Constructor
	public DState(String name, IReceiver receiver)
	{
		this(name,receiver,new DefaultEvaluator());
	}
	
	public DState(String name, IReceiver receiver, IEvaluator eval)
	{
		m_name = name;
		m_receiver = receiver;
		this.evaluator = eval;
	}
	
	public String getName()
	{
		return m_name;
	}
	public String getBody()
	{
		return m_body.toString();
	}
	public State getPreviousState()
	{
		return m_previousState;
	}
	
	public void registerNextState(IBingo bingo,State nextState)
	{
		this.m_tokenList.add(bingo);
		m_exitStateList.add(nextState);
	}
	
	public void registerNextState(char inc,State nextState)
	{
		registerNextState(new CharacterBingo(inc),nextState);
	}
	
	public void registerNextState(String ins,State nextState)
	{
		registerNextState(new StringBingo(ins),nextState);
	}
	
	public void registerStatesComplete()
	{
		//this.m_tokenListItr = this.m_tokenList.iterator();
	}
	
	public State processChar(char inChar)
	{
		m_body.append(inChar);
		BingoAndIndex matchingBingo = getMatchingBingo(inChar);
		if (matchingBingo == null)
		{
			return null;
		}
		//Next state available
		//remove excessive chars
		this.removeCharsFromBody(matchingBingo.matchingBingo);
		
		State nextState = getNextState(matchingBingo);
		//exit the current state
		exited(matchingBingo.matchingBingo);
		nextState.entered(matchingBingo.matchingBingo,this);
		return nextState;
		
	}
	public void nomoreChars()
	{
		//
	}
	private BingoAndIndex getMatchingBingo(char inChar)
	{
		int matchingIndex = -1;
		Iterator itr = this.m_tokenList.iterator();
		while(itr.hasNext())
		{
			matchingIndex++;
			IBingo bingo = (IBingo)itr.next();
			int result = bingo.processChar(inChar);
			if (result == IBingo.BINGO)
			{
				return new BingoAndIndex(bingo,matchingIndex);
			}
		}
		//no matching index
		return null;
	}
	
	private void removeCharsFromBody(IBingo matchingBingo)
	{
		m_body.delete(m_body.length() - matchingBingo.getLength(),m_body.length());
	}
	public State getNextState(BingoAndIndex bi)
	{
		//matching index found
		State nextState = (State)m_exitStateList.get(bi.matchingIndex);
		
		State newState = nextState.recast();
		return newState;
	}
	//*************************************************
	//* call backs
	//*************************************************
	public void exited(IBingo bingo)
	{
		this.evaluator.exited(this,bingo);
	}
	/**
	 * You may want to move the previous state setting out 
	 * of this call back.
	 */
	public void entered(IBingo bingo, State previousState)
	{
		m_previousState = previousState;
		this.evaluator.entered(this, bingo, previousState);
	}
	public String evaluate()
	{
		return this.evaluator.evaluate(this);
	}
	public IReceiver getReceiver()
	{
		return this.m_receiver;
	}
	/**
	 * @return Returns the m_parentState.
	 */
	public State getParentState() {
		return m_parentState;
	}
	/**
	 * @param state The m_parentState to set.
	 */
	public void setParentState(State state) {
		m_parentState = state;
	}
	
	public class BingoAndIndex
	{
		public IBingo matchingBingo = null;
		public int matchingIndex = -1;
		public BingoAndIndex(IBingo bingo, int index)
		{
			matchingBingo = bingo;
			matchingIndex = index;		
		}
	}//eof-class
}//eof-class

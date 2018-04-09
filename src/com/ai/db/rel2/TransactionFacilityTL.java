/*
 * Created on Nov 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.util.Stack;

import com.ai.application.utils.AppObjects;

/**
 * @author Satya
 *
 * 1. TL stands for thread local
 * 2. will hold a stack of transactional contexts
 * 3. Only one will be active or current
 */
public class TransactionFacilityTL 
{
	private TransactionalContext m_curTC = null;
	private Stack m_TCStack = new Stack();
	
	public TransactionFacilityTL()
	{
		
	}
	
	/**
	 * start a transactional context
	 * 1. the passed in tc will become current
	 * 2. One that is current will be pushed to stack, if it is there
	 * @param tc
	 */
	public void startContext(TransactionalContext tc)
	{
		if (m_curTC != null)
		{
			AppObjects.trace(this,"Pushing a transactional context to stack:" + tc);
			m_TCStack.push(m_curTC);
			m_curTC = tc;
		}
		else
		{
			AppObjects.trace(this,"No previous context. stack is empty");
			m_curTC = tc;
		}
	}
	/**
	 * 1. if there is no current context, log a warning
	 * 2. If one exists pop from the stack
	 * 3. if none exists on the stack make it null
	 *
	 */
	public void endCurrentContext()
	{
		if (m_curTC == null)
		{
			AppObjects.warn(this,"There is no current context to end.");
			return;
		}
		//current context exists
		if (this.m_TCStack.size() > 0)
		{
			//there is something in the stack
			AppObjects.trace(this,"Restoring a previous context from stack");
			m_curTC = (TransactionalContext)this.m_TCStack.pop();
		}
		else
		{
			AppObjects.trace(this,"stack is empty. setting the current context to null");
			m_curTC = null;
		}
	}
	public TransactionalContext getCurrentContext()
	{
		return this.m_curTC;
	}
}//eof-class

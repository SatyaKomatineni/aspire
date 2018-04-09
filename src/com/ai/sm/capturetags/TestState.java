/*
 * Created on Jan 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.sm.capturetags;

import com.ai.sm.CharacterBingo;
import com.ai.sm.DState;
import com.ai.sm.DollarState;
import com.ai.sm.FunctionState;
import com.ai.sm.ImplicitLiteralState;
import com.ai.sm.LiteralState;
import com.ai.sm.State;
import com.ai.sm.StringReceiver;

/**
 * @author a3le
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestState {

	public static void main(String[] args) 
	{
		//Setup a receiver for the evaluated production
		StringReceiver receiver = new StringReceiver();
		
		//Create a state for literals that start of the stuff
		DState literal = new LiteralState("literal",receiver);
		
		//Get ready to set up a functionstate for parsing a function
		DState functionName = new DState("functionName",receiver);
		DState argument = new DState("argument",receiver);
		DState endState = new DState("end",receiver);
		
		//Define the function state as an expression
		DState expression = new FunctionState("expression",receiver,functionName);
		
		//Define the state changes
		//From a literal you can go to an expression on {
		literal.registerNextState('{',expression);
		
		//From an expression you can go to a literal on a }
		expression.registerNextState('}',literal);
		
		//From a function you can go to an argument on (
		functionName.registerNextState('(',argument);
		
		//end of the argument
		argument.registerNextState(')',endState);
		argument.registerNextState(',',argument);
		
		DollarState dollarState = new DollarState("dollar",receiver);
		ImplicitLiteralState implicitLiteralState = new ImplicitLiteralState("implicit-literal",receiver);
		
		literal.registerNextState('$',dollarState);
		dollarState.registerNextState(' ',implicitLiteralState);
		implicitLiteralState.registerNextState('{',expression);
		implicitLiteralState.registerNextState('$',dollarState);
		
		//Start with a literal state
		State curState = literal;
		String stmt = "one of $1 stuff $2 stuff these days {atleast($1,stuff)} abcdef {anotherfunc($2,$3,$4)} dd";
		System.out.println(stmt + "\n");
		for(int i=0;i<stmt.length();i++)
		{
			char curChar = stmt.charAt(i);
			State newState = curState.processChar(curChar);
			if (newState != null)
			{
				curState = newState;
			}
		}
		curState.exited(new CharacterBingo('1'));
		
		System.out.println("\n\nProduced string is");
		System.out.println(receiver.getString());
	}
}

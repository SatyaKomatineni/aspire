package com.ai.sm.capturetags;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;

import com.ai.sm.CharacterBingo;
import com.ai.sm.CompositeState;
import com.ai.sm.DState;
import com.ai.sm.LiteralState;
import com.ai.sm.State;
import com.ai.sm.StringReceiver;

public class CaptureTagUtils 
{
	public static void splitString(String ins, Map outputMap, String mainPartName)
	{
		//Setup a receiver for the evaluated production
		MapEvaluator me = new MapEvaluator(outputMap);
		StringReceiver receiver = new StringReceiver();
		
		LiteralState ls = new LiteralState("literal",receiver);
		DState beginTagState = new DState("begintag",receiver);
		DState endTagState = new DState("end",receiver);
		
		CompositeState tagSpan = new CompositeState("tagspan"
					,receiver
					,me
					,beginTagState);
		
		CompositeState cs = new CompositeState("file",receiver,ls);
		ls.registerNextState("<!--tag_bgn", tagSpan);
		beginTagState.registerNextState("-->", ls);
		ls.registerNextState("<!--tag_end", endTagState);
		endTagState.registerNextState("-->", ls);
		

		//Start with a literal state
		State curState = cs;
		StringCharacterIterator sci = new StringCharacterIterator(ins);
		for(char curChar=sci.first();curChar != CharacterIterator.DONE; curChar=sci.next())
		{
			State newState = curState.processChar(curChar);
			if (newState != null)
			{
				curState = newState;
			}
		}
		curState.nomoreChars();
		//curState.exited(new CharacterBingo('1'));
		String evals = cs.evaluate();
		if (mainPartName != null)
		{
			outputMap.put(mainPartName.toLowerCase(), evals);
		}
		
		//System.out.println("\n\nProduced string is");
		//System.out.println(evals);
	}
	
	public static Map getMapFromString(String ins)
	throws Exception
	{
		Map map = new HashMap();
		splitString(ins,map,null);
		return map;
	}
	
	/**
	 * @deprecated use instead getMapFromString
	 * @param ins
	 * @return
	 * @throws Exception
	 */
	public static Map getMapFromStringOld(String ins)
	throws Exception
	{
		//Setup a receiver for the evaluated production
		MapEvaluator me = new MapEvaluator();
		StringReceiver receiver = new StringReceiver();
		
		LiteralState ls = new LiteralState("literal",receiver);
		DState beginTagState = new DState("begintag",receiver);
		DState endTagState = new DState("end",receiver);
		
		CompositeState tagSpan = new CompositeState("tagspan"
					,receiver
					,me
					,beginTagState);
		
		CompositeState cs = new CompositeState("file",receiver,ls);
		ls.registerNextState("<!--tag_bgn", tagSpan);
		beginTagState.registerNextState("-->", ls);
		ls.registerNextState("<!--tag_end", endTagState);
		endTagState.registerNextState("-->", ls);
		

		//Start with a literal state
		State curState = cs;
		StringCharacterIterator sci = new StringCharacterIterator(ins);
		for(char curChar=sci.first();curChar != CharacterIterator.DONE; curChar=sci.next())
		{
			State newState = curState.processChar(curChar);
			if (newState != null)
			{
				curState = newState;
			}
		}
		curState.exited(new CharacterBingo('1'));
		String evals = cs.evaluate();
		
		//System.out.println("\n\nProduced string is");
		//System.out.println(evals);
		return me.getMap();
	}
}//eof-class
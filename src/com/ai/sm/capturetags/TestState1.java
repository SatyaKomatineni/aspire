/*
 * Created on Jan 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.sm.capturetags;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.ai.common.base.BaseFileUtils;
import com.ai.sm.CharacterBingo;
import com.ai.sm.CompositeState;
import com.ai.sm.DState;
import com.ai.sm.LiteralState;
import com.ai.sm.State;
import com.ai.sm.StringReceiver;

/**
 * @author a3le
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestState1 {

	public static void main(String[] args) 
	{
		try
		{
			realMain();
		}
		catch(Throwable t)
		{
			System.out.println(t.getMessage());
			t.printStackTrace();
		}
	}
	public static void realMain()
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
		

		String files = TestState1.readFile();

		
		//Start with a literal state
		State curState = cs;
		for(int i=0;i<files.length();i++)
		{
			char curChar = files.charAt(i);
			State newState = curState.processChar(curChar);
			if (newState != null)
			{
				curState = newState;
			}
		}
		curState.exited(new CharacterBingo('1'));
		
		System.out.println("\n\nProduced string is");
		System.out.println(cs.evaluate());
		
		System.out.println("\n\nThe hashtable is");
		System.out.println(me.getMap().toString());
	}
	
	public static void test()
	throws Exception
	{
		String hello = TestState1.readFile();
		//System.out.print(hello);
	}
	
	private static String readFile() throws FileNotFoundException
	,IOException
	{
		File file = new File("C:\\satya\\data\\webapps\\statemachine\\data\\test1.html");
		FileInputStream fi = new FileInputStream(file);
		String content = BaseFileUtils.readStreamAsString(fi);
		return content;
	}
	
}//eof-class

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
import java.util.Map;

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
public class TestState2 {

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
		String files = TestState2.readFile();
		Map map = CaptureTagUtils.getMapFromString(files);
		System.out.println("\n\nThe hashtable is");
		System.out.println(map.toString());
	}
	
	public static void test()
	throws Exception
	{
		String hello = TestState2.readFile();
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

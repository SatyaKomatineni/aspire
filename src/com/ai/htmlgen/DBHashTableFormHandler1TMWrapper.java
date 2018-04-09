/*
 * Created on Dec 14, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.htmlgen;

import java.util.Map;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.db.rel2.ATransactionalPart;

/**
 * @author Satya
 *
 */
public class DBHashTableFormHandler1TMWrapper extends ATransactionalPart 
{

	protected Object executeRequestForPartTM(String requestName, Map inArgs)
			throws RequestExecutionException 
	{
		DBHashTableFormHandler1TM a = new DBHashTableFormHandler1TM();
		a.executeRequest(requestName,inArgs);
		return a;
	}
}//eof-class

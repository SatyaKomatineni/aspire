package com.ai.akc.lists;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.RequestExecutorResponse;
import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;
import com.ai.common.Utils;
import com.ai.data.DataException;
import com.ai.data.DataUtils;
import com.ai.data.IDataCollection;
import com.ai.data.IDataRow;
import com.ai.data.IIterator;
import com.ai.data.SimpleDataRowDictionary;
import com.ai.db.DBException;
import com.ai.parts.DBProcedure;

/*
 * inputs
 * ******
 * akcListOwnerUserId:  (Owner of the list)
 * akcListName: 		(unique for this context)
 * akcListId:			(unique list id - use this instead)
 * akcListColumnNames: 	comma separated
 * key/value pairs that match the list definition for inserts
 * 
 * Logic
 * ******
 * 1. locate the list in the database (listname)
 * 2. Create a list object in java
 * 3. Find a select statement to get the rows
 * 4. Get the collection and return it 
 * 
 * Access Rules
 * ******************
 * A list can be one of 
 * 	a) public/global: global
 * 	b) Any logged in user: loggedin_user
 * 	c) or only by the owned user: owner_only
 * A list definition should indicate one of these
 * These can be applied at the URL level!!
 *
 * PropertyFile db definitions
 * ***************************
 * request.akclist.readListDefinition
 * request.akclist.selectListView
 *
 * request.akclist.selectListView
 * ********************************
 * {akcListViewSelectStatement.empty}
 * The entire select statement will be constructed
 * by the client.
 * 
	request.akclist.selectListView.classname=com.ai.db.DBRequestExecutor2
	request.akclist.selectListView.db=reportsDB
	request.akclist.selectListView.stmt={akcListViewSelectStatement.empty} 
 *
 * Special Note:
 * *************
 * Please note this is a singleton
 * Dont' keep instance variables in the class
 */
public class ListViewSelector 
extends DBProcedure
{
	public static final String c_akclistViewSelectStatement="akclistviewselectstatement";
	public static final String c_selectListViewRequestName="akcList.selectListView";
	@Override
	protected Object executeDBProcedure(String requestName, Hashtable arguments)
			throws DBException 
    {
		try
		{
			//get the list definition
			AkcList akcList = AkcListReader.getAkcList(requestName, arguments);
			AppObjects.info(this,"AkcList:%1s successfully constructed", akcList.listName);
			
			//insert values using the list
			AppObjects.info(this,"Selecting rows for this list %1s", akcList.listName);
			String selectStatement = akcList.getSelectStatement();
			AppObjects.info(this,"The select statement is:%1s", selectStatement);
			return getSQLResults(requestName,selectStatement,arguments);
		}
		catch(RequestExecutionException x)
		{
			throw new DBException("Request execution problem",x);
		} 
		catch (DataException e) 
		{
			throw new DBException("Not able to read the list definition",e);
		}
	}//eof-method
	private IDataCollection getSQLResults(String requestName, String selectStatement, Hashtable arguments)
	throws RequestExecutionException
	{
		arguments.put(c_akclistViewSelectStatement, selectStatement);
		return (IDataCollection)AppObjects.getObject(c_selectListViewRequestName,arguments);
	}//eof-method
}//eof-class

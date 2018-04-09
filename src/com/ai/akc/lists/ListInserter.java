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
 * 3. Pass and get an insert statement
 * 4. The insert statement should accomodate
 * 		. user, ownerid, list-definition-id: storage
 * 5. Get multiple statements
 * 6. insert each statement
 * 7. Thats it. This is the first test
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
 * request.akclist.insertListRow
 *
 *
 * request.akclist.readListDefinition
 * ***********************************
 * select * from t_list_definition
 * where 1=1 and
 * f_list_key_name = {akcListName.quote}
 * f_owner_user_id = {akcListOwnerId}
 *  
 *  
 * Special Note:
 * *************
 * Please note this is a singleton
 * Dont' keep instance variables in the class
 */
public class ListInserter 
extends DBProcedure
{
	//This is used to insert a row into the database
	//These are used with getObject to execute sql statements
	public static final String C_INSERT_ROW_STATEMENT_NAME = "akcList.insertListRow";
	
	//Make sure these are lower case
	public static final String C_COLUMN_NAME_SEGMENT = "akclist_columnnamessegment";
	public static final String C_COLUMN_VALUE_SEGMENT = "akclist_columnvaluessegment";
	public static final String C_TABLE_NAME = "akclist_tablename";
	private static final Object C_LIST_DEFINITION_ID_COLUMN_NAME = "f_list_definition_id";

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
			AppObjects.info(this,"Populating rows for this list %1s", akcList.listName);
			insertValues(akcList,requestName, arguments);
			return new Boolean(true);
		}
		catch(RequestExecutionException x)
		{
			throw new DBException("Request execution problem",x);
		} 
		catch (DataException e) 
		{
			throw new DBException("Not able to read the list definition",e);
		}
	}

	private void insertValues(AkcList akcList, String requestName, Hashtable arguments)
	throws RequestExecutionException
	{
		//akcList
		Map<String, String> submittedListRow =
			getSubmittedListRow(arguments);
		Collection<InsertStatement> statementCollection = 
		akcList.getInsertStatements(submittedListRow);
		
		//for each insert statement insert it
		for(InsertStatement ins: statementCollection)
		{
			insertARow(akcList.listDefinitionId, ins,arguments);
		}
	}
	/**
	 * Insert the row into the specified table.
	 */
	private void insertARow(int listDefinitionId, InsertStatement ins, Hashtable arguments)
	throws RequestExecutionException
	{
		AppObjects.info(this,"Inserting a row into akc list population table");
		String columnNameString = ins.getInsertColumnNames();
		String valuesString = ins.getInsertColumnValues();
		arguments.put(C_COLUMN_NAME_SEGMENT, columnNameString);
		arguments.put(C_COLUMN_VALUE_SEGMENT, valuesString);
		arguments.put(C_TABLE_NAME, ins.tablename);
		arguments.put(C_LIST_DEFINITION_ID_COLUMN_NAME, Integer.toString(listDefinitionId));
		RequestExecutorResponse result = 
			(RequestExecutorResponse)AppObjects.getObject(C_INSERT_ROW_STATEMENT_NAME, arguments);
	}
	
	private Map<String, String> getSubmittedListRow(Hashtable args)
	{
		Map<String,String> submittedListRowMap = new HashMap<String,String>();
		String commaSeparatedColumnNames = (String)args.get("akclistcolumnnames");
		Utils.massert(this,
				commaSeparatedColumnNames != null,
				"No value for key akcListColumnNames ");
		List<String> columnNamesList = Tokenizer.tokenizeAsList(commaSeparatedColumnNames, ",");
		for(String columnName: columnNamesList)
		{
			String columnValue = (String)args.get(columnName);
			Utils.massert(this, 
					(columnValue != null), 
					"Column name:" + columnName + " does not have a value.");
			submittedListRowMap.put(columnName, columnValue);
		}
		Utils.massert(this, 
				(submittedListRowMap.isEmpty() == false)
				,"There are no values to insert");
		return submittedListRowMap;
	}
}//eof-class

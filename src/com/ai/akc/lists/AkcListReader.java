package com.ai.akc.lists;

import java.util.Hashtable;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.data.DataException;
import com.ai.data.DataUtils;
import com.ai.data.IDataCollection;
import com.ai.data.IDataRow;
import com.ai.data.IIterator;
import com.ai.data.SimpleDataRowDictionary;
import com.ai.db.DBException;

/*
 *
 * Arguments
 * **********************************
 * akcListName
 * akcListOwnerId
 * 
 * request.akclist.readListDefinition
 * ***********************************
 * select * from t_list_definition
 * where 1=1 and
 * f_list_key_name = {akcListName.quote}
 * f_owner_user_id = {akcListOwnerId}
*/  
public class AkcListReader 
{
	public static final String C_READ_LIST_DEFINITION_STATEMENT_NAME = "akclist.readListDefinition";

	public static AkcList getAkcList(String requestName, Hashtable arguments)
	throws DBException, RequestExecutionException, DataException
	{
			IDataCollection dc = null;
			try
			{
				dc = (IDataCollection)AppObjects.getObject(C_READ_LIST_DEFINITION_STATEMENT_NAME,arguments);
				IIterator itr = dc.getIIterator();
				itr.moveToFirst();
				if (itr.isAtTheEnd())
				{
					//THere are no rows
					AppObjects.error("AkcListReader","No list definition is present for this list of arguments: %1s", arguments);
					throw new DBException("Error: No list definition found based on incoming arguments");
				}
				
				//There is atleast one row
				//This row is the list definition
				//see documentation to see what a list definition may contain
				//Construct an AkcList from the definition
				IDataRow idr = (IDataRow)itr.getCurrentElement();
				AkcList akcList = new AkcList(new SimpleDataRowDictionary(idr));
				return akcList;
			}
			finally
			{
				DataUtils.closeCollectionSilently(dc);
			}
		}//eof-method
}

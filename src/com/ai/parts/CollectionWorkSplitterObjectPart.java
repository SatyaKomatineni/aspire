/*
 * Goal
 * *****
 * 1. Provide a way to spawn multiple requests
 * for each row in a collection
 * 2. if the collection is empty call an  empty request
 * 3. For each collection call a begin request
 * 4. For each row call a processRow request
 * 5. call an end request
 * 6. extend the DBProcedure 
 * 
 * Configuration
 * *************
 * collectionRequestName
 * emptyRequestName
 * beginRequestName
 * rowRequestName
 * endRequestName
 * 
 */
package com.ai.parts;

import java.util.Hashtable;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.data.DataException;
import com.ai.data.DataUtils;
import com.ai.data.IDataCollection;
import com.ai.data.IDataRow;
import com.ai.data.IIterator;
import com.ai.db.DBException;

public class CollectionWorkSplitterObjectPart 
extends DBProcedureObject
{
	 private String collectionRequestName; //mandatory
	 private String emptyRequestName; //optional
	 private String beginRequestName; //optional
	 private String rowRequestName; //optional
	 private String endRequestName; //optional

	 private void populateConfigurationArguments() throws ConfigException 
	 {
		emptyRequestName = this.readConfigArgument("emptyRequestName", null); 
		beginRequestName = this.readConfigArgument("beginRequestName", null); 
		rowRequestName = this.readConfigArgument("rowRequestName", null); 
		endRequestName = this.readConfigArgument("endRequestName", null); 
		collectionRequestName = this.readConfigArgument("collectionRequestName"); 
	 }
	@Override
	protected Object executeDBProcedure(String requestName, Hashtable arguments)
	throws DBException 
	{
		IDataCollection dc = null;
		try
		{
			this.populateConfigurationArguments();
			dc = (IDataCollection)AppObjects.getObject(this.collectionRequestName,arguments);
			IIterator itr = dc.getIIterator();
			itr.moveToFirst();
			if (itr.isAtTheEnd())
			{
				//THere are no rows
				AppObjects.warn(this,"No rows retrieved for request %1s",requestName);
				processEmptyCollection(arguments);
				return new Boolean(true);
			}
			this.processBeginCollection(arguments);
			if (rowRequestName != null)
			{
				//row Request available
				for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
				{
					IDataRow idr = (IDataRow)itr.getCurrentElement();
					this.processRow(arguments, idr);
				}
			}
			
			this.processEndCollection(arguments);
		
			return new Boolean(true);
		}
		catch(Exception x)
		{
			throw new DBException("An underlying exception:",x);
		}
		finally
		{
			if (dc != null)
			{
				DataUtils.closeCollectionSilently(dc);
			}
		}
	}
	protected void processEmptyCollection(Hashtable originalArgs)
	throws RequestExecutionException
	{
		if (emptyRequestName == null)
		{
			AppObjects.trace(this,"No empty request for: %1s",this.thisRequestName);
			return;
		}
		AppObjects.trace(this,"Processing empty request: %1s",this.emptyRequestName);
		AppObjects.getObject(this.emptyRequestName,originalArgs);
	}
	protected void processRow(Hashtable originalArgs, IDataRow dataRow)
	throws DataException, RequestExecutionException
	{
		if (rowRequestName == null)
		{
			AppObjects.trace(this,"No row request for: %1s",this.thisRequestName);
			return;
		}
		DataUtils.fillAMap(dataRow, originalArgs);
		//orginal args is now filled with the current row
		//make sure the field names are unique
		AppObjects.getObject(this.rowRequestName,originalArgs);
	}
	protected void processBeginCollection(Hashtable originalArgs)
	throws RequestExecutionException
	{
		if (beginRequestName == null)
		{
			AppObjects.trace(this,"No begin request for: %1s",this.thisRequestName);
			return;
		}
		AppObjects.trace(this,"Processing beginning of collection: %1s",this.beginRequestName);
		AppObjects.getObject(this.beginRequestName,originalArgs);
	}
	protected void processEndCollection(Hashtable originalArgs)
	throws RequestExecutionException
	{
		if (endRequestName == null)
		{
			AppObjects.trace(this,"No end request for: %1s",this.thisRequestName);
			return;
		}
		AppObjects.trace(this,"Processing End of collection: %1s",this.endRequestName);
		AppObjects.getObject(this.endRequestName,originalArgs);
	}
}

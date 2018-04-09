package com.ai.db.events;

import java.sql.Connection;
import java.util.Hashtable;

import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.data.DataException;
import com.ai.data.IDataCollection;
import com.ai.db.DBException;
import com.ai.servlets.AspireConstants;

/**
 * @author Satya Komatineni Oct 17, 2005
 * 
 * How to use this class
 * *********************
 * request.aspire.db.connectionevents.classname=com.ai.db.events.ConnectionEventDistributor
 * request.aspire.db.connectionevents.eventHandlerList=handler1,handler2
 * 
 * #Log the connection events
 * request.handler1.classname=com.ai.db.events.ConnectionEventLogger
 * 
 * #Call a select statement to set certain connection properties
 * request.handler2.classname=com.ai.db.events.RequestBasedConnectionEventDemultiplexer
 * request.handler2.onCreateConnectionRequestName=stmt1
 * request.handler2.onPreCloseConnectionRequestName=stmt2
 * request.handler2.onGetConnectionRequestName=stmt3
 * request.handler2.onPutConnectionRequestName=stmt3
 * 
 * request.stmt1.classname=com.ai.db.DBRequestExecutor2
 * request.stmt1.stmt=some select statement
 * #Don't specify the usual .db here
 * 
 * @see 
 */
public class RequestBasedConnectionEventDemultiplexer 
implements IConnectionEvents, IInitializable 
{
	private String onCreateConnectionRequestName = null;
	private String onPreCloseConnectionRequestName = null;
	private String onGetConnectionRequestName = null;
	private String onPutConnectionRequestName = null;
	
	public void initialize(String requestName) 
	{
		onCreateConnectionRequestName
		= AppObjects.getValue(requestName + ".onCreateConnectionRequestName",null);
		
		onPreCloseConnectionRequestName
		= AppObjects.getValue(requestName + ".onPreCloseConnectionRequestName",null);

		onGetConnectionRequestName
		= AppObjects.getValue(requestName + ".onGetConnectionRequestName",null);

		onPutConnectionRequestName
		= AppObjects.getValue(requestName + ".onPutConnectionRequestName",null);
		
	}
	
	private void executeRequest(Connection con, String requestName)
	throws DBException
	{
		try
		{
			if (requestName == null)
			{
				return;
			}
			Hashtable args = new Hashtable();
			args.put(AspireConstants.JDBC_CONNECTION_PARAM_KEY,con);
			Object o = AppObjects.getObject(requestName,args);
			if (o instanceof IDataCollection)
			{
				IDataCollection idc = (IDataCollection)o;
				idc.closeCollection();
				return;
			}
			//It is not of type IDataCollection
			//nothing to do
			return;
		}
		catch(RequestExecutionException x)
		{
			throw new DBException("Error: could not execute request.",x);
		}
		catch(DataException x)
		{
			throw new DBException("Error: could not execute request.",x);
		}
	}
    public boolean onCreateConnection(Connection con) throws DBException 
    {
    	executeRequest(con,this.onCreateConnectionRequestName);
        return true;
    }

    public boolean onPreCloseConnection(Connection con) throws DBException 
    {
    	executeRequest(con,this.onPreCloseConnectionRequestName);
        return true;
    }

    public boolean onGetConnection(Connection con) throws DBException 
    {
    	executeRequest(con,this.onGetConnectionRequestName);
        return true;
    }

    public boolean onPutConnection(Connection con) throws DBException 
    {
    	executeRequest(con,this.onPutConnectionRequestName);
        return true;
    }
}//eof-class

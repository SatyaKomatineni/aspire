package com.ai.resourcecleanup;

import java.sql.Connection;
import java.util.Hashtable;

import com.ai.application.utils.AppObjects;
import com.ai.common.CommonException;
import com.ai.db.DBException;
import com.ai.db.events.IConnectionEvents;

/**
 * @author Satya Komatineni Oct 17, 2005
 */
public class ConnectionCleanupHandler implements IConnectionEvents 
{
	private Hashtable ht = new Hashtable();
    public boolean onCreateConnection(Connection con) throws DBException 
    {
        AppObjects.trace(this,"A real new connection %1s is being created", con);
        return true;
    }

    public boolean onPreCloseConnection(Connection con) throws DBException 
    {
        AppObjects.trace(this,"Connection %1s is being closed", con);
        return true;
    }

    public boolean onGetConnection(Connection con) throws DBException 
    {
    	try
    	{
	        AppObjects.trace(this,"Connection %1s is obtained. Register it in ResourceCleanupRegistry", con);
	        ConnectionResource cr = new ConnectionResource(con);
	        ht.put(con, cr);
	        SWIResourceCleanup.addResource(cr);
	        return true;
    	}
    	catch(CommonException x)
    	{
    		throw new DBException("Error: Not able to add a resource to the cleaun up registry");
    	}
    }

    public boolean onPutConnection(Connection con) throws DBException 
    {
    	try
    	{
	        AppObjects.trace(this,"Connection %1s is being retuned to the pool. Un register it in ResourceCleanupRegistry", con);
	        ConnectionResource cr = (ConnectionResource)ht.get(con);
	        if (cr == null)
	        {
	        	AppObjects.trace(this, "Connection close wrapper not found!!");
	        	return true;
	        }
	        //connection wrapper is there
	        SWIResourceCleanup.removeResource(cr);
	        return true;
    	}
    	catch(CommonException x)
    	{
    		throw new DBException("Error: Not able to add a resource to the cleaun up registry");
    	}
    	finally
    	{
    		AppObjects.trace(this,"Removing the connection from local registry");
    		this.ht.remove(con);
    	}
    }
}//eof-class

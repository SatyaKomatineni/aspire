package com.ai.db.events;

import java.sql.Connection;

import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;

/**
 * @author Satya Komatineni Oct 17, 2005
 */
public class ConnectionEventLogger implements IConnectionEvents 
{
    public boolean onCreateConnection(Connection con) throws DBException 
    {
        AppObjects.info(this,"Connection %1s is being created.", con);
        return true;
    }

    public boolean onPreCloseConnection(Connection con) throws DBException 
    {
        AppObjects.info(this,"Connection %1s is being closed.", con );
        return true;
    }

    public boolean onGetConnection(Connection con) throws DBException 
    {
        AppObjects.info(this,"Connection %1s is being requested from the pool", con);
        return true;
    }

    public boolean onPutConnection(Connection con) throws DBException 
    {
        AppObjects.info(this,"Connection %1s is being returned to the pool", con);
        return true;
    }
}//eof-class

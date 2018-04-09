package com.ai.db.events;

import java.sql.Connection;

import com.ai.db.DBException;

/**
 * @author Satya Komatineni Oct 17, 2005
 */
public interface IConnectionEvents 
{
    public static String NAME = "aspire.db.connectionevents";
    public boolean onCreateConnection(Connection con) throws DBException;
    public boolean onPreCloseConnection(Connection con) throws DBException;
    public boolean onGetConnection(Connection con) throws DBException;
    public boolean onPutConnection(Connection con) throws DBException;
}

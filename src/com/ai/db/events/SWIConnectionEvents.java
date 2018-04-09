package com.ai.db.events;

import java.sql.Connection;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;

/**
 * @author Satya Komatineni Oct 17, 2005
 */
public class SWIConnectionEvents 
{
    public static IConnectionEvents m_events = null;

    static
    {
       try
       {
          m_events = (IConnectionEvents)AppObjects.getObject(IConnectionEvents.NAME,null);
       }
       catch(RequestExecutionException x)
       {
          m_events=null;
          AppObjects.log("Warn: No Connection events class available. No events will be reported");
       }
    }
    static public boolean onCreateConnection(Connection con) throws DBException
    {
        if (m_events == null) return true;
        return m_events.onCreateConnection(con);
    }
    static public boolean onPreCloseConnection(Connection con) throws DBException
    {
        if (m_events == null) return true;
        return m_events.onPreCloseConnection(con);
    }
    static public boolean onGetConnection(Connection con) throws DBException
    {
        if (m_events == null) return true;
        return m_events.onGetConnection(con);
    }
    static public boolean onPutConnection(Connection con) throws DBException
    {
        if (m_events == null) return true;
        return m_events.onPutConnection(con);
    }
}//eof-class

package com.ai.db.cp4;

import java.sql.Connection;

import com.ai.common.AICalendar;

//*************************************************************************
//* a private class
//*************************************************************************
class ConnectionPoolConnectionItem                                         
{
 Connection m_con;
 public String m_dataSourceName;
 
 // null implies it is free
 public long m_lastCheckoutTime;
 // creation time
 public  long m_creationTime;

 ConnectionPoolConnectionItem(Connection inCon, String inDataSourceName )
 {
    m_con = inCon;
    m_dataSourceName = inDataSourceName; 
 }
 public void touch()
 {
    m_lastCheckoutTime = System.currentTimeMillis();
 }      
 public String toString()
 {
    return new String("{datasource name: " + m_dataSourceName
                      + ", last check out time : " + AICalendar.formatTimeInMilliSeconds(m_lastCheckoutTime)
                      + ", creation time : " + AICalendar.formatTimeInMilliSeconds(m_creationTime)
                      + "}");
 }
} // end of ConnectionPoolConnectionItem  

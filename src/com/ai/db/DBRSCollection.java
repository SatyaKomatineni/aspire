/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.db;

import com.ai.data.*;
import java.sql.*;
import java.util.*;
import com.ai.application.utils.AppObjects;
import java.text.*;
/**
 * @deprecated in favor of DBRSCollection2
 */
public class DBRSCollection implements IDataCollection, IIterator 
{
   private static SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yy");
   
   ResultSet m_rs = null;
   ResultSetMetaData m_rsMetaData = null;
      // Initialized in the constructor
      
   private IMetaData m_metadata = null;
      // valid only via getIMetadata
      
   private Connection m_con = null;
   private Statement m_statement = null;
     // value set during connection
     // used to cache it for control at the time of closure
   private boolean m_bAtTheEnd = false;
   
   private boolean m_bConnectionOwner = true;
	private boolean bCollectionClosed = false;
                    
   private void closeDBResources()
      throws java.sql.SQLException
   {
            if (m_rs != null) m_rs.close();
            if (m_statement != null) m_statement.close();
            if (m_bConnectionOwner)
            {
               if (m_con != null) m_con.close();
            }               
   }
   public void closeCollection()
         throws com.ai.data.DataException
   {
         if (bCollectionClosed == true)
         {
            AppObjects.log("info.db: Collection already closed");
            return;
         }
         try {
		      bCollectionClosed = true;
            m_rs.close();
            m_statement.close();
            if (m_bConnectionOwner)
            {
               // return the connection only if I own it
               IConnectionManager mgr = 
                  (IConnectionManager)AppObjects
                           .getIFactory()
                           .getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null);
               mgr.putConnection(m_con);                        
            }
         }
         catch (java.sql.SQLException x)
         {
            AppObjects.log("Could not close the resultset");
            throw new DataException("Could not close the resultset", x);
         }
         catch(com.ai.application.interfaces.RequestExecutionException x)
         {
            AppObjects.log("Could not obtain the connection manager for closing the connection");
            AppObjects.log(x);
         }
         catch(com.ai.db.DBException x)
         {
            AppObjects.log("Could not return the connection");
            AppObjects.log(x);
         }
   }
   public DBRSCollection(Connection con, boolean bConnectionOwner, String statementString)
      throws java.sql.SQLException
   {
      try 
      {
        m_con = con;
        m_bConnectionOwner = bConnectionOwner;
        m_statement = m_con.createStatement();
        m_rs = m_statement.executeQuery(statementString);
        m_rsMetaData = m_rs.getMetaData();
      }
      catch (java.sql.SQLException x)
      {
         closeDBResources();
         throw x;
      }        
   }
   public  IMetaData getIMetaData() throws com.ai.data.DataException
   {
      try
      {
        if (m_metadata != null) return m_metadata;
        m_metadata = createMetaData();
        return m_metadata;
      }
      catch(java.sql.SQLException x)
      {
         try{closeDBResources();}
         catch(SQLException y) { AppObjects.log(y);}
         throw new DataException("Can not get Meta data", x);
      }
   }
   private IMetaData createMetaData()
      throws java.sql.SQLException
   {
         Vector m_columnNames = new Vector();
         ResultSetMetaData  rsMetaData = m_rs.getMetaData();
         for(int i=1;i <= rsMetaData.getColumnCount();i++)
         {
            m_columnNames.addElement(rsMetaData.getColumnName(i));
//            m_columnNames.addElement("test");
         }                      
         return new VectorMetaData(m_columnNames );
   }
   
   public IIterator getIIterator()
   {
      return this;
   }
   
        public void moveToFirst()
                  throws DataException
        {                        
         try   
         {  
            if( m_rs.next() == false)
            {
               m_bAtTheEnd = true;
            }
         }
         catch(java.sql.SQLException x)
         {
            try{closeDBResources();}
            catch(SQLException y) { AppObjects.log(y);}
            throw new com.ai.data.DataException("Can not move to first",x);
         }
            
        }
        public void moveToNext()
                  throws DataException
        {                        
             try 
             { 
               if( m_rs.next() == false)
               {
                  m_bAtTheEnd = true;
               }
             }
             catch(java.sql.SQLException x)
             {
               try{closeDBResources();}
               catch(SQLException y) { AppObjects.log(y);}
                throw new com.ai.data.DataException("Can not move to next",x);
             }
        }               
        public boolean isAtTheEnd()
                  throws DataException
        {    
               return m_bAtTheEnd;
        }               
        
        public Object getCurrentElement()
                  throws DataException
        {   
            try 
            {                     
               StringBuffer row = new StringBuffer();
               for(int i = 1;i<=getIMetaData().getColumnCount();i++)
               {
                  if ((m_rsMetaData.getColumnType(i)) == java.sql.Types.DATE )
                  {
                     row.append("|").append(formatDate(m_rs.getDate(i)));   
                  }
                  else if ((m_rsMetaData.getColumnType(i)) == java.sql.Types.TIMESTAMP )
                  {
                     row.append("|").append(formatDate(m_rs.getTimestamp(i)));   
                  }
                  else
                  {
                     String value = m_rs.getString(i);
                     AppObjects.trace(this,"retrived value :%1s", value);
                     if (value == null)
                     {
                        AppObjects.log("value is really null");
                        value ="none";
                     }
                     if (value.equals("null"))
                     {
                        value = "none";
                     }
                     row.append("|").append(value);
                  }
               }
               return new DataRow(this.getIMetaData(),row.toString(),"|");
            }               
            catch(java.sql.SQLException x)
            {
               AppObjects.log("Can not get cur element");
               AppObjects.log(x);
               try{closeDBResources();}
               catch(SQLException y) { AppObjects.log(y);}
               throw new com.ai.data.DataException("Can not get current element",x);
            }
        }
        private String formatDate(java.util.Date date )
        {
            if (date == null) return "none";
            return dateFormat.format(date);
        }
} 


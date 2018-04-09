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

public class DBRSCollection1 implements IDataCollection1, IIterator 
{
   private static SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yy");
   
   ResultSet m_rs = null;
   ResultSetMetaData m_rsMetaData = null;
      // Initialized in the constructor
      
   private IMetaData m_metadata = null;
      // valid only via getIMetadata
      
   private Connection m_con = null;
   private Statement m_statement = null;
//   private Statement m_statement = null;
     // value set during connection
     // used to cache it for control at the time of closure
   private boolean m_bAtTheEnd = false;
   private boolean m_bConnectionOwner = true;
   private boolean bMovedToFirst = false;
   private boolean bCollectionClosed = false;
         
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
               // return the connection only if you are the owner
               AppObjects.log("db: DBRSCollection1: Returning connection as this object is the owner");
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
   public DBRSCollection1(Connection con, boolean bConnectionOwner, Statement stmt, ResultSet rs)
      throws java.sql.SQLException
   {
      m_con = con;        // needed forr cleanup
      m_bConnectionOwner = bConnectionOwner;
      m_statement = stmt; // needed for cleanup
      m_rs = rs;
      m_rsMetaData = m_rs.getMetaData();
   }
   private void closeDBResources()
      throws java.sql.SQLException
   {
          AppObjects.log("db: Closing statment and result set due to an sql exception");
            m_rs.close();
            m_statement.close();
            if (m_bConnectionOwner)
            {
               m_con.close();
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
         try
         {
            // close statement and the result set
            closeDBResources();
         }
         catch(SQLException y) { AppObjects.log(y);}
         throw new DataException("Can not get Meta data", x);
      }
   }
   private IMetaData createMetaData()
      throws java.sql.SQLException
   {
         Vector m_columnNames = new Vector();
         if (m_rsMetaData == null)
         {
            AppObjects.log("Error: Null meta data ");
         }
         for(int i=1;i <= m_rsMetaData.getColumnCount();i++)
         {
            m_columnNames.addElement(m_rsMetaData.getColumnName(i));
//            m_columnNames.addElement("test");
         }                      
         return new VectorMetaData(m_columnNames );
   }
   
      public IIterator getIIterator()
      {
         return this;
      }
   
      public IIterator getDataRowIterator()
         throws DataException
      {
         return new DataRowIterator(getIIterator(),getIMetaData());
      }            
      
      public void moveToFirst()
         throws DataException
      {
         if (bMovedToFirst)
         {
            return;
         }                        
         else
         {
            bMovedToFirst = true;
         }
         try   
         {  
            if( m_rs.next() == false)
            {
               m_bAtTheEnd = true;
            }
         }
         catch(java.sql.SQLException x)
         {
            try {closeDBResources();}
            catch(SQLException y) { AppObjects.log(y); }
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
                throw new com.ai.data.DataException("Can not move to first",x);
             }
        }               
        /**
         * Is the iterator at the end
         */
        public boolean isAtTheEnd()
                  throws DataException
        {    
            // The idea is to enable the iterator to reply to this 
            // when the iterator is obtained.
            if (bMovedToFirst)
            {
               // If the collection is already moved to first
               return m_bAtTheEnd;
            }              
            // Collection has not been attempted to be moved to first
            moveToFirst();
            return m_bAtTheEnd;
        }               
        
        /**
         * getCurrentElement
         */
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
                     AppObjects.info(this,"dbrs: retrived value :%1s",value);
                     if (value == null)
                     {
                        AppObjects.log("dbrs: value is really null");
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
               try{closeDBResources();}
               catch(SQLException y) { AppObjects.log(y);}
               
               AppObjects.log("Can not get the current element");
               AppObjects.log(x);
               throw new com.ai.data.DataException("Can not get the current element",x);
            }
        }
        /**
         * formatDate
         */
        private String formatDate(java.util.Date date )
        {
            if (date == null) return "none";
            return dateFormat.format(date);
        }
} 


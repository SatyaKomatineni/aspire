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
 * A variation on DBRSCollection1 and replaces DBRSCollection1 for future reference
 *
 * Uses IDataRow as the only primary interface for dealing with rows
 */
public class DBRSCollection2 implements IDataCollection1
{
   
   ResultSet m_rs = null;
   ResultSetMetaData m_rsMetaData = null;
   IMetaData m_metaData = null;
      
   private Connection m_con = null;
   private Statement m_statement = null;
//   private Statement m_statement = null;
     // value set during connection
     // used to cache it for control at the time of closure
   private boolean m_bConnectionOwner = true;
   private boolean bCollectionClosed = false;
         
   public DBRSCollection2(Connection con, boolean bConnectionOwner, Statement stmt, ResultSet rs)
      throws java.sql.SQLException
   {
      m_con = con;        // needed forr cleanup
      m_bConnectionOwner = bConnectionOwner;
      m_statement = stmt; // needed for cleanup
      m_rs = rs;
      m_rsMetaData = m_rs.getMetaData();
      m_metaData = DBUtils.createMetaData(m_rsMetaData);
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
   
      public IIterator getIIterator() throws DataException
      {
         return new RSDataRowIterator(m_rs,m_metaData,this);
      }
   
      public IIterator getDataRowIterator()
         throws DataException
      {
         return new DataRowIterator(getIIterator(),m_metaData);
      }            
   // Responsibility for the IDataCollection signature   
   public  IMetaData getIMetaData() throws com.ai.data.DataException
   {
      return m_metaData;
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
            throw new DataException("Could not obtain the CM for closing the connection",x);
         }
         catch(com.ai.db.DBException x)
         {
            throw new DataException("Could not return the connection to the pool",x);
         }
   }
}  


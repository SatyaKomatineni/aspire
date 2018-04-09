/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.db.rel2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import com.ai.application.utils.AppObjects;
import com.ai.data.DataException;
import com.ai.data.DataRowIterator;
import com.ai.data.IDataCollection1;
import com.ai.data.IIterator;
import com.ai.data.IMetaData;
import com.ai.db.DBException;
import com.ai.db.DBUtils;
import com.ai.db.RSDataRowIterator;

/**
 * A variation on DBRSCollection1 and replaces DBRSCollection1 for future reference
 *
 * Uses IDataRow as the only primary interface for dealing with rows
 */
public class DBRSCollection3 implements IDataCollection1, IDelayedRead
{
   
   ResultSet m_rs = null;
   ResultSetMetaData m_rsMetaData = null;
   IMetaData m_metaData = null;
      
   private Connection m_con = null;
   private Statement m_statement = null;
   private boolean bCollectionClosed = false;
         
   public DBRSCollection3(Connection con, Statement stmt, ResultSet rs)
      throws java.sql.SQLException
   {
      m_con = con;        // needed forr cleanup
      m_statement = stmt; // needed for cleanup
      m_rs = rs;
      m_rsMetaData = m_rs.getMetaData();
      m_metaData = DBUtils.createMetaData(m_rsMetaData);
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
            if (m_delayedReadResource != null)
            {
            	this.m_delayedReadResource.release();
            }
         }
         catch (java.sql.SQLException x)
         {
            AppObjects.log("Could not close the resultset");
            throw new DataException("Could not close the resultset", x);
         }
         catch(DBException x)
		 {
         	throw new DataException("Could not release connections through transactional context");
		 }
   }
   
	public boolean isDelayedReadActive() throws DBException
	{
		return true;
	}
	
	private IDelayedReadResource m_delayedReadResource = null;
	public void registerResource(IDelayedReadResource resource) throws DBException
	{
		m_delayedReadResource = resource;
	}
   
}//eof-class


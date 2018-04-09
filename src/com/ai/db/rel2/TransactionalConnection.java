/*
 * Created on Nov 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.sql.Connection;
import java.sql.SQLException;

import com.ai.application.utils.AppObjects;

/**
 * @author Satya
 *
 * what does it do?
 * *****************
 * 1. Represents the idea of a connection tied to a data source
 * 2. Primarily tied to a transactional context
 * 3. Acts like a holder of a connection and its associated attributes
 */
public class TransactionalConnection 
{
	public Connection m_connection = null;
	public String m_datasourceName = null;
	public boolean isCommitNeeded = false;
	public boolean savedAutoCommitFlag = false;
	
	public void tagForCommit()
	{
		isCommitNeeded = true;
	}
	public TransactionalConnection(String datasourcename, Connection con)
	throws SQLException
	{
		m_connection = con;
		m_datasourceName = datasourcename;
		savedAutoCommitFlag = con.getAutoCommit();
		con.setAutoCommit(false);
		AppObjects.trace(this,"Set auto commit flag to false. Original is:" + savedAutoCommitFlag);
	}
	public void restoreAutoCommit() throws SQLException
	{
		AppObjects.trace(this,"Restoring auto commit flag to its original status:" + savedAutoCommitFlag);
		m_connection.setAutoCommit(this.savedAutoCommitFlag);
	}
}

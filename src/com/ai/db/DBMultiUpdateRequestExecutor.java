/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;

import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.sql.*;
import java.util.*;
import com.ai.common.*;

// Please move this object to com.ai.common or db
// 
import com.ai.application.interfaces.RequestExecutorResponse;
       
public class DBMultiUpdateRequestExecutor extends DBBaseRequestExecutor
{
   public Object executeStatements(Connection con,
                                       Object args,
                                       Vector statementVector,
                                       Vector modifiedStatementVector )
                                       throws java.sql.SQLException
   {
      con.setAutoCommit(false);
      try 
      {
        for(Enumeration e=modifiedStatementVector.elements();e.hasMoreElements();)
        {
           String modifiedStatement = (String)e.nextElement();
           executeStatement(con,modifiedStatement);
        }
      }
      catch(java.sql.SQLException x)
      {
         // update failed
         con.rollback();
         con.setAutoCommit(true);      
         return new RequestExecutorResponse(false);
      }
      con.commit();
      con.setAutoCommit(true);
      return new RequestExecutorResponse(true);
   }                                       
   private boolean executeStatement(Connection con, String statement )
         throws java.sql.SQLException
   {
      Statement stmt = con.createStatement();
      AppObjects.trace(this,"Executing : %1s", statement );
      int n = stmt.executeUpdate(statement);
      return true;
   }
}

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db.rel2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import oracle.jdbc.driver.OracleTypes;

import com.ai.application.interfaces.RequestExecutorResponse;
import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;

/**
 * Replacement for StoredProcedureExecutor
 *
 */
 
/**
 * Execute stored procedures, both query and update types
 *
 * 1. Derives from DBBaseJavaProcedure
 *
 * 2. Responsible for overriding only 1 method called executeProcedure
 *
 * 3. If you are executing an update you are responsible for closing the statement
 *
 * 4. If you are executing an update your return object should be RequestExecutorResponse
 *
 * 5. For executing queries
 *
 *    1. return object should be DBRSCollection2
 *    2. Simply pass the bConnectionCreator value to this DBRScollection2 constructor
 *    3. The collection is responsible for closing the statement etc
 *    4. Close the statement only in case of failure
 *
 * 6. This object is a singleton, so do not maintain any internal state that is not readonly.
 *
 * 7. In other words if you are calling other methods, pass the parameters explicitly
 *
 * 8. Always throw back the exception that you catch
 *
 */
public class StoredProcedureExecutor3 extends DBBaseJavaProcedure3
{
   public Object executeProcedure(Connection con
                                           ,String requestName
                                           , Hashtable arguments )
         throws DBException, SQLException
   {
      try 
      {
         String statementString
         = AppObjects.getValue(requestName + ".stmt");
         
         AppObjects.log("db: Arguments in to the statement are : " + arguments );
         
        String modifiedStatementString = getArgSubstitutor().substitute(statementString
                                                            ,arguments);
         AppObjects.log("db: statement to execute : " + modifiedStatementString );
         
         String statementType = AppObjects.getIConfig().getValue(requestName + ".query_type","");
         // get a callable statement
         if (statementType.equals("update"))
         {
            if (statementString.indexOf('?') == -1)
            {
                return execUpdate(con,modifiedStatementString);
            }
               
            else
            {
               return execQuery(con,modifiedStatementString );
            }                               
         }
         else // case of simple query
         {
               return execQuery(con,modifiedStatementString );
         }
      }
      catch(com.ai.application.interfaces.ConfigException x)
      {
         throw new DBException("Config exception",x);
      }         
   }         
   
   private DBRSCollection3 execQuery( Connection con
                           , String statementString )
      throws java.sql.SQLException
   {
         CallableStatement stmt =null;
         ResultSet rs = null; 

         try
         {
               stmt = con.prepareCall("{" + statementString + "}");
               // there is a resultSetCursor
               stmt.registerOutParameter(1,OracleTypes.CURSOR);
               stmt.execute();
               rs = (ResultSet)stmt.getObject(1);
               return new DBRSCollection3(con,stmt,rs);
         }
         catch(java.sql.SQLException x)
         {
            AppObjects.log("db: closing statement and result set due to an exception ");
            AppObjects.log(x);
            try
            {
               if ( stmt != null) stmt.close();
               if (rs != null) rs.close();
            }
            catch(java.sql.SQLException y)
            {
               AppObjects.log(y);
            }   
            throw x;
         }
   }      
   
   private RequestExecutorResponse execUpdate( Connection con, 
                                       String statementString )
      throws java.sql.SQLException
   {
      CallableStatement stmt =null;
      try 
      {
               stmt = con.prepareCall("{" + statementString + "}");
               // No resultSetCursor
               stmt.execute();
               return new RequestExecutorResponse(true);
      }
      finally
      {
         if (stmt != null) stmt.close();
      }         
   }      
} 

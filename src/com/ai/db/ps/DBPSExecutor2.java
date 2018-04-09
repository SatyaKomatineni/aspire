/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.db.ps;

import com.ai.application.interfaces.*;
import com.ai.application.utils.*;

import java.io.InputStream;
import java.sql.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import com.ai.common.*;
import com.ai.data.DataException;
import com.ai.db.DBBaseJavaProcedure;
import com.ai.db.DBException;
import com.ai.db.DBRSCollection2;

// Please move this object to com.ai.common or db
// 
import com.ai.application.interfaces.RequestExecutorResponse;

/**
 * Uses DBRSCollection2 as the returned collection
 * Will replace DBRequestExecutor1 for future references
 */
public class DBPSExecutor2 extends DBBaseJavaProcedure
{
   public Object executeProcedure(Connection con
                                          ,boolean bConnectionCreator
                                           ,String requestName
                                           , Hashtable arguments )
         throws DBException, SQLException
   {
      try 
      {
      	 AppObjects.log("Arguments in to the statement are : " + arguments );
         String statementString
         	= AppObjects.getValue(requestName + ".stmt");
         
         String paramList = AppObjects.getValue(requestName + ".paramList");
         
         AppObjects.info(this,"statement to execute : " + statementString );
         AppObjects.info(this,"param list is : " + paramList );
         
         int questionMarksCount = this.getNumberOfParameters(paramList);
         AppObjects.trace(this,"Number of parameters:" + questionMarksCount);
         
         AppObjects.trace(this,"Get prepared statement");
         PreparedStatement ps = con.prepareStatement(statementString);
         
         //set parameters
         this.setParams(ps,arguments,paramList,questionMarksCount);
         
         //time to execute
         String statementType = AppObjects.getIConfig().getValue(requestName + ".query_type","");
         if (statementType.equals("update"))
         {
            AppObjects.trace(this,"Executing query for statement:" + statementString);
            return execUpdate(con, bConnectionCreator, ps);
         }
         else
         {
            AppObjects.trace(this,"Executing update for statement:" + statementString);
            return execQuery(con, bConnectionCreator, ps );
         }
         
      }
      catch(Exception x)
      {
         throw new DBException("Error:Executing PSExecutor2",x);
      }         
   }//eof-function         
   
   //************************************************************************
   //* Setting the paramters into the prepared statement
   //************************************************************************
   private void setParams(PreparedStatement ps
   			, Map inArgs
			, String strParamList
			, int questionMarksCount)
   throws DBException, SQLException
   {
   		//shape of the param string
   		//key,type,converter|key,type,converter...
   		AppObjects.trace(this,"Parsing the parameter string:" + strParamList);
   		List paramList = ParamSpec.parseString(strParamList);
   
   		//make sure the the parameters are validated
   		AppObjects.trace(this,"validate the param counts");
   		validateParamSizes(questionMarksCount,paramList.size());
   		
   		
   		AppObjects.trace(this,"Set parameters into the prepared statement");
   		Iterator itr = paramList.iterator();
   		int paramIndex = 0;
   		while(itr.hasNext())
   		{
   			ParamSpec paramSpec = (ParamSpec)itr.next();
   			this.setParam(ps,inArgs,paramSpec,paramIndex);
   			paramIndex++;
   		}
   		AppObjects.trace(this,"Completed setting the params");
   }
   
   private void validateParamSizes(int questionMarksCount, int paramCount)
   throws DBException
   {
		if (questionMarksCount != paramCount)
   		{
   			throw new DBException("Specified parameters (" 
   					+ paramCount
					+ ") don't match the number of ? ("
					+ questionMarksCount
					+ ") marks");
   		}
   }
   private void setParam(PreparedStatement ps, Map inArgs, ParamSpec paramSpec, int index)
   throws SQLException, DBException
   {
   		//make sure the key exists
		Object paramValue = inArgs.get(paramSpec.key);
   		if (paramValue == null)
   		{
   			throw new DBException("Specified key not found in the arguments:" + paramSpec.key);
   		}
   		
   		//check the param types and set the objects in the
   		//prepared statement
   		if (paramSpec.type == null)
   		{
   			AppObjects.trace(this,"No type specified for key:" + paramSpec.key);
   			ps.setObject(index,inArgs.get(paramSpec.key));
   			return;
   		}
   }
   //************************************************************************
   //* Meet of the work: executing prepared statements
   //************************************************************************
   DBRSCollection2 execQuery( Connection con, boolean bConnectionCreator
   			, PreparedStatement ps )
      throws java.sql.SQLException
   {
         ResultSet rs = null; 

         try
         {
            rs = ps.executeQuery();
            return new DBRSCollection2(con, bConnectionCreator, ps, rs);
         }
         catch(java.sql.SQLException x)
         {
            AppObjects.log("db: closing statement and result set due to an exception ");
            if ( ps != null) ps.close();
            if (rs != null) rs.close();
            throw x;
         }
   }      
   RequestExecutorResponse execUpdate( Connection con, boolean bConnectionCreator
   		, PreparedStatement ps )
      throws java.sql.SQLException
   {
      Statement stmt = null;
      try 
      {
         int numberOfRowsUpdated = ps.executeUpdate();
         AppObjects.trace(this,"Number of rows updated : " + numberOfRowsUpdated );
         return new RequestExecutorResponse(true);
      }
      finally
      {
         if (ps != null) ps.close();
      }         
   }      
   //************************************************************************
   //* Utility functions
   //************************************************************************
   private int getNumberOfParameters(String ps)
   {
   		int count=0;
   		StringCharacterIterator sci = new StringCharacterIterator(ps);
   		for(char c=sci.first();c != CharacterIterator.DONE; sci.next())
   		{
   			if (c == '?')
   			{
   				count++;
   			}
   		}
   		return count;
   }//eof-function
   
   //************************************************************************
   //* Setting parameters into Prepared statement
   //************************************************************************
   private void setNoType(PreparedStatement ps, Object paramValue, 
   						Map args, String key, int index)
   throws SQLException, DBException
   {
   		ps.setObject(index,paramValue);
   }//eof-function
   
   private void setBlob(PreparedStatement ps, Object paramValue, Map args, String key, int index)
   throws SQLException, DBException
   {
   		if (!(paramValue instanceof InputStream))
   		{
   			throw new DBException("The key is pointing to a non stream or non blob object:" + key);
   		}
   		InputStream is = (InputStream)paramValue;
   		
   		String lengthKey = key + "_length";
   		String lengthStr = (String)args.get(lengthKey);
   		if (lengthStr == null)
   		{
   			throw new DBException("length key for the stream is not found in the args:" + lengthKey);
   		}
   		int length = Integer.parseInt(lengthStr);
  		
   		ps.setBinaryStream(index,is,length);
   }//eof-function
   
   private void setUsingConverter(PreparedStatement ps, Object paramValue
   				,String paramType
				, Map args, String key, int index
				, String converterName)
   throws SQLException, DBException, DataException, RequestExecutionException
   {
   		ITypeConverter itc = (ITypeConverter)AppObjects.getObject(converterName,null);
   		Object cobject = itc.convert(paramValue,paramType);
   		ps.setObject(index,cobject);
   }
   
   private void setUsingGenericConverter(PreparedStatement ps, Object paramValue
			,String paramType
		, Map args, String key, int index
		, String converterName)
   throws SQLException, DBException, DataException, RequestExecutionException
   {
   		Object cobject = TypeConverterUtility.convert(paramValue,paramType);
   		ps.setObject(index,cobject);
   }
}//eof-class 

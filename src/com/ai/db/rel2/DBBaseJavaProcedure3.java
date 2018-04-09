/*
 * Created on Nov 25, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.ai.application.interfaces.AFactoryPart;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.AArgSubstitutor;
import com.ai.common.SQLArgSubstitutor;
import com.ai.common.Tokenizer;
import com.ai.data.IDataCollection;
import com.ai.db.DBException;
import com.ai.db.IConnectionManager;
import com.ai.servlets.AspireConstants;

public abstract class DBBaseJavaProcedure3 extends AFactoryPart
{
   public abstract Object executeProcedure(Connection con, 
		String requestName, 
		Hashtable arguments)
        throws DBException, SQLException;

   protected Object executeRequestForPart(String requestName, Map inArgs)
   throws RequestExecutionException
   {
   		try
		{
		   	IProcedureBaseExtender extender = getExtender(requestName); 
		   	return extender.executeRequestForPart(this,requestName,inArgs);
		}
   		catch(SQLException x)
		{
   			throw new RequestExecutionException("SQL exception from base java procedure extender",x);
		}
   }//eof-function
   
   private static AArgSubstitutor ms_argSubstitutor = new SQLArgSubstitutor();
   public AArgSubstitutor getArgSubstitutor() { return ms_argSubstitutor; }
   
   private IProcedureBaseExtender getExtender(String requestName)
   throws RequestExecutionException
   {
   		try
		{
   			IProcedureBaseExtender extender 
			= (IProcedureBaseExtender)AppObjects.getObject("IProcedureBaseExtender",null);
   			return extender;
		}
	   	catch(RequestExecutionException x)
		{
	   		AppObjects.trace(this,"No default extender specified. Using a default");
	   		return new TMBaseJavaProcedure();
		}
   		
   }
}//eof-class

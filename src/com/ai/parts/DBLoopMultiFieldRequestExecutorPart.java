/*
 * Name: DBLoopMultiFieldRequestExecutorPart
 * 
 * Goal
 * *****
 * 1. Follow the DBLoopRequestExecutor rules
 * 2. Call multiple requests based on a plural field name
 * 3. Allow for multiple fields in each request separated by commas
 * 4. ex: updateAnswersRequest?changedAnswers=2,2|1,2|44,4&userid=john.doe
 * 
 * Configuration
 * *************
 * individualRequestname (ex: upsertAnswerRequest)
 * pluralFieldname (ex: changedAnswers)
 * individualFieldnames(ex: questiond,answerid)
 * 
 * Structure of fields
 * ***********************
 * pluralfieldname=changedAnswers
 * (Ex Coming on the request: changedAnswers=2,2|1,2|44,4 )
 * individualFieldnames=questionid,answerid
 * individualRequestname=upsertAnswer
 * 
 * that results in
 * 
 * upsertAnswer(questionid=2,answerid=2)
 * upsertAnswer(questionid=1,answerid=2)
 * upsertAnswer(questionid=44,answerid=2)
 * 
 * Related classes/parts
 * *************************
 * 1. Based on DBLoopRequestExecutor
 * 2. Also based on CollectionWorkSplitterObjectPart
 * 
 * Instance Information
 * **********************
 * @See its base class DBProcedureObject
 * @see also its efficient cousin DBProcedure
 * Being a DBProcedureObject it can manage local variables
 * 
 */
package com.ai.parts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.RequestExecutorResponse;
import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;
import com.ai.db.DBException;

public class DBLoopMultiFieldRequestExecutorPart 
extends DBProcedureObject
{
	 private String individualRequestName; //mandatory
	 private String pluralFieldName; //mandatory
	 private String individualFieldNames; //mandatory
	 private Vector individualFieldNamesVector;

	 private void populateConfigurationArguments() throws DBException 
	 {
		 try
		 {
			 individualRequestName = this.readConfigArgument("individualRequestName"); 
			 pluralFieldName = this.readConfigArgument("pluralFieldName");
			 pluralFieldName = pluralFieldName.toLowerCase();
			 individualFieldNames = this.readConfigArgument("individualFieldNames");
			 individualFieldNamesVector = Tokenizer.tokenize(individualFieldNames,",");
		 }
		 catch(ConfigException x)
		 {
			 throw new DBException("Error: Reading configuration for this part", x);
		 }         
	 }
	@Override
	protected Object executeDBProcedure(String requestName, Hashtable arguments)
	throws DBException 
	{
		  AppObjects.info(this,"Executing Multirow insert request:%s",requestName);
		  this.populateConfigurationArguments();

	      // field spec if mentioned 
	      String pluralFieldValue = (String)arguments.get(pluralFieldName);
	      AppObjects.info(this, "Passed in rows are:%s", pluralFieldValue);
	      if (pluralFieldValue == null)
	      {
	         throw new DBException("Error: No value for the plural field name: " + pluralFieldName);
	      }
	      // plural field value found
	      Vector rowFieldVector = Tokenizer.tokenize(pluralFieldValue,"|");
	      AppObjects.info(this, "The number of rows are:%s",rowFieldVector.size());
	      
	      try
	      {
	         for(Enumeration e=rowFieldVector.elements();e.hasMoreElements();)
	         {
	        	//rowFieldValue will be like: v1,v2,v3 etc
	            String rowFieldValue = (String)e.nextElement();
	            if (isStringEscaped(rowFieldValue) == false)
	            {
	            	//No escape sequence
		            Vector columnValueVector = Tokenizer.tokenize(rowFieldValue,",");
		  	        AppObjects.info(this, "The number of column values are:%s", columnValueVector.size());
		  	        addColumnsToArgs(arguments,this.individualFieldNamesVector,columnValueVector);
	            }
	            else {
	            	//There are commas in commas
		            List<String> columnValueList = Tokenizer.tokenizeWithEscapeCharDecoded(rowFieldValue,',');
		  	        AppObjects.info(this, "There are escape characters. They are decoded now.");
		  	        AppObjects.info(this, "The number of column values are:%s", columnValueList.size());
		  	        AppObjects.info(this, "The decoded values are:%s", columnValueList);
		  	        addColumnsToArgsList(arguments,this.individualFieldNamesVector,columnValueList);
	            	
	            }
	            Object obj = AppObjects.getIFactory().getObject(individualRequestName,arguments);
	         }
	      }
	      catch(RequestExecutionException x)
	      {
	         throw new DBException("Error: Could not execute a request", x);
	      }         
	      return new RequestExecutorResponse(true);
		
	}//eof-function executeDBProcedure
	
	void addColumnsToArgs(Hashtable arguments, Vector colNamesVector, Vector colValuesVector)
	{
		//for each column name
		//lower case it
		//add it as an element in arguments along with its value
        for(int i=0;i<colNamesVector.size();i++)
        {
            String colName = (String)colNamesVector.elementAt(i);
        	String colValue = (String)colValuesVector.elementAt(i);
        	arguments.put(colName.toLowerCase(),colValue);
        }
	}//eof-function addColumnsToArgs
	
	void addColumnsToArgsList(Hashtable arguments, Vector colNamesVector, List<String> colValuesList)
	{
		//for each column name
		//lower case it
		//add it as an element in arguments along with its value
        for(int i=0;i<colNamesVector.size();i++)
        {
            String colName = (String)colNamesVector.elementAt(i);
        	String colValue = (String)colValuesList.get(i);
        	arguments.put(colName.toLowerCase(),colValue);
        }
	}//eof-function addColumnsToArgs
	
	private boolean isStringEscaped(String s)
	{
		if (s.indexOf('\\') == -1)
		{
			//it is not escaped
			return false;
		}
		return true;
	}
}//eof-class

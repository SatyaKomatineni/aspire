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
import com.ai.db.*;
import com.ai.data.*;
import com.ai.servlets.AspireConstants;       
/**
 * Substitutes parameters in to an sql statement.
 * DBPreTranslateArgsMultiRequestExecutor
 *
 * What does it do:
 * ****************
 * 1. Evaluates a series of db requests
 * 2. After evaluating each request it collects the key/value pairs from each request
 * 3. Updates the incoming hashtable with the key/value pairs
 * 4. Sends the updated hashtable and the connection to the next db request
 * 5. The final request could be an update request or a query request
 * 6. For a query request it returns the collection
 * 7. For the update request it returns the RequestExecutorResponse
 * 
 * Restrictions
 * *************
 * 1. All but the last statement needs to be a single row fetching select
 * 2. Intermediate statements could be updates but they won't affect the hash table
 *
 * Drawbacks
 * **********
 * Although it could execute single requests, it 
 * When is this applicable
 * ************************
 *
 * How is this different from DBRequestExecutor1
 * **********************************************
 *
 * 
 * @see                        DBRequestExecutor1
 * @see                        DBBaseJavaProcedure
 * @see                        DBMultiUpdateRequestExecutor
 * @version                    1.37, 26 Jun 1996
 */
public class DBLoopRequestExecutor extends DBBaseJavaProcedure
{

   public Object executeProcedure(Connection con, 
                                    boolean bConnectionCreator,
                                    String requestName, 
                                    Hashtable arguments )
         throws DBException
   {
      String fieldSpec = 
      AppObjects.getIConfig().getValue(requestName + ".fieldSpec",null);
      String individualRequest = 
      AppObjects.getIConfig().getValue(requestName + ".individualRequest",null);
      if (fieldSpec == null)     
      {
         throw new DBException("Error: Field spec required");
      }
      if (individualRequest == null)     
      {
         throw new DBException("Error: Individual request not specified");
      }
      // field spec if mentioned 
      Vector fieldNameVector = Tokenizer.tokenize(fieldSpec,"|");
      String pluralFieldName = (String)fieldNameVector.get(0);
      String singularFieldName = (String)fieldNameVector.get(1);
      String pluralFieldValue = (String)arguments.get(pluralFieldName);
      if (pluralFieldValue == null)
      {
         throw new DBException("Error: No value for the plural field name: " + pluralFieldName);
      }
      // plural field value found
      Vector singularFieldVector = Tokenizer.tokenize(pluralFieldValue,"|");
      try
      {
         for(Enumeration e=singularFieldVector.elements();e.hasMoreElements();)
         {
            String singularFieldValue = (String)e.nextElement();
            arguments.put(singularFieldName,singularFieldValue);
            arguments.put("aspire.reserved.jdbc_connection",con);
            Object obj = AppObjects.getIFactory().getObject(individualRequest,arguments);
         }
      }
      catch(RequestExecutionException x)
      {
         throw new DBException("Error: Could not execute a request", x);
      }         
      return new RequestExecutorResponse(true);
   }
}   

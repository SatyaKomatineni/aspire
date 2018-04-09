/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.filters;

import com.ai.application.interfaces.*;
import com.ai.data.IDataCollection;
import com.ai.data.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.common.*;

// import java.util.ST
/**
 * Splits an incoming single argument into multiple arguments.
 * Incoming single argument should be a comma separated list of values
 *
 * inputs: A Hashtable of input arguments
 * outputs: An IDataCollection of 1 row
 * Ex: 
 * input argument: ids
 * Value of input argument: ids=value1,value2,value3
 * Split specification in the properties file: 
 *
 *    request.name.args={ids}&c1|c2|c3
 * 
 * will split "ids" argument into 3 arguments named as "c1", "c2", & "c3"
 *
 *    c1=value1
 *    c2=value2
 *    c3=value3
 *
 * multiplicity: Singleton
 * No instance variables are allowerd
 */
public class SplitRequestExecutor implements ICreator
{
   private static SQLArgSubstitutor ms_argSubstitutor = new SQLArgSubstitutor();
   
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      try
      {

        Hashtable params=null;
        if (args instanceof Vector)
        {
            params = (Hashtable)(((Vector)args).elementAt(0));
        }
        else if (args instanceof Hashtable)
        {
            params = (Hashtable)args;
        }
        else
        {
            throw new RequestExecutionException("wrong argument type");
        }
        
            
        String argString = AppObjects.getIConfig().getValue(requestName + ".args", "v1,v2&a1|a2");
        AppObjects.trace(this,"Arguments in to the statement are : %1s",params );
        AppObjects.trace(this,"info.args = %1s",argString );         
        String modifiedStatementString = ms_argSubstitutor.substitute(argString
                                                            ,params);
         AppObjects.trace(this,"statement to execute : %1s", modifiedStatementString );
         
         // return an IDataCollection
         // valuestrng & key1,key2
         //
         Vector v1 = Tokenizer.tokenize(modifiedStatementString,"&");
         
         // value string would be separated by |
         String valueString = (String)v1.elementAt(0);
         Vector valueVector = new Vector();
         valueVector.addElement(valueString);
         
         // look for metadata (vector)
         String metaDataString = (String)v1.elementAt(1);
         Vector metaDataVector = Tokenizer.tokenize(metaDataString,",");
         
         return new VectorDataCollection(metaDataVector,valueVector);
      }
      catch(com.ai.data.InvalidVectorDataCollection x)
      {
         AppObjects.log(x);
         throw new RequestExecutionException("wrong split arguments",x);
      }
   }      
} 
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

// import java.util.ST
/**
 * A filter to assert that a data collection is emtpy
 * If the set is not empty throws an exception with the specified message
 *
 * Config format:
 *    request.name.classname=EmptySetValidator
 *    request.name.exception_message=this set should be empty
 *
 * inputs: IDataCollection
 * outputs: true if the set is empty
 *          exception if not
 *
 * multiplicity: Singleton
 * No instance variables are allowerd
 */
public class EmptySetValidator implements ICreator
{
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      // The argument is an IDataCollection
      if (!(args instanceof IDataCollection ))
      {
         AppObjects.error(this,"Wrong type of object passed in : %1s",args.getClass().getName() );
         AppObjects.error(this,"Expecting a class of type : com.ai.data.IDataCollection");
         throw new RequestExecutionException("Unexpected data type");
      }
      IDataCollection col = null;
      try
      {
         col = (IDataCollection)args;
         IIterator itr = col.getIIterator();
         itr.moveToFirst();
         if (itr.isAtTheEnd())
         {
            // empty data set
            return new Boolean(true);
         }
         //  not an empty data set
         String message = AppObjects.getIConfig().getValue(requestName + ".exception_message","Error:No message specified");
         throw new RequestExecutionException(message);         
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log(x);
         throw new RequestExecutionException("Data Exception",x);
      }         
      finally
      {
         try {col.closeCollection();}
         catch(DataException x){ AppObjects.log(x); }
      }
   }      
} 




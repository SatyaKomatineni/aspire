/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.filters;

import com.ai.application.interfaces.*;
import com.ai.common.UnexpectedTypeException;
import com.ai.data.*;
import java.util.*;
import com.ai.application.utils.*;

/**
 * A filter to convert a collection to a boolean result
 * If the set is empty decide to return true/false
 *
 * Config format:
 *    request.name.classname=EmptySetValidator
 *    request.name.onEmpty=[true|false:true]
 *
 * inputs: IDataCollection
 * outputs: true if the set is empty
 *          exception if not
 *
 * multiplicity: Singleton
 * No instance variables are allowerd
 * 
 * Based On
 * ********
 * NonEmptySetValidator
 * 
 * See
 * **********
 * com.ai.filter package
 * 
 */
public class CollectionToBooleanFilter implements ICreator
{
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      // The argument is an IDataCollection
      if (!(args instanceof IDataCollection ))
      {
         AppObjects.warn("Wrong type of object passed in : %1\n%2" 
        		 ,args.getClass().getName()
        		 ,"Expecting a class of type : com.ai.data.IDataCollection"
        		 );
         throw new RequestExecutionException(
        		 "Unexpected data type. Expecting a data collection");
      }
      IDataCollection col = null;
      try
      {
         boolean onEmpty = this.getOnEmpty(requestName);
         col = (IDataCollection)args;
         IIterator itr = col.getIIterator();
         itr.moveToFirst();
         if (!(itr.isAtTheEnd()))
         {
            // non empty data set
            // rows exist, it is OK
            return new Boolean(!onEmpty);
         }
         //  Collection is at the end
         //  No rows exist
         //  empty data set
         return new Boolean(onEmpty);
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log(x);
         throw new RequestExecutionException("Data Exception",x);
      }
      catch(com.ai.common.UnexpectedTypeException x)
      {
         AppObjects.log(x);
         throw new RequestExecutionException(
        		 "Trying to convert a boolean string to boolean",x);
      }
      finally
      {
         try {col.closeCollection();}
         catch(DataException x){ AppObjects.log(x); }
      }
   }
   boolean getOnEmpty(String requestName) 
   throws UnexpectedTypeException
   {
       String message = 
    	   AppObjects.getIConfig().getValue(requestName + ".onEmpty",
    			   "false");
       return FilterUtils.convertToBoolean(message);
   }
}//eof-class





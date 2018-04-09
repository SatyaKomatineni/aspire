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

/**
 * 5/29/2013
 * Converts a datacollection that has only 1 row into a string
 * inputs: IDataCollection
 * outputs: Hashtable
 * errors: On error a null value is returned
 * multiplicity: Singleton
 * No instance variables are allowerd
 * 
 * Uses a column name called "result"
 * 
 * Returns a null if there is an error
 */
public class SingleColumnToStringConverter implements ICreator
{
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      // The argument is an IDataCollection
      if (!(args instanceof IDataCollection ))
      {
         AppObjects.error(this,"Wrong type of object passed in : %1s",args.getClass().getName() );
         AppObjects.error(this,"Expecting a class of type : com.ai.data.IDataCollection");
         return null;
      }
      IDataCollection col = null;
      try
      {
         col = (IDataCollection)args;
         IIterator itr = col.getIIterator();
         itr.moveToFirst();
         if (itr.isAtTheEnd())
         {
            AppObjects.log("No rows retrieved. returning a null.");
            // No rows retrieved
            col.closeCollection();
            return null;
         }

         IDataRow datarow = (IDataRow)itr.getCurrentElement();
         String returnvalue = datarow.getValue("result");
         
         return returnvalue;
      }
      catch(Exception x)
      {
         AppObjects.error(this,"Error: Data exception",x);
         throw new RequestExecutionException("Data exception",x);
      }         
      finally      
      {
         try {col.closeCollection();}
         catch(DataException x){ AppObjects.log(x); }
      }
   }      
} 

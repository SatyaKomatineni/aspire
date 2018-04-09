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
 * Converts a datacollection that has only 1 row into a hashtable
 * inputs: IDataCollection
 * outputs: Hashtable
 * errors: On error a null value is returned
 * multiplicity: Singleton
 * No instance variables are allowerd
 */
public class SingleRowToHashtableConverter implements ICreator
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
            return new Hashtable();
         }
         // There is a row afterall
         Hashtable ht = new Hashtable();

         IDataRow datarow = (IDataRow)itr.getCurrentElement();
         
         // StringTokenizer tokenizer = new StringTokenizer((String)itr.getCurrentElement(),"|");
         IIterator mItr = col.getIMetaData().getIterator();
         for(mItr.moveToFirst();!mItr.isAtTheEnd();mItr.moveToNext())
         {
            String key = (String)mItr.getCurrentElement();
            AppObjects.trace(this, "Placing %1s into hash table", key);
            ht.put(key.toLowerCase(),datarow.getValue(key));
         }
         return ht;
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log("Error: Data exception",x);
         return null;
      }         
      catch(com.ai.data.FieldNameNotFoundException x)
      {
         AppObjects.log("Error: Fieldname not  found exception",x);
         return null;
      }         
      finally      
      {
         try {col.closeCollection();}
         catch(DataException x){ AppObjects.log(x); }
      }
   }      
} 

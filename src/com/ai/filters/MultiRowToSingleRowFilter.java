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
 * Converts a datacollection that has multiple rows into 1 row.
 * The resulting collection will have the same column names
 * But value for each column now is a concatenated set of previous rows
 *
 * inputs: IDataCollection
 * outputs: IDataCollection that has 1 row
 * errors: On error ????
 * multiplicity: Singleton
 * No instance variables are allowerd
 */
public class MultiRowToSingleRowFilter implements ICreator
{
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      // The argument is an IDataCollection
      if (!(args instanceof IDataCollection ))
      {
         AppObjects.error(this,"Wrong type of object passed in : %1s", args.getClass().getName() );
         AppObjects.error(this,"Expecting a class of type : com.ai.data.IDataCollection1");
         throw new RequestExecutionException("Wrong type of object passed in");
      }
      IDataCollection1 col = null;
      try
      {
         col = (IDataCollection1)args;
         IIterator itr = col.getIIterator();
         itr.moveToFirst();
         if (itr.isAtTheEnd())
         {
            AppObjects.info(this,"info: No rows retrieved. returning an empty collection.");
            // No rows retrieved
            col.closeCollection();
            return new EmptyDataCollection();
         }
         
         // Some rows available
         IMetaData colMetaData = col.getIMetaData();
         int numberOfColumns = colMetaData.getColumnCount();

         // Instantiate a vector to hold a concatenated list of each column
         Vector colValueVector = new Vector();
         for(int i=0;i<numberOfColumns;i++)
         {
            colValueVector.addElement(new StringBuffer());
         }

         // Walk through the rows and append each column to the above vector         
         IIterator rowItr = col.getIIterator();
         int rowId = 0;
         for(rowItr.moveToFirst();!rowItr.isAtTheEnd();rowItr.moveToNext())
         {
            // get the row
            IDataRow rowData = (IDataRow)rowItr.getCurrentElement();

            // for each column append
            for(int i=0;i<numberOfColumns;i++)
            {
               StringBuffer prevColValue = (StringBuffer)colValueVector.elementAt(i);
               if (rowId==0)
               {
                  prevColValue.append( (String)rowData.getValue(i));
               }
               else
               {
                  prevColValue.append(",");
                  prevColValue.append((String)rowData.getValue(i));
               }                  
            }
            rowId++;
         }

         // Vector collection requires a single row vector
         // where columns are separated by '|'
         StringBuffer concatenatedSingleRow = new StringBuffer();
         
         for(Enumeration e=colValueVector.elements();e.hasMoreElements();)
         {
            StringBuffer colValue = (StringBuffer)e.nextElement();
            concatenatedSingleRow.append(colValue + "|");
         }

         //Create a vector data collection and return
         Vector singleRowVector = new Vector();
         singleRowVector.addElement(concatenatedSingleRow.toString());

         AppObjects.info(this, "info: converted vector :%1s",singleRowVector.toString() );
         IIterator colNamesItr = colMetaData.getIterator();
         Vector colNamesVector = new Vector();
         for(colNamesItr.moveToFirst();!colNamesItr.isAtTheEnd();colNamesItr.moveToNext())
         {
            colNamesVector.addElement(colNamesItr.getCurrentElement());
         }
         return new VectorDataCollection(colNamesVector, singleRowVector);
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log(x);
         throw new RequestExecutionException("Data exception", x);
      }         
      catch(com.ai.data.InvalidVectorDataCollection x)
      {
         AppObjects.log(x);
         throw new RequestExecutionException("Invalid Vector collection", x);
      }         
      finally     
      {
         try {col.closeCollection();}
         catch(DataException x){ AppObjects.log(x); }
      }
   }      
} 

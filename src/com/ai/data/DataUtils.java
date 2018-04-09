package com.ai.data;

import java.util.Hashtable;
import java.util.Map;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.RequestExecutorResponse;
import com.ai.application.utils.AppObjects;
import com.ai.reflection.ReflectionException;
import com.ai.typefaces.TypeFaceFacility;

/**
 * @author Satya
 *
 */
public class DataUtils 
{

	public static void fillAMap(IDataRow inRow, Map map)
   	throws DataException
   {
   		try
		{
	   		IIterator colNames = inRow.getColumnNamesIterator();
	   		for(colNames.moveToFirst();
	   			!colNames.isAtTheEnd();
	   			colNames.moveToNext())
	   		{
	   			String colName = (String)colNames.getCurrentElement();
	   			String colValue = inRow.getValue(colName);
	   			map.put(colName.toLowerCase(),colValue);
	   		}
		}
   		catch(FieldNameNotFoundException x)
		{
   			throw new DataException("Error: field name not found",x);
		}
   }
	
   public static Object execRequestUsingDataRow(String requestName, IDataRow inRow, Map args)
   	throws DataException, RequestExecutionException
   {
   	  fillAMap(inRow, args);
   	  return AppObjects.getObject(requestName,args);
   }
   
   public static IDataCollection queryUsingDataRow(String requestName, IDataRow inRow, Map args)
  	throws DataException, RequestExecutionException
  {
  	  fillAMap(inRow, args);
  	  return (IDataCollection)AppObjects.getObject(requestName,args);
  }
  
   public static String toStringDataRow(IDataRow dataRow) throws DataException
   {
      StringBuffer row = new StringBuffer();
      IIterator itr = dataRow.getColumnNamesIterator();
      for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
      {
      	 String column = (String)itr.getCurrentElement();
      	 String value = dataRow.getValue(column,"Field not found");
         row.append(value + "|");
      }
      return row.toString();
   }
   public static void closeCollectionSilently(IDataCollection col)
   {
	   try
	   {
		   if (col == null)
		   {
			   AppObjects.warn("DataUtils", "The passed collection is null.");
			   return;
		   }
		   col.closeCollection();
	   }
	   catch(Throwable t)
	   {
		   AppObjects.log("Error:Closing collection",t);
	   }
   }
   //Developed in May 2014
   //Analyze a collection to see if it is empty
   public static boolean dataNotExists(String requestname, Map args)
   throws RequestExecutionException
   {
	   return !dataExists(requestname,args);
   }
   public static boolean dataExists(String requestname, Map args)
   throws RequestExecutionException
   {
	   Object rtnobject = AppObjects.getObject(requestname,args);
	   if (rtnobject instanceof Boolean)
	   {
		   return ((Boolean)rtnobject).booleanValue();
	   }
	   if (rtnobject instanceof RequestExecutorResponse)
	   {
		   return ((RequestExecutorResponse)rtnobject).getReturnCode();
	   }
	   if (!(rtnobject instanceof IDataCollection))
	   {
		   //wrong type
		   throw new RequestExecutionException("Wrong type returned for requestname:" + requestname);
	   }
	   
	   //This is an IDataCollection
	   IDataCollection col = (IDataCollection)rtnobject;
	   try 
	   {
		   IIterator colitr = col.getIIterator();
		   if (colitr.isAtTheEnd())
		   {
			   //it is an empty collection
			   //so data exists is false
			   return false;
		   }
		   //data exists
		   return true;
	   } 
	   catch(DataException e) 
	   {
		   throw new RequestExecutionException("Unexpeced data exception during data analysis of requestname:" + requestname,e);
	   }
	   finally
	   {
		   DataUtils.closeCollectionSilently(col);
	   }
	   
   }//eof-function
   public static Object getObjectFromSingleRow(Class objectClass, String requestname, Hashtable args) 
   throws RequestExecutionException, ReflectionException, DataException
   {
	   
	   Object rtnobject = AppObjects.getObject(requestname,args);
	   if (!(rtnobject instanceof IDataCollection))
	   {
		   //wrong type
		   throw new DataException("Wrong type returned for requestname:" + requestname);
	   }
	   
	   //This is an IDataCollection
	   IDataCollection col = (IDataCollection)rtnobject;
	   try 
	   {
		   IIterator colitr = col.getIIterator();
		   if (colitr.isAtTheEnd())
		   {
			   //it is an empty collection
			   //so data exists is false
			   AppObjects.warn("DataUtils","No rows available for request %s while expecting object:%s"
					   , requestname
					   , objectClass.getName());
			   return null;
		   }
		   //data exists
		   IDataRow datarow = (IDataRow)colitr.getCurrentElement();
		   Object rowobject = 
			   TypeFaceFacility.self().castTo(objectClass, new SimpleDataRowDictionary(datarow));
		   
		   return rowobject;
	   } 
	   finally
	   {
		   DataUtils.closeCollectionSilently(col);
	   }
   }
}//eof-class

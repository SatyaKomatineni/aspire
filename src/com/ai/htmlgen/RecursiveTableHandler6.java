package com.ai.htmlgen;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.data.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.*;

public class RecursiveTableHandler6 extends GenericTableHandler6 implements IInitializable
{
   private String m_requestName;
   private String m_getChildRequestName;
   private String m_childrenRequestName;
   private IDataCollection m_childrenDC;
   private Vector m_childrenNamesVector;

   public void initialize(String requestName)
   {
      AppObjects.trace(this,"RTH6 initialized with %1s",requestName);
      m_requestName=requestName;
      m_childrenRequestName = AppObjects.getValue(requestName + ".getChildrenNamesRequestName",null);
      m_getChildRequestName = AppObjects.getValue(requestName + ".getChildRequestName",null);
   }
   public RecursiveTableHandler6()
   {
   }

   public IIterator getChildNames()
       throws DataException
   {
      try
      {
         // return new VectorIterator(m_childLoopNames);
         if (m_childrenNamesVector != null) return new VectorIterator(m_childLoopNames);

         m_childrenNamesVector = new Vector();
         m_childrenDC = (IDataCollection)AppObjects.getObject(m_childrenRequestName,this.m_inputArguments);
         IIterator di = m_childrenDC.getIIterator();
         for(di.moveToFirst();!di.isAtTheEnd();di.moveToNext())
         {
            IDataRow row = (IDataRow)di.getCurrentElement();
            String name = row.getValue("name");
            m_childrenNamesVector.add(name);
         }
         return new VectorIterator(m_childrenNamesVector);
      }
      catch(RequestExecutionException x)
      {
         throw new DataException("Error:Could not get child names. check getChildrenNamesRequestName",x);
      }
      catch(FieldNameNotFoundException x)
      {
         throw new DataException("Error:Could not get field names. check to see if you have a field named 'name'",x);
      }
   }

   public ihds getChild(String childName)
       throws DataException
   {
       try
       {
           //Put the current row in a dictionary
           String requestName = m_getChildRequestName;
           String handlerName = m_getChildRequestName;
           ihds lparent = this;
           Hashtable lArgs = createChildRequestArgs(childName, this.m_inputArguments);
           return
           (ihds)GenericTableHandlerFactory.getControlHandler(requestName,handlerName,lparent,lArgs);
       }
       catch(RequestExecutionException x)
       {
           throw new DataException("Error: Could not construct an inner generic table handler",x);
       }
       catch(FieldNameNotFoundException x)
       {
          throw new DataException("Error: Field name issue",x);
       }
   }

   Hashtable createChildRequestArgs(String childName, Map arguments)
         throws DataException, FieldNameNotFoundException
   {
      //locate the row
      IIterator rowItr = this.m_childrenDC.getIIterator();
      IMetaData m = this.m_childrenDC.getIMetaData();
      IIterator colItr = m.getIterator();
      Hashtable args = new Hashtable();
      //Locate the row
      IDataRow row = null;
      for(rowItr.moveToFirst();!rowItr.isAtTheEnd();rowItr.moveToNext())
      {
         IDataRow curRow = (IDataRow)rowItr.getCurrentElement();
         String name = curRow.getValue("name");
         if (name.equalsIgnoreCase(childName))
         {
            row = curRow;
         }
      }
      //row found
      for(colItr.moveToFirst();!colItr.isAtTheEnd();colItr.moveToNext())
      {
         String colName = (String)colItr.getCurrentElement();
         String colValue = row.getValue(colName);
         args.put(colName.toLowerCase(),colValue);
      }
      return args;
   }

}
/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.data.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;

/**
 * Based on GenericTableHandler4
 *
 * Through IControlHandler3 implements the forward iteration scheme.
 * From GenericeTableHandler3 preserves the reuse of connection from the parent.
 * Also retrieves values from the parent.
 *
 */
public class RandomTableHandler5 implements ICreator
            , IControlHandler3
            , ISingleThreaded
            , ILoopRandomIterator
{
        IReportTotalsCalculator  m_reportTotalsCalculator;
        IFormHandler            m_parentFormHandler;
        String                  m_tableHandlerName;
        IDataCollection         m_dataCollection;
        IIterator               m_collectionIterator;
        IMetaData               m_metaData;
        IDataRow                  m_curRow;
        Vector                  m_dataRows;

        boolean                  m_bEliminateRows;
        int                     m_curTurn = 0; // Points to the first row if available

      boolean bFirstTimeGotoNextRow = true; // To give a counter to the gotoNextRow

        // required by the factory
        public RandomTableHandler5()
        {
        }
        // Don't call this for regular construction
        public RandomTableHandler5(String handlerName
                                    , IFormHandler parentFormHandler
                                    , IDataCollection dataCollection)
                                    throws com.ai.data.DataException
        {
            init(handlerName, parentFormHandler, dataCollection,false );
        }

//**********************************************************
//* Initialization
//**********************************************************
        private void init(String handlerName
                                    , IFormHandler parentFormHandler
                                    , IDataCollection dataCollection
                                    , boolean bEliminateLoop)
                                    throws com.ai.data.DataException
        {
           m_tableHandlerName = handlerName;
           m_parentFormHandler = parentFormHandler;
           m_dataCollection = dataCollection;
           m_bEliminateRows = bEliminateLoop;

            m_collectionIterator = m_dataCollection.getIIterator();
            m_metaData = m_dataCollection.getIMetaData();
            // Obtain the report totals calculator
            m_reportTotalsCalculator = getReportTotalsCalculator(handlerName);
            m_dataRows = getAllRows(m_collectionIterator);
        }

//**********************************************************
//* A call from Aspire
//**********************************************************
        // from ICreator
        public Object executeRequest(String requestName, Object inArgs)
                throws RequestExecutionException
        {
            try
            {
                // I am going to have two arguments
                // form name , type string
                // parent form pointer
                //[optional argument strings]

                Vector args = (Vector)inArgs;
                String handlerName = (String)(args.elementAt(0));
                IFormHandler parentFormHandler = (IFormHandler)(args.elementAt(1));

                Hashtable urlArguments = null;
                if (args.size() > 2)
                {
                  urlArguments = (Hashtable)args.elementAt(2);
                }

                Object obj = AppObjects.getIFactory().getObject(
                                handlerName + ".query_request"
                                ,urlArguments );

                String eliminateLoop = AppObjects.getIConfig().getValue(
                                handlerName + ".eliminateLoop"
                                ,"no");

                m_dataCollection = (IDataCollection)obj;
                init( handlerName
                     , parentFormHandler
                     , m_dataCollection
                     , com.ai.filters.FilterUtils.convertToBoolean(eliminateLoop));
                return this;
            }
            catch(com.ai.data.DataException x)
            {
               throw new com.ai.application.interfaces.RequestExecutionException("DataException",x);
            }
            catch(com.ai.common.UnexpectedTypeException x)
            {
               throw new com.ai.application.interfaces.RequestExecutionException("Wrong type passed in",x);
            }
        }

        // from IControlHandler
//**********************************************************
//* getValue(key,turn)
//* @deprecated use getValue(key)
//**********************************************************
        public String getValue(final String key, int turn)
        {
         return getValue(key);
        }
//**********************************************************
//* isDataAvailable()
//* returns true if available
//**********************************************************
       public boolean isDataAvailable()
       {
         return (m_dataRows.size() > 0 );
       }

//**********************************************************
//* eliminate loop
//* indicates users' intention that the presentation engine
//* should eliminate the corresponding loop
//**********************************************************
      public boolean eliminateLoop()
      {
         return this.m_bEliminateRows;
      }
//**********************************************************
//* getContinueFlag()
//* indicates that presentation engine should proceed reading rows
//**********************************************************
        public boolean getContinueFlag()
        {
            return true;
        }


//**********************************************************
//* gotoNextRow()
//* Move the data collection iterator to the next row
//* if not successfull indicate the end of the collection
//* Also set up the DataRow so that getValue() function will work.
//**********************************************************
   /**
    */
   public boolean gotoNextRow()
   {
      if (m_curTurn >= m_dataRows.size())
      {
         return false;
      }
      m_curRow = (IDataRow)m_dataRows.elementAt(m_curTurn++);
      return true;
   }
   public boolean isRowAvailable()
   {
      if ((m_curTurn-1 >= 0) && (m_curTurn-1 < m_dataRows.size()))
      {
         return true;
      }
      else
      {
         return false;
      }
   }
//**********************************************************
//* getValue given key
//**********************************************************
   public String getValue(final String key)
   {
      if (m_curRow == null)
      {
        return UserMessages.getMessage(UserMessages.NO_DATA_FOUND_KEY
                                       ,UserMessages.NO_DATA_FOUND_KEY_DEFAULT_VALUE);
       }
      try { return m_curRow.getValue(key); }
       catch(FieldNameNotFoundException x)
       {

               return getValueFromParent(key);
       }
   }
   private String getValueFromParent(final String key)
   {
      String value = null;
      if (this.m_parentFormHandler != null)
      {
         value = m_parentFormHandler.getValue(key);
      }
      if (value != null) return value;
      return "Field not found";
   }

//**********************************************************
//* Collection management related
//**********************************************************
   public void formProcessingComplete()
   {
      try
      {
         m_dataCollection.closeCollection();
      }
      catch( com.ai.data.DataException x)
      {
         AppObjects.log("Could not close the loop handler data collection");
         AppObjects.log(x);
      }
   }
   public int getNumberOfRowsRetrieved()
   {
      return m_dataRows.size();
   }
   public IDataCollection getDataCollection()
   {
      return this.m_dataCollection;
   }
        //**********************************************************
        //* Report specific functions
        //**********************************************************
        public String getAggregateValue(final String key)
        {
            if (m_reportTotalsCalculator != null)
            {
               return m_reportTotalsCalculator.getAggregateValue(key);
            }
            return null;
        }
        private void processCurRowForTotals(final IDataRow row)
        {
            if (m_reportTotalsCalculator != null)
            {
               m_reportTotalsCalculator.processRow(row);
            }
        }
        private IReportTotalsCalculator getReportTotalsCalculator(final String handlerName)
        {
            try
            {
            IFactory fact = AppObjects.getIFactory();
            Object obj =
            fact.getObject(handlerName + ".reportTotalsCalculator",null);
            return (IReportTotalsCalculator)obj;
            }
            catch(RequestExecutionException x)
            {
               AppObjects.log("info.htmlgen: Report totals calculator not found");
               return null;
            }
        }


//**********************************************************
//* moveToFirstRow()
//**********************************************************
   public boolean moveToFirstRow()
   {
      if (isDataAvailable() == false) return false;
      m_curTurn = 1;
      m_curRow = (IDataRow)m_dataRows.elementAt(0);
      return true;
   }
//**********************************************************
//* moveToRow()
//**********************************************************
   public boolean moveToRow(int rownum)
   {
      if (rownum >= m_dataRows.size()) return false;
      if (rownum < 0) return false;
      m_curTurn = rownum;
      m_curRow = (IDataRow)m_dataRows.elementAt(rownum);
      return true;
   }
//**********************************************************
//* moveToFirstRow()
//**********************************************************
   public int getNumberOfRows()
   {
      return m_dataRows.size();
   }
//**********************************************************
//* getAllRows()
//**********************************************************
   private Vector getAllRows(IIterator itr) throws DataException
   {
      Vector v = new Vector();
      for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
      {
         IDataRow m_curRow = (IDataRow)m_collectionIterator.getCurrentElement();
         processCurRowForTotals(m_curRow);
         v.addElement(m_curRow);
      }
      AppObjects.log("Info: Number of rows retrieved" +  v.size());
      return v;
   }// end_f
//**********************************************************
//* Implement the new ILoopForwardIteratorInterface
//**********************************************************
   public void moveToFirst()
             throws DataException
   {
           this.moveToFirstRow();
   }

   public void moveToNext()
             throws DataException
   {
       this.gotoNextRow();
   }

   public boolean isAtTheEnd()
                  throws DataException
   {
       if (m_curTurn >= m_dataRows.size())
       {
          return true;
       }
       else
       {
           return false;
       }
   }
}

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
 * Following GenericTableHandler4
 * Adds color support
 *
 * Enhanced with IColumnFilter
 * @see AspireColorFilter
 * @see IColumnFilter
 *
 * Retrieve a column filter
 * request.<handlername>.columnFilterName=filterName
 *
 * request.<filterName>.className=filterClass
 * ..additional args for the filter..
 *
 * An example:
 * request.Aspire.defaultColumnFilter.className=com.ai.htmlgen.AspireColorFilter
 * request.Aspire.defaultColumnFilter.oddColor=<your_color_string>[aspire_odd]
 * request.Aspire.defaultColumnFilter.evenColr=<your_color_string>[aspire_even]
 * request.Aspire.defaultColumnFilter.rows=2
 *
 *
 * Through IControlHandler3 implements the forward iteration scheme.
 * From GenericeTableHandler3 preserves the reuse of connection from the parent.
 * Also retrieves values from the parent.
 */
public class GenericTableHandler5 implements ICreator, IControlHandler3, ISingleThreaded
{
      // Following two variables are used to safeguard this handler
      // being called from transforms older than AITransform5
         boolean m_bMismatch = false;
         int m_safetyTurn = 0;

        // Regular variable
        IReportTotalsCalculator  m_reportTotalsCalculator;
        IFormHandler            m_parentFormHandler;
        String                  m_tableHandlerName;
        IDataCollection         m_dataCollection;
        IIterator               m_collectionIterator;
        IMetaData               m_metaData;
        IDataRow                  m_curRow;
        boolean                  m_bEliminateRows;
        int                     m_curTurn = -1;
      boolean bFirstTimeGotoNextRow = true; // To give a counter to the gotoNextRow

      // color filter support
      IColumnFilter m_columnFilter=null;

        // required by the factory
        // Don't call this for regular construction
        public GenericTableHandler5()
        {
        }
        public GenericTableHandler5(String handlerName
                                    , IFormHandler parentFormHandler
                                    , IDataCollection dataCollection)
                                    throws com.ai.data.DataException
        {
            init(handlerName, parentFormHandler, dataCollection,false );
        }

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

            // Obtain the column filter
            m_columnFilter = getColumnFilter(handlerName);

            gotoFirstRow();
        }

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
        public String getValue(final String key, int turn)
        {
         m_bMismatch = true;
         m_safetyTurn = turn;
         return getValue(key);
        }
       public boolean isDataAvailable()
       {
         try
         {
            return !m_collectionIterator.isAtTheEnd();
         }
         catch(com.ai.data.DataException x)
         {
            AppObjects.log("No data available");
            AppObjects.log(x);
            return false;
         }
       }

      public boolean eliminateLoop()
      {
         return this.m_bEliminateRows;
      }
        public boolean getContinueFlag()
        {
         if (this.m_bMismatch == true)
         {
            AppObjects.log("Error: Wrong type of transform used for this handler");
            return false;
         }
         else
         {
            return true;
         }
        }
//**********************************************************
//* Iterator related
//**********************************************************
        private boolean gotoFirstRow()
        {
            boolean bReply=false;
            try
            {
               m_collectionIterator.moveToFirst();
               if (m_collectionIterator.isAtTheEnd() == true)
               {
                  AppObjects.log("info: No rows found found in gth");
                  bReply = false;
                  return bReply;
               }
               // there is a row to process
               Object obj = m_collectionIterator.getCurrentElement();
               if (obj instanceof String)
               {
                  m_curRow = new DataRow(m_metaData
                                    ,(String)obj
                                    ,"|" );
               }
               else
               {
                  m_curRow = (IDataRow)obj;
               }
               m_columnFilter.setRow(m_curRow,0);
               m_curTurn = 1;
               bReply = true;
               return bReply;
            }
            catch(com.ai.data.DataException x)
            {
               AppObjects.log("Error: Error with data collection");
               AppObjects.log(x);
               bReply = false;
               return bReply;
            }
            finally
            {
               if (bReply == false)
               {
                  try {m_dataCollection.closeCollection();}
                  catch(com.ai.data.DataException y)
                  {
                     AppObjects.log("Error: Could not close collection");
                     AppObjects.log(y);
                  }
               }
            }
        }

   public boolean moveToFirstRow()
   {
      // This collection is already pointing to first row
      bFirstTimeGotoNextRow = false;
      if (isDataAvailable() == true) return true;
      return false;
   }
   /**
    * Move the data collection iterator to the next row
    * if not successfull indicate the end of the collection
    * Also set up the DataRow so that getValue() function will work.
    */
   public boolean gotoNextRow()
   {
      AppObjects.log("Trace:GT5/gotoNextRow Go to next row called");
      // If calling for the first time, the collection is already pointing to
      // the first row.  Simply return the data availability
      if (bFirstTimeGotoNextRow)
      {
         // Already pointing to the first row
         bFirstTimeGotoNextRow = false;
         processCurRowForTotals(m_curRow);
         return isDataAvailable();
      }
      // This is not the first time
      // I may be at the end

      if (isDataAvailable() == false)
      {
         AppObjects.log("info: Collection already at the end");
         return false;
      }

      // I am not at the end
      // If going to the next row is successful
      // increment the success

      boolean bReply=false;
      try
      {
         m_collectionIterator.moveToNext();
         if (m_collectionIterator.isAtTheEnd() == true)
         {
            AppObjects.log("info: No rows found found in gth");
            bReply = false;
            return bReply;
         }
         // there is a row to process
         Object obj = m_collectionIterator.getCurrentElement();
         if (obj instanceof String)
         {
            m_curRow = new DataRow(m_metaData
                              ,(String)obj
                              ,"|" );
         }
         else
         {
            m_curRow = (IDataRow)obj;
         }
         m_columnFilter.setRow(m_curRow,m_curTurn);
         m_curTurn++;
         bReply = true;
         return bReply;
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log("Error: Error with data collection");
         AppObjects.log(x);
         bReply = false;
         return bReply;
      }
      finally
      {
         if (bReply == false)
         {
            try {m_dataCollection.closeCollection();}
            catch(com.ai.data.DataException y)
            {
               AppObjects.log("Error: Could not close collection");
               AppObjects.log(y);
            }
         }
      }
   }
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

         // try from the color filter
         String value = m_columnFilter.getValue(key);
         if (value != null) return value;
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
      return m_curTurn - 1;
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

/**
 * Retrieve a column filter
 * request.<handlername>.columnFilterName=filterName
 *
 * request.<filterName>.className=filterClass
 * ..additional args for the filter..
 *
 * An example:
 * request.Aspire.defaultColumnFilter.className=com.ai.htmlgen.AspireColorFilter
 * request.Aspire.defaultColumnFilter.oddColor=<your_color_string>[aspire_odd]
 * request.Aspire.defaultColumnFilter.evenColr=<your_color_string>[aspire_even]
 * request.Aspire.defaultColumnFilter.rows=2
 *
 *
 */
        private IColumnFilter getColumnFilter(final String handlerName)
        {
            try
            {
            IConfig config = AppObjects.getIConfig();
            String filterName = config.getValue("request." + handlerName + ".columnFilterName","Aspire.defaultColumnFilter");

            // filter specified
            // Get it using the factory
            IFactory fact = AppObjects.getIFactory();
            Object obj =
            fact.getObject(filterName,null);
            return (IColumnFilter)obj;
            }
            catch(RequestExecutionException x)
            {
               AppObjects.log("warn.htmlgen: Column filter not found");
               return null;
            }
        }
/**
 * Implementing ILoopForwardIterator
 */
        public void moveToFirst()
                  throws DataException
        {
            moveToFirstRow();
        }

        public void moveToNext()
                  throws DataException
        {
            this.gotoNextRow();
        }
        public boolean isAtTheEnd()
                throws DataException
        {
            return m_collectionIterator.isAtTheEnd();
        }

}

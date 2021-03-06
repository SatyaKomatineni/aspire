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

public class GenericTableHandler implements ICreator, IControlHandler, ISingleThreaded
{
        IFormHandler            m_parentFormHandler;
        String                  m_tableHandlerName;
        IDataCollection         m_dataCollection;        
        IIterator               m_collectionIterator;
        IMetaData               m_metaData;
        IDataRow                  m_curRow;
        int                     m_curTurn = -1;
        
        public GenericTableHandler() 
        {
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
                m_tableHandlerName = (String)(args.elementAt(0));
                m_parentFormHandler = (IFormHandler)(args.elementAt(1));

                Hashtable urlArguments = null;
                if (args.size() > 2)
                {
                  urlArguments = (Hashtable)args.elementAt(2);
                }
                
                Object obj = AppObjects.getIFactory().getObject(
                                m_tableHandlerName + ".query_request"
                                ,urlArguments );
                                
                m_dataCollection = (IDataCollection)obj;   
                m_collectionIterator = m_dataCollection.getIIterator();                                            
                m_collectionIterator.moveToFirst();                
                m_metaData = m_dataCollection.getIMetaData();
                return this;
            }
            catch(com.ai.data.DataException x)
            {
               throw new com.ai.application.interfaces.RequestExecutionException("DataException",x);
            }
        }

        // from IControlHandler
        public String getValue(final String key, int turn)
        {
//               AppObjects.log("Looking for column : " + key  );
                try
                {
                   if (m_collectionIterator.isAtTheEnd() == true)
                   {
                      return "No data found";
                   }
                   if (turn > m_curTurn)
                   {
                        // switch the row to next one
                        Object obj  = m_collectionIterator.getCurrentElement();
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
                        m_curTurn = turn;                                 
                   }
                   return m_curRow.getValue(key);
                }
                catch(FieldNameNotFoundException x)
                {
                        return "Field not found";
                }                        
                catch(com.ai.data.DataException x)
                {
                        return "Data exception";
                }                        
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
         return false;
      }
        public boolean getContinueFlag()
        {
            try 
            {
                m_collectionIterator.moveToNext();
                if ( m_collectionIterator.isAtTheEnd() )
                {
                        return false;
                }
                return true;
            }
            catch(com.ai.data.DataException x)
            {
               throw new RuntimeException("Unable to handle");
            }                
        }
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
}     
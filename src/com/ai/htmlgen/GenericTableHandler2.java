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

public class GenericTableHandler2 implements ICreator, IControlHandler1, ISingleThreaded
{
        IFormHandler            m_parentFormHandler;
        String                  m_tableHandlerName;
        IDataCollection         m_dataCollection;        
        IIterator               m_collectionIterator;
        IMetaData               m_metaData;
        IDataRow                  m_curRow;
        boolean                  m_bEliminateRows;
        int                     m_curTurn = -1;
        
        
        // required by the factory
        // Don't call this for regular construction
        public GenericTableHandler2() 
        {
        }
        public GenericTableHandler2(String handlerName
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
            m_collectionIterator.moveToFirst();                
            m_metaData = m_dataCollection.getIMetaData();
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
         return this.m_bEliminateRows;
      }
        public boolean getContinueFlag()
        {
            boolean bReply = true;
            try 
            {
               if (m_collectionIterator.isAtTheEnd())
               {
                  bReply = false;
                  return bReply;
               }
               
                m_collectionIterator.moveToNext();
                if ( m_collectionIterator.isAtTheEnd() )
                {
                   bReply = false;
                   return bReply;
                }
                bReply = true;
                return bReply;
            }
            catch(com.ai.data.DataException x)
            {
               AppObjects.log("sql: getContinue of the table handler loop");
               AppObjects.log(x);
               bReply = false;
               return bReply;
            }                
            finally
            {
               if (bReply == false)
               {
                  try {m_dataCollection.closeCollection();}
                  catch(com.ai.data.DataException x)
                  {
                     AppObjects.log("error.db: Could not closeCollection");
                     AppObjects.log(x);
                  }
               }
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
   public int getNumberOfRowsRetrieved()
   {
      return m_curTurn - 1;
   }
   public IDataCollection getDataCollection()
   {
      return this.m_dataCollection;
   }
}     

package com.ai.db.events;
import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import com.ai.db.DBException;

import java.sql.Connection;
import java.util.*;


/**
 * @author Satya Komatineni Oct 17, 2005
 */
public class ConnectionEventDistributor implements IConnectionEvents, IInitializable
{
   private List m_eventHandlers = new ArrayList();
   public void initialize(String requestName)
   {
      String eventHandlers = AppObjects.getValue(requestName + ".eventHandlerList",null);
      if (eventHandlers != null)
      {
         Vector v = com.ai.common.Tokenizer.tokenize(eventHandlers,",");

         for (Enumeration e = v.elements();e.hasMoreElements();)
         {
            String eventHandler = (String)e.nextElement();
            try
            {
                IConnectionEvents ieh = (IConnectionEvents)AppObjects.getObject(eventHandler,null);
               m_eventHandlers.add(ieh);
            }
            catch(RequestExecutionException x)
            {
               AppObjects.log("Error: Could not obtain the requested event handler",x);
               continue;
            }
         }
      }
   }

   public boolean onCreateConnection(Connection con) throws DBException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
          IConnectionEvents ihe = (IConnectionEvents)itr.next();
            boolean rtncode = ihe.onCreateConnection(con);
            if (rtncode == false) return false;
      }
      return true;
   }
   public boolean onPreCloseConnection(Connection con) throws DBException
   {
       Iterator itr = m_eventHandlers.iterator();
       while(itr.hasNext())
       {
           IConnectionEvents ihe = (IConnectionEvents)itr.next();
             boolean rtncode = ihe.onPreCloseConnection(con);
             if (rtncode == false) return false;
       }
       return true;
   }
   public boolean onGetConnection(Connection con) throws DBException
   {
       Iterator itr = m_eventHandlers.iterator();
       while(itr.hasNext())
       {
           IConnectionEvents ihe = (IConnectionEvents)itr.next();
             boolean rtncode = ihe.onGetConnection(con);
             if (rtncode == false) return false;
       }
       return true;
   }
   public boolean onPutConnection(Connection con) throws DBException
   {
       
       Iterator itr = m_eventHandlers.iterator();
       while(itr.hasNext())
       {
           IConnectionEvents ihe = (IConnectionEvents)itr.next();
             boolean rtncode = ihe.onPutConnection(con);
             if (rtncode == false) return false;
       }
       return true;
   }
}//eof-class

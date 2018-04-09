package com.ai.servlets;

import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import javax.servlet.http.*;

import java.io.PrintWriter;
import java.util.*;

public class HttpEventDistributor implements IHttpEvents, ICreator
{
   private List m_eventHandlers = new ArrayList();
   public Object executeRequest(String requestName, Object args)
         throws RequestExecutionException
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
               IHttpEvents ieh = (IHttpEvents)AppObjects.getObject(eventHandler,null);
               m_eventHandlers.add(ieh);
            }
            catch(RequestExecutionException x)
            {
               AppObjects.log("Error: Could not obtain the requested event handler",x);
               continue;
            }
         }
      }
      return this;
   }

   public boolean applicationStart() throws AspireServletException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
            IHttpEvents ihe = (IHttpEvents)itr.next();
            boolean rtncode = ihe.applicationStart();
            if (rtncode == false) return false;
      }
      return true;
   }
   public boolean applicationStop() throws AspireServletException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
            IHttpEvents ihe = (IHttpEvents)itr.next();
            boolean rtncode = ihe.applicationStop();
            if (rtncode == false) return false;
      }
      return true;
   }
   public boolean sessionStart(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
            IHttpEvents ihe = (IHttpEvents)itr.next();
            boolean rtncode = ihe.sessionStart(session,request,response);
            if (rtncode == false) return false;
      }
      return true;
   }
   public boolean sessionStop() throws AspireServletException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
            IHttpEvents ihe = (IHttpEvents)itr.next();
            boolean rtncode = ihe.sessionStop();
            if (rtncode == false) return false;
      }
      return true;
   }
   public boolean beginRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
            IHttpEvents ihe = (IHttpEvents)itr.next();
            boolean rtncode = ihe.beginRequest(request,response);
            if (rtncode == false) return false;
      }
      return true;
   }
   public boolean endRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
            IHttpEvents ihe = (IHttpEvents)itr.next();
            boolean rtncode = ihe.endRequest(request,response);
            if (rtncode == false) return false;
      }
      return true;
   }
   public boolean userLogin(String username,  HttpSession session, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
            IHttpEvents ihe = (IHttpEvents)itr.next();
            boolean rtncode = ihe.userLogin(username,session,request,response);
            if (rtncode == false) return false;
      }
      return true;
   }
   public boolean userChange(String oldUser, String newUser,  HttpSession session, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
            IHttpEvents ihe = (IHttpEvents)itr.next();
            boolean rtncode = ihe.userChange(oldUser,newUser,session,request,response);
            if (rtncode == false) return false;
      }
      return true;
   }
   //added 2014
   //added 2014
   public boolean beginAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
           ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      Iterator itr = m_eventHandlers.iterator();
      while(itr.hasNext())
      {
            IHttpEvents ihe = (IHttpEvents)itr.next();
            boolean rtncode = ihe.beginAspireRequest(coreuser, session,uri,query,parameters
                    ,out, request, response);
            if (rtncode == false) return false;
      }
      return true;
   }

   public boolean endAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
     ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException   
   {
         Iterator itr = m_eventHandlers.iterator();
         while(itr.hasNext())
         {
               IHttpEvents ihe = (IHttpEvents)itr.next();
               boolean rtncode = ihe.endAspireRequest(coreuser, session,uri,query,parameters
                       ,out, request, response);
               if (rtncode == false) return false;
         }
         return true;   
   }//eof-function   
}//eof-class

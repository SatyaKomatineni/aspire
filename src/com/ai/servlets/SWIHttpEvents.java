package com.ai.servlets;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;

import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import javax.servlet.http.*;

public class SWIHttpEvents
{
   public static IHttpEvents m_events = null;

   static
   {
      try
      {
         m_events = (IHttpEvents)AppObjects.getObject(IHttpEvents.NAME,null);
      }
      catch(RequestExecutionException x)
      {
         m_events=null;
         AppObjects.log("Warn: No http events class available. No events will be reported");
      }
   }

   static public boolean applicationStart() throws AspireServletException
   {
      if (m_events == null) return true;
      return m_events.applicationStart();
   }

   static public boolean applicationStop() throws AspireServletException
   {
      if (m_events == null) return true;
      return m_events.applicationStop();
   }

   static public boolean sessionStart(HttpSession session,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws AspireServletException
   {
      if (m_events == null) return true;
      return m_events.sessionStart(session,request,response);
   }

   static public boolean sessionStop() throws AspireServletException
   {
      if (m_events == null) return true;
      return m_events.sessionStop();
   }

   static public boolean beginRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      if (m_events == null) return true;
      return m_events.beginRequest(request,response);
   }
   static public boolean endRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      if (m_events == null) return true;
      return m_events.endRequest(request,response);
   }
   static public boolean userLogin(String username,
                                HttpSession session,
                                HttpServletRequest request,
                                HttpServletResponse response) throws AspireServletException
   {
      if (m_events == null) return true;
      return m_events.userLogin(username,session,request,response);
   }
   static public boolean userChange(String oldUser,
                                 String newUser,
                                 HttpSession session,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws AspireServletException
   {
      if (m_events == null) return true;
      return m_events.userChange(oldUser,newUser,session,request,response);
   }
   //added 2014
   static public boolean beginAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
           ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      if (m_events == null) return true;
      return m_events.beginAspireRequest(coreuser, session,uri,query,parameters,out,request,response);
   }

   static public boolean endAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
     ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException   
   {
      if (m_events == null) return true;
      return m_events.endAspireRequest(coreuser, session,uri,query,parameters,out,request,response);
   }//eof-function
}//eof-class


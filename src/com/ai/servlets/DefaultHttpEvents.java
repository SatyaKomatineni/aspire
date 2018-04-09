package com.ai.servlets;

import java.io.PrintWriter;
import java.util.Hashtable;

import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import javax.servlet.http.*;

public class DefaultHttpEvents implements IHttpEvents, ICreator
{
   protected String m_requestName = null;
   public Object executeRequest(String requestName, Object args)
         throws RequestExecutionException
   {
      m_requestName = requestName;
      return this;
   }

   public boolean applicationStart() throws AspireServletException
   {
      AppObjects.log("Info:Application start event");
      return true;
   }
   public boolean applicationStop() throws AspireServletException
   {
      AppObjects.log("Info:Application stop event");
      return true;
   }
   public boolean sessionStart(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      AppObjects.log("Info:session start event");
      return true;
   }
   public boolean sessionStop() throws AspireServletException
   {
      AppObjects.log("Info:session stop event");
      return true;
   }
   public boolean beginRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      AppObjects.log("Info:request begin event");
      return true;
   }
   public boolean endRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      AppObjects.log("Info:request end event");
      return true;
   }
   public boolean userLogin(String username,  HttpSession session, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      AppObjects.log("Info:user loggedin event");
      return true;
   }
   public boolean userChange(String oldUser, String newUser,  HttpSession session, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      AppObjects.log("Info:user change event");
      return true;
   }
   //added 2014
   public boolean beginAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
           ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
	      AppObjects.info(this,"Begin aspire request with propert args");
	      return true;	   
   }

   public boolean endAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
     ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException   
   {
	      AppObjects.info(this,"end aspire request with proper args");
	      return true;	   
   }
}
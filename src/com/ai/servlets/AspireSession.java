/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import java.util.*;
import javax.servlet.http.*;

import com.ai.servlets.compatibility.ServletCompatibility;

/**
 * This is a very very old implementation. Not sure how I am using it.
 * It is used only in PageDispatcherServlet
 * That class is no longer used.
 * 
 * Instead the class to be used is PageDispatcherServlet1
 * 
 * This is deprecated as of 2013.
 * 
 * Use AspireSession1 instead!
 *
 */
public class AspireSession 
{
   public static String ASPIRE_SESSION_NAME =     "AspireSession";
   public static String SENT_PAGE_REGISTRY_NAME = "SentPageResistry";
   private Hashtable m_parms = new Hashtable();
   private Hashtable m_curRequestParms = null;
   private long m_curTimeStamp;

   /**
    * Constructor
    */
   public AspireSession() 
   {
      // create a sentPageRegistry
      m_parms.put(SENT_PAGE_REGISTRY_NAME, new Hashtable());
   }

   public Hashtable getCurRequestParms()
   {
      return m_curRequestParms;
   }
   public void setCurRequestParms(Hashtable requestParms)
   {
      m_curRequestParms = requestParms;
   }
   
   public Hashtable getSentPageRegistry()
   {
      return (Hashtable)m_parms.get(SENT_PAGE_REGISTRY_NAME);
   }
   
   static public AspireSession
      getAspireSessionFromHttpSession(HttpSession session, boolean bCreate)
   {
      Object obj = ServletCompatibility.getSessionValue(session,AspireSession.ASPIRE_SESSION_NAME);
      if (bCreate == false)
      {
         return (AspireSession)obj;
      }      
      // create one and add it and return it
      obj = new AspireSession();
	  ServletCompatibility.putSessionValue(session,AspireSession.ASPIRE_SESSION_NAME,obj);
      return (AspireSession)obj;
   }

   public long getLastModifiedTimeForThisURL(boolean bCreate)
   {
      if (bCreate == true)
      {
         m_curTimeStamp = (System.currentTimeMillis()/1000) * 1000;
         return m_curTimeStamp;
      }
      else
      {
         return m_curTimeStamp;
      }
   }
} 

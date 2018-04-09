/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.testservlets;
import javax.servlet.http.*;
import javax.servlet.*;

public class SentPage
{
   private String m_url;
   private long m_time;
   
   public String getUrl(){ return m_url; }
   public void  setUrl(String url ){ m_url=url; }

   public long getTime(){ return m_time; }
   public void setTime(long time){ m_time = time; }

   public SentPage(String url, long time)
   {
      m_url = url;
      m_time = time;
   }
   /**
    * Determine if this page would like to be in Cache or not
    */
   public boolean doYouWantToBeInCache(HttpServletRequest inRequest)
   {
      String prevPageURL = inRequest.getParameter("previous_page_url");
      if (prevPageURL == m_url)
      {
         return true;
      }
      return false;
   }
}


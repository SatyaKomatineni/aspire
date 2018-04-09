package com.ai.aspire.context;
import javax.servlet.http.HttpSession;

public class AspireContext 
{
   private HttpSession m_session;
   public AspireContext(HttpSession session) 
   {
      m_session = session;
   }
   public HttpSession getSession()
   {
      return m_session;
   }
} 
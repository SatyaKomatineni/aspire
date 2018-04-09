package com.ai.aspire.authentication;

import javax.servlet.http.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import com.ai.servlets.AspireConstants;
import java.util.*;
import com.ai.common.*;
import com.ai.servletutils.*;

public class PublicAccessAuthentication extends DefaultAuthentication implements IAuthentication2, IInitializable
{
   private String m_publicURLs = null;
   private String m_publicRequestNames = null;

   private Map m_publicURLMap;
   private Map m_publicRNMap;

   public PublicAccessAuthentication()
   {
   }

   public void initialize(String requestName)
   {
     m_publicURLs = AppObjects.getValue(requestName + ".publicURLs","");
     m_publicRequestNames = AppObjects.getValue(requestName + ".publicRequestNames","");
     m_publicURLMap = Tokenizer.tokenizeAsAHashtable(m_publicURLs.toLowerCase(),",");
     m_publicRNMap = Tokenizer.tokenizeAsAHashtable(m_publicRequestNames.toLowerCase(),",");
     AppObjects.info(this,"Public URLs:%1s", m_publicURLs);
  }

  public boolean isAPublicURL(HttpServletRequest request
                                 ,HttpServletResponse response)
     throws AuthorizationException
  {
   /*
   *
   * By default things are private
   */
   String aspireURL= request.getParameter("url");
   AppObjects.info(this,"aspireURL: %1s", aspireURL);
   String aspireRequestName = request.getParameter("request_name");

   if (aspireURL != null)
   {
     if (m_publicURLMap.get(aspireURL.toLowerCase()) != null)
     {
       return true;
     }
   }

   if (aspireRequestName != null)
   {
     if (m_publicRNMap.get(aspireRequestName.toLowerCase()) != null)
     {
       return true;
     }
   }

   return false;
  }
}
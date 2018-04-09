package com.ai.servlets;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.util.*;

import javax.servlet.http.*;
import com.ai.common.*;

public class DSetHeaders implements ISetHeaders, ICreator {

   /**
    * interface from aspire and implemented by the client
    *
    * args is null for this all the time
    */
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
    {
      return this;
    }

   // from ISetHeaders    
      public void setHeaders(String displayUrl
            , HttpServletRequest request
            , HttpServletResponse response)
            throws AspireServletException
      {
         AppObjects.info(this,"Setting headers for Aspire URL: %1s",displayUrl);
         // Set expiration so that the document is always current
         response.setDateHeader("Expires",0);
      
         // For backward compatibility set content type
         String contentType = AppObjects.getIConfig().getValue(displayUrl + ".contentType",null);
         if (contentType != null)
         {
            response.setContentType(contentType);
         }
         else
         {
            response.setContentType("text/html");
         }
         //set any additional headers
         String headersString = getHeadersStringFromURL(displayUrl);
         if (headersString == null)
         {
            return;
         }
         // A valid header specification exists
         Vector headers = Tokenizer.tokenize(headersString,"|");
         for(Enumeration e=headers.elements();e.hasMoreElements();)
         {
            String singleHeaderString = (String)e.nextElement();
            Vector headerNameValuePair = StringUtils.splitAtFirst(singleHeaderString,'=');
            response.setHeader((String)headerNameValuePair.elementAt(0)
                     ,(String)headerNameValuePair.elementAt(1));
         }
         return;
      }            
   private String getHeadersStringFromURL(String url)
   {
      String urlResponseHeaders = AppObjects.getIConfig().getValue(url + ".headers",null);
      String globalResponseHeaders = AppObjects.getIConfig().getValue(AspireConstants.RESPONSE_HEADERS,null);
      if (globalResponseHeaders == null)
      {
         // global response headers not available
         return urlResponseHeaders;
      }
      // global response headers not null and available
      if (urlResponseHeaders == null)
      {
         // no url headers
         // just return the global ones
         return globalResponseHeaders;
      }
      // both are available
      // append them and return: global first and urlnext
      return globalResponseHeaders.trim() + "|" + urlResponseHeaders.trim();
   }
} 

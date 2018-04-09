package com.ai.servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import com.ai.common.*;
import com.ai.application.utils.*;
import com.ai.application.interfaces.*;

/**
 * Responsible for setting the headers in a http response
 * Global response header specification
 *    aspire.servletSupport.responseHeaders=Content-Type=text/html; charst=UTF-8|nextone
 *
 * url response headers
 *    testURL=tempplatefile
 *    testUrl.headers=Content-Type=text/html; charst=UTF-8|nextone
 *
 * Global headers applied first and then local next
 *
 */
public class SetHeaders
{
   /**
    * @deprecated
    */
   public static void setHeaders(String displayUrl, HttpServletResponse response)
   {
      AppObjects.info("SetHeaders.java","Setting headers for Aspire URL: %1s",displayUrl);
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
      String headersString = SetHeaders.getHeadersStringFromURL(displayUrl);
      AppObjects.info("SetHeaders.java","setting headers: %1s",headersString);
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
   private static String getHeadersStringFromURL(String url)
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
   /**
    * Uses dynamic settings for setting headers
    * Allows you to use a pluggable java class as well
    *
    */
   public static void setHeaders1(String displayUrl
         , HttpServletRequest request
         , HttpServletResponse response)
         throws AspireServletException
   {
      AppObjects.info("SetHeaders.java","Setting headers (1) for Aspire URL: %1s",displayUrl);
      // If there is a java class call it
      // other wise use the old setHeaders method
      String setHeadersClass = AppObjects.getIConfig().getValue("request."
                                       + AspireConstants.RESPONSE_HEADERS_CLASS
                                       + ".className", null);
      if (setHeadersClass == null)
      {
          AppObjects.log("Info: Setting headers using local code");
         setHeaders(displayUrl,response);
      }
      else
      {
         try
         {
            AppObjects.log("Info: Setting headers using a delegate class");
            ISetHeaders setHeaders = (ISetHeaders)AppObjects
                  .getIFactory()
                  .getObject(AspireConstants.RESPONSE_HEADERS_CLASS,null);
            setHeaders.setHeaders(displayUrl,request,response);
         }
         catch(RequestExecutionException x)
         {
            throw new AspireServletException("Error: Could not set headers due to a plugin error",x);
         }
      }
   }
}

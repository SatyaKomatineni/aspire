/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servletutils;
import javax.servlet.http.*;
import javax.servlet.*;
import com.ai.application.utils.*;

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import com.ai.common.Tokenizer;
import com.ai.servlets.*;
import com.ai.aspire.authentication.*;
import com.ai.common.*;
import java.util.HashMap;
import java.util.Map;
public class ServletUtils
{
	static public void redirectUserToFile( HttpServletRequest request
							,HttpServletResponse	response
							,String fileIdentifier )
		throws com.ai.application.interfaces.ConfigException
			   ,java.io.IOException
  	{
		String htmlFilename  = AppObjects.getIConfig().getValue(fileIdentifier);
		response.sendRedirect(response.encodeRedirectUrl(htmlFilename));
	}

	/**
    * @deprecated Move this function to FileUtils
    */
	static private String translateFileIdentifier( String fileIdentifier )
		throws com.ai.application.interfaces.ConfigException
	{
		String relativeHtmlFilename  = AppObjects.getIConfig().getValue(fileIdentifier);
      String specifiedFileSeparator = AppObjects.getIConfig().getValue("directories.file_separator","\\");
      if (specifiedFileSeparator.equals(File.separator) == false)
      {
         relativeHtmlFilename = relativeHtmlFilename.replace(specifiedFileSeparator.charAt(0)
                                             ,File.separatorChar);
      }
      File relativeHtmlFile = new File(relativeHtmlFilename );
      if (relativeHtmlFile.exists())
      {
         return relativeHtmlFilename;
      }
      // Do the substitution
      StringTokenizer tokenizer = new StringTokenizer(relativeHtmlFilename,":");
      if (tokenizer.countTokens() < 2 )
      {
         return relativeHtmlFilename;
      }

      // There certainly are two tokens with : separator
      // first part is the root path
      // second part is the relative path
      String rootSpec = tokenizer.nextToken();
      String rootSpecTranslated = AppObjects.getIConfig().getValue("directories." + rootSpec ,null);
      return rootSpecTranslated + tokenizer.nextToken();
	}
  static public String quote( String inStr )
  {
      return "\"" + inStr + "\"";
  }
  static public void appendHtmlHeader(StringBuffer inBuf)
  {
   inBuf.append("<html> \n<head>\n</head>\n<body>\n");
  }
  static public void appendHtmlTail(StringBuffer inBuf)
  {
   inBuf.append("\n</body>\n</html>");
  }
  static public void appendJSHeader(StringBuffer inBuf)
  {
      inBuf.append("\n<script LANGUAGE=\"JavaScript\">");
      inBuf.append("\n");
      inBuf.append("<!-- html comment (first line is ignored by js)");
      inBuf.append("\n");
  }
  static public void appendJSTail(StringBuffer inBuf)
  {
      inBuf.append("\n//-->");
      inBuf.append("\n</script>\n");
  }
  static public void writeOutAsJS(PrintWriter out, StringBuffer inBuf )
  {
   StringBuffer buf = new StringBuffer();
   appendHtmlHeader(buf);
   appendJSHeader(buf);
   out.println(buf.toString());
   appendJSTail(inBuf);
   out.println(inBuf.toString());
  }

  /**
   * Converts a string with new lines to html with <p> tags to
   * get the new line effect in an html document.
   */
  static public String convertToHtmlLines(final String inString)
  {
     if (inString == null)
        return inString;
     if (inString.equals(""))
     {
         return inString;
     }
     Vector v = Tokenizer.tokenize(inString, "\n" );
     StringBuffer buf = new StringBuffer("<p>");
     for (Enumeration e=v.elements();e.hasMoreElements();)
     {
         String thisToken = (String)e.nextElement();
         buf.append(thisToken);
         buf.append("</p><p>");
     }
     buf.append("</p>");
     return buf.toString();
  }
   static public Hashtable parseQueryString(final String httpQueryString )
   {
      // return a hash table of key value pairs for the query string
      // employs HttpUtils.parseQueryString().
      //
      // converts a hashtable of arrays returned by HttpUtils.querstring
      // to a hashtable of strings

      Hashtable rt = new Hashtable();
      if (httpQueryString == null) return rt;

      try
      {
         Hashtable t = HttpUtils.parseQueryString(httpQueryString);
         for (Enumeration e = t.keys(); e.hasMoreElements();)
         {
            Object key = e.nextElement();
            Object value = t.get(key);
            // This could be a string or an array of strings
            if (value instanceof String[])
            {
               String valueArray[] = (String[])value;
               rt.put(key,valueArray[0]);
            }
            else if (key instanceof String)
            {
               rt.put(key,value);
            }
            else
            {
               rt.put(key,"");
            }
         }
      }
      catch (IllegalArgumentException  x)
      {
         AppObjects.log(x);
         rt.put(httpQueryString,"");
      }
      return rt;
   }
   static public void main(String[] args)
   {
	   testParseQueryString();
   }
   
   static public void testParseQueryString()
   {
	      System.out.println("Testing parseQuery string");
//	      Hashtable t = HttpUtils.parseQueryString("TEST");
//	      Hashtable t = HttpUtils.parseQueryString("TEST&Test1");
//	    results in illegal argument exception
	      Hashtable t = ServletUtils.parseQueryString("TEST");
   }
   static public Hashtable convertToLowerCase(Hashtable inHashtable)
   {
      Hashtable table = new Hashtable();
      for(Enumeration e=inHashtable.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         table.put(key.toLowerCase(),inHashtable.get(key));
      }
      return table;
   }
   
   static public Hashtable getParameters(HttpServletRequest inRequest )
   {
      Hashtable  parms = new Hashtable();
      for (Enumeration e=inRequest.getParameterNames();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         String value = inRequest.getParameter(key);
         if (SecureVariables.isASecureVariable(key))
         {
         	AppObjects.error("ServletUtils", 
         			"Someone is passing a secure variable:" + key + ":" + value );
         	continue;
         }
         parms.put(key,value);
      }
      String aspireContext = inRequest.getContextPath();
      if (aspireContext != null)
      {
      	AppObjects.trace("ServletUtils","Retrived appcontex:" + aspireContext);
      	if (aspireContext.equals(""))
      	{
      		parms.put("aspirecontext","");
      	}
      	else if (aspireContext.length() == 1)
      	{
      		parms.put("aspirecontext","");
      	}
      	else
      	{
      		parms.put("aspirecontext",aspireContext.substring(1));
      	}
      }
      else
      {
         AppObjects.log("Warn:Unexpected result. context path not in the request");
      }
      //place
      fillParamsWithRedirectParams(parms,inRequest);
      return parms;
   }
   private static void fillParamsWithRedirectParams(Hashtable params, HttpServletRequest request)
   {
	   Map redirectParams = (Map)request.getAttribute("redirectParams");
	   if (redirectParams == null)
	   {
		   //there are no redirect params
		   return;
	   }
	   AppObjects.trace("ServletUtils","Redirect Params found are:%1s", redirectParams.toString());
	   params.putAll(redirectParams);
   }

   // Authentication utilities
   static public boolean isAPublicURL(HttpServletRequest request
                        , HttpServletResponse response )
      throws com.ai.aspire.authentication.AuthorizationException
   {
      try
      {
         IAuthentication authObject =
         (IAuthentication)(AppObjects.getIFactory().getObject(AspireConstants.AUTHENTICATION_OBJECT,null));
         if (authObject instanceof IAuthentication2)
         {
          return ((IAuthentication2)authObject).isAPublicURL(request,response);
         }
         else
         {
          return false;
         }
      }
      catch(com.ai.application.interfaces.RequestExecutionException x)
      {
         throw new AuthorizationException("auth: Could not locate and authroization service provider",x);
      }
   }
   static public String getUser(HttpServletRequest request)
   {
      HttpSession session = request.getSession(false);
      if (session == null)
      {
         AppObjects.log("Error:User: Session doesn't exist. Returing annonymous as user");
         return AspireConstants.ASPIRE_ANNONYMOUS_USER_NAME;
      }
      //Session is there
      String username = (String)session.getAttribute(AspireConstants.ASPIRE_USER_NAME_KEY);
      if (username != null) return username;

      //username is null
       AppObjects.log("Error: User name not set in session. Using annonymous user name");
       return AspireConstants.ASPIRE_ANNONYMOUS_USER_NAME;
   }

   static public boolean verifyPassword(final String userid, final String passwd )
      throws com.ai.aspire.authentication.AuthorizationException
   {
      try
      {
         IAuthentication authObject =
         (IAuthentication)(AppObjects.getIFactory().getObject(AspireConstants.AUTHENTICATION_OBJECT,null));
         return authObject.verifyPassword(userid,passwd);
      }
      catch(com.ai.application.interfaces.RequestExecutionException x)
      {
         throw new AuthorizationException("auth: Could not locate and authroization service provider",x);
      }
   }
   static public boolean isAccessAllowed(final String userid
                        , HttpServletRequest request
                        , HttpServletResponse response )
      throws com.ai.aspire.authentication.AuthorizationException
   {
      // If not required dont do it
      String verifyAccess =
         AppObjects.getIConfig().getValue(AspireConstants.VERIFY_ACCESS,"no");
      if (verifyAccess.equals("no")) { return true; }
      try
      {
         IAuthentication authObject =
         (IAuthentication)(AppObjects.getIFactory().getObject(AspireConstants.AUTHENTICATION_OBJECT,null));

         if (authObject == null)
         {
            AppObjects.log("auth: Could not find authorization object");
            throw new AuthorizationException("auth: Could not locate an authroization service provider");
         }

         if (authObject instanceof IAuthentication1)
         {
            // new authentication scheme
            AppObjects.log("auth: Verifying using IAuthentication1");
            IAuthentication1 auth1 = (IAuthentication1)authObject;
            return auth1.isAccessAllowed(userid,request,response);
         }
         else
         {
            IResourceExtractor resExtractor =
            (IResourceExtractor)(AppObjects.getIFactory().getObject(IResourceExtractor.NAME,null));
            String resource = resExtractor.extractResource(request);

            boolean bReply = authObject.isAccessAllowed(userid, resource);
            if (bReply == true){ return true;}

            AppObjects.info("ServletUtils","authd:%1s is not allowed access to %2s",userid,resource);
            // access not allowed
            // redirect the page
            String accessDeniedPage = AppObjects.getIConfig().getValue(AspireConstants.ACCESS_DENIED_PAGE,null);
            if (accessDeniedPage == null)
            {
               AppObjects.log("error.auth: Access denied page not specified");
            }
            return false;
         }
      }
      catch(com.ai.application.interfaces.RequestExecutionException x)
      {
         throw new AuthorizationException("auth: Could not locate an authroization service provider",x);
      }
   }

/***************************************************************************
 * Redirect logic: System wide redirect
 ***************************************************************************
 */
   static public void redirect(final String url
                        , HttpServletRequest request
                        , HttpServletResponse response
                        , ServletContext servletContext)
      throws java.net.MalformedURLException,
               java.io.IOException,
               ServletException,
            com.ai.servlets.RedirectorException
   {
      try
      {
      AppObjects.info("ServletUtils","Request to redirect to:%1s",url);
      IRedirector pageRedirector =
         ((IRedirector)(AppObjects
                        .getIFactory()
                        .getObject(com.ai.servlets.IRedirector.NAME,null)));
       pageRedirector.redirect(url,request,response,servletContext);
      }
      catch(com.ai.application.interfaces.RequestExecutionException x)
      {
         AppObjects.log(x);
         throw new RedirectorException("error.redirect: Could not locate the redirector",x);
      }
   }
   /***************************************************************************
    * Redirect logic: aspireServerSideRedirect
    ***************************************************************************
    */
      static public void redirectServerSideWithParams(final String url
                           , Map params
                           , HttpServletRequest request
                           , HttpServletResponse response
                           , ServletContext servletContext)
         throws java.net.MalformedURLException,
                  java.io.IOException,
                  ServletException,
               com.ai.servlets.RedirectorException
      {
         try
         {
         AppObjects.info("ServletUtils","Request to redirect to:%1s",url);
         IRedirector1 pageRedirector =
            ((IRedirector1)(AppObjects
                           .getIFactory()
                           .getObject(AspireConstants.SERVER_SIDE_REDIRECTOR,null)));
          pageRedirector.redirect(url,params,request,response,servletContext);
         }
         catch(com.ai.application.interfaces.RequestExecutionException x)
         {
            throw new RedirectorException("error.redirect: Could not locate the serveside redirector",x);
         }
      }
      /***************************************************************************
       * Redirect logic: aspireServerSideRedirect
       ***************************************************************************
       */
         static public void redirectGeneric(final String url
        		 			  , String redirectType
                              , HttpServletRequest request
                              , HttpServletResponse response
                              , ServletContext servletContext)
            throws java.net.MalformedURLException,
                     java.io.IOException,
                     ServletException,
                  com.ai.servlets.RedirectorException
         {
            try
            {
            AppObjects.info("ServletUtils","Request to redirect to (generic):%1s",url);
            IRedirector pageRedirector =
               ((IRedirector)(AppObjects
                              .getIFactory()
                              .getObject(AspireConstants.GENERIC_REDIRECTOR + "." + redirectType,null)));
             pageRedirector.redirect(url,request,response,servletContext);
            }
            catch(com.ai.application.interfaces.RequestExecutionException x)
            {
               throw new RedirectorException("error.redirect: Could not locate the serveside redirector",x);
            }
         }
   /**
    * @deprecated use the one with out the http request object
    */
   static public String getQueryString(HttpServletRequest srcRequest
                              , String relativeURL)
                              throws java.net.MalformedURLException
   {
//      java.net.URL requestURL =
//         new java.net.URL(HttpUtils.getRequestURL(srcRequest).toString());
//      java.net.URL thisURL = new java.net.URL(requestURL,relativeURL);
//      String queryString = thisURL.getQuery();
//      return queryString;
      StringTokenizer urlTokens = new StringTokenizer(relativeURL,"?");
      if (urlTokens.countTokens() > 1)
      {
         urlTokens.nextToken();
         return urlTokens.nextToken();
      }
      else
      {
         return null;
      }
   }
   static public String getURLPrefix(HttpServletRequest request)
   {
	   
	   String uri = request.getRequestURI();
	   String url = request.getRequestURL().toString();
	   int i = url.indexOf(uri);
	   return url.substring(0,i);
   }

   static public String getQueryString(String relativeURL)
                              throws java.net.MalformedURLException
   {
      StringTokenizer urlTokens = new StringTokenizer(relativeURL,"?");
      if (urlTokens.countTokens() > 1)
      {
         urlTokens.nextToken();
         return urlTokens.nextToken();
      }
      else
      {
         return null;
      }
   }
   public static void printHashtable(Hashtable tbl)
   {
      for(Enumeration e=tbl.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         System.out.println("key: " + key);
         Object value = tbl.get(key);
         if (value instanceof java.lang.String[])
         {
            System.out.println("array detected");
         }
         if (value instanceof String)
         {
            System.out.println("string detected");
         }

         System.out.println("value: " + value.toString());
      }
      return;
   }
   public static Hashtable convertHashtable(Hashtable tbl)
   {
      Hashtable newTbl = new Hashtable();
      for(Enumeration e=tbl.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         Object value = tbl.get(key);
         if (value instanceof java.lang.String[])
         {
            value = ((String[])value)[0];
            newTbl.put(key.toLowerCase(),value);
         }
         else
         {
            newTbl.put(key.toLowerCase(),value);
         }
      }
      return newTbl;
    }
/***************************************************************************
 * getPrintWriter()
 ***************************************************************************
 */
   static public PrintWriter prepareResponseAndGetPrintWriter(HttpServletResponse response, String inContentType)
      throws IOException
   {
      String contentType = (inContentType == null) ? "text/html" : inContentType;
      response.setContentType(contentType);
      response.setDateHeader("Expires",0);
      return response.getWriter();
   }
/***************************************************************************
 * exportURLToaFile
 ***************************************************************************
 */
   static public void exportURLToAFile(final String inUrlString, final String filename)
      throws IOException
   {
      InputStream is=null;
      OutputStream os=null;
      try
      {
         java.net.URL url = new java.net.URL(inUrlString);
         is = url.openStream();
         os = new FileOutputStream(filename);
         FileUtils.copy(is,os);
      }
      finally
      {
         FileUtils.closeStream(is);
         FileUtils.closeStream(os);
      }
   }
   static public void exportURLToAStream(final String inUrlString, OutputStream os)
      throws IOException
   {
      InputStream is=null;
      try
      {
         java.net.URL url = new java.net.URL(inUrlString);
         is = url.openStream();
         FileUtils.copy(is,os);
      }
      finally
      {
         FileUtils.closeStream(is);
      }
   }

   static public String exportURLToAString(final String inUrlString)
      throws IOException
   {
      ByteArrayOutputStream baos = null;

      try
      {
         baos = new ByteArrayOutputStream();
         exportURLToAStream(inUrlString,baos);
         return baos.toString();
      }
      finally
      {
         FileUtils.closeStream(baos);
      }
   }
   static public void logRequestDetails(Object logsource, HttpServletRequest request)
   {
	   StringBuffer URL = request.getRequestURL();
	   String uri = request.getRequestURI();
	   String pathinfo = request.getPathInfo();
	   String urlPrefix = ServletUtils.getURLPrefix(request);
	   
	   AppObjects.info(logsource, "url: %1s", URL.toString());
	   AppObjects.info(logsource, "uri: %1s", uri);
	   AppObjects.info(logsource, "pathinfo: %1s", pathinfo);
	   AppObjects.info(logsource, "urlPrefix: %1s",urlPrefix);
   }
/***************************************************************************
 * getSuitablePrintWriter
 ***************************************************************************
 */
 static public PrintWriter getSuitablePrintWriter(HttpServletRequest request
                                                ,HttpServletResponse response
                                                ,PrintWriter previousPrintWriter )
 {
   try{
      // return the incoming printwriter if it is not null
      if (previousPrintWriter != null) return previousPrintWriter;

      // See if the printwriter exists in the http request
      PrintWriter printWriter =
       (PrintWriter)request.getAttribute(AspireConstants.PER_REQUEST_PRINT_WRITER);
      if (printWriter != null) return printWriter;

      //Set content type and obtain the printwriter from the response
      response.setContentType("text/html");
      printWriter = response.getWriter();

      // Register the printwriter for future retrievals
      request.setAttribute(AspireConstants.PER_REQUEST_PRINT_WRITER,printWriter);

      return printWriter;
  }
  catch(java.io.IOException x)
  {
      AppObjects.log("Could not get printwriter");
      AppObjects.log(x);
      return null;
  }
 } // end of function
 /***************************************************************************
  * getWebApplicationContext
  ***************************************************************************
  */
  static public String getWebApplicationContext()
  {
     return AppObjects.getValue("aspireContext",null);
  }//end of function

  public static String getSubstitutedURL(String urlString, Hashtable args)
  {
   if (args.get("aspirecontext") != null)
   {
      return SubstitutorUtils.urlencodeSubstitute(urlString,args);
   }
   AppObjects.log("Warn: aspireContext not in the request parameters.Going to use the global aspirecontext");
   Map map = new HashMap();

   //Shell dictionary
   IDictionary paramDictionary = new MapDictionary(map);

   //Search context dictionary first
   paramDictionary.addChild(ContextDictionaryHolder.s_contextDictionary);

   //Search url arguments next
   paramDictionary.addChild(new HashtableDictionary(args));

   //Search configuration last
   paramDictionary.addChild(ConfigDictionary.self);

   //Replace
   return SubstitutorUtils.urlencodeSubstitute(urlString,paramDictionary);
  }

  public static String getSubstitutedURLUsingAMap(String urlString, Map args)
  {
   if (args.get("aspirecontext") != null)
   {
      return SubstitutorUtils.urlencodeSubstitute(urlString,args);
   }
   AppObjects.log("Warn: aspireContext not in the request parameters.Going to use the global aspirecontext");
   Map map = new HashMap();

   //Shell dictionary
   IDictionary paramDictionary = new MapDictionary(map);

   //Search context dictionary first
   paramDictionary.addChild(ContextDictionaryHolder.s_contextDictionary);

   //Search url arguments next
   paramDictionary.addChild(new MapDictionary(args));

   //Search configuration last
   paramDictionary.addChild(ConfigDictionary.self);

   //Replace
   return SubstitutorUtils.urlencodeSubstitute(urlString,paramDictionary);
  }
  
  /**
   **************************************************************
   * getCookie
   **************************************************************
   */
  public static Cookie getCookie(String cookieName, HttpServletRequest request)
  {
	  Cookie[] carray = request.getCookies();
	  if (carray == null)
	  {
		  return null;
	  }
	  for(Cookie c: carray)
	  {
		  if (c.getName().equals(cookieName))
		  {
			  return c;
		  }
	  }
	  return null;
  }

  /**
   **************************************************************
   * end of class
   **************************************************************
   */
}//end of class

/**
 **************************************************************
 * A sibling class
 **************************************************************
 */
class ContextDictionaryHolder
{
   public static IDictionary s_contextDictionary = null;
   static
   {
      String aspireContext = ServletUtils.getWebApplicationContext();
      if (aspireContext == null) aspireContext = "";
      HashMap m = new HashMap();
      m.put("aspirecontext",aspireContext);
      s_contextDictionary = new MapDictionary(m);
   }
}//end of class

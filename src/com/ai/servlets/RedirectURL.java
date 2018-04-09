package com.ai.servlets;
import com.ai.application.utils.AppObjects;
import com.ai.servletutils.ServletUtils;
import java.util.Hashtable;
import javax.servlet.*;
import javax.servlet.http.*;

public class RedirectURL
{
   public static String SERVER_SIDE = "serverside";
   public static String CLIENT_SIDE = "clientside";
   public static String DEFAULT = "default";
   public String urlString;
   public String redirectionType;
   public RedirectURL(String inUrl, String inRedirectionType)
   {
      urlString = inUrl;
      redirectionType = inRedirectionType;
   }

   public static void redirect(RedirectURL redirectURL
                               , Hashtable optionalParams
                               , HttpServletRequest request
                               , HttpServletResponse response
                               , ServletContext servletContext)
         throws com.ai.servlets.RedirectorException
                ,javax.servlet.ServletException
                ,java.io.IOException
                ,java.net.MalformedURLException
   {
      if (redirectURL.redirectionType.equals(RedirectURL.DEFAULT))
      {
         ServletUtils.redirect(redirectURL.urlString
                               ,request
                               ,response
                               ,servletContext);
      }
      else if (redirectURL.redirectionType.equals(RedirectURL.SERVER_SIDE))
      {
         ServletUtils.redirectServerSideWithParams(redirectURL.urlString
                               ,optionalParams
                               ,request
                               ,response
                               ,servletContext);
      }
      else
      {
    	  AppObjects.trace("RedirectURL","Redirecting using a generic redirector for:%1s", redirectURL.redirectionType);
    	  ServletUtils.redirectGeneric(redirectURL.urlString, 
    			  				redirectURL.redirectionType, 
    			  				request, 
    			  				response, 
    			  				servletContext);
      }
   }//end of function
}//end of class

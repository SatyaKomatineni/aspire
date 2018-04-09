package com.ai.servlets;

import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.RequestExecutorResponse;
import com.ai.application.utils.AppObjects;
import com.ai.common.StringUtils;
import com.ai.parts.AHttpPartObject;
import com.ai.servletutils.ServletUtils;

/**
 * How does this work
 * if you are logged in allow to forward
 * otherwise
 * redirec to a login page
 * 
 * needed config params:
 * *********************
 * what flag to check
 * where to go if if not there
 */
public class GeneralLoginConstraint extends AHttpPartObject
{
	private String m_loginFlagName;
	private String m_redirectToURL;
	
    //Implement these methods as necessary
    protected void readConfigParametersAndInitialize()
    throws ConfigException
    {
    	//initialzie params
    	m_loginFlagName = readMandatoryConfigArgument("loginFlagName");
    	m_loginFlagName = m_loginFlagName.toLowerCase();
    	m_redirectToURL = readMandatoryConfigArgument("redirectToURL");
    }
    protected Object executeRequestForHttpPartObject(HttpServletRequest request
	         ,HttpServletResponse response
	         ,HttpSession session
	         ,Map inArgs)
	         throws RequestExecutionException
    {
    	String loginflag = (String)session.getAttribute(m_loginFlagName);
    	boolean bLoggedin = StringUtils.ConvertStringToBoolean(loginflag, false);
    	if (bLoggedin == true)
    	{
    		return new RequestExecutorResponse(true);
    	}
    	
		AppObjects.warn(this,"User has not logged in for app:%s", m_loginFlagName);
		//redirect the user to a different URL
		redirectToLoginPage(request,response,m_redirectToURL,inArgs);
		
		//discontinue processing by returning false
		return new RequestExecutorResponse(false);
    }
    private void redirectToLoginPage(HttpServletRequest request,
            HttpServletResponse response,
            String loginPageURL,
            Map<String,Object> inArgs)
            throws RequestExecutionException
      {
         String targetURI = request.getRequestURI();
         AppObjects.info(this,"LV:target uri:%1s",targetURI);

         //Hashtable t = new Hashtable();
         //t.put("aspire_login_targeturi",targetURI);
         //t.put("aspirecontext",request.getContextPath().substring(1));
         
         inArgs.put("aspire_login_targeturi",targetURI);
         inArgs.put("aspirecontext",request.getContextPath().substring(1));

         String newURL = ServletUtils.getSubstitutedURL(loginPageURL,(Hashtable)inArgs);

         //See what the target url is
         String uri = request.getRequestURI();
         String paramstring = request.getQueryString();

         String targetUrl = uri;

         //Add parameters if they are available
         if (paramstring != null)
         {
             targetUrl = uri + "?" + paramstring;
         }

         //escape the target url as it will http encoded in it
         String escapedTargetUrl = URLEncoder.encode(targetUrl);
         AppObjects.info(this,"Escaped target url is:%1s",escapedTargetUrl);

         String finalNewUrl = newURL + "&aspire_target_url=" + escapedTargetUrl;
         AppObjects.info(this,"Redirecting to :%1s",finalNewUrl);

         try
         {
            response.sendRedirect(response.encodeRedirectURL(finalNewUrl));
         }
         catch(java.io.IOException x)
         {
            throw new RequestExecutionException("Error:LV: could not redirect using encode redirect",x);
         }
         return;
      }//eof-class
}//eof-class

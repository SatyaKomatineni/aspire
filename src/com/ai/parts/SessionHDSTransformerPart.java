/*
 *
 * Returns a string by transforming an "ihds" variable in session
 * via aspire transforms.
 * 
 * uses the standard aspire url format.
 * Any AITransform is allowed.
 * Both aspire trasform and xslt transform will work
 * 
 * JSP transform will not work
 * 
 * Ex: usage
 * *********
 * 
 * request.r1.classname=com.ai.parts.SessionHDSTransformerPart
 * 
 * request.r1.sessionHDSVariableName=\
 * your-session-variable-name-case-sensitive
 * 
 * request.r1.aspireUrlName=myaspire-url
 * request.r1.resultName=your-intended-string-name
 *
 * The following sytanx is standarl
 * ************************************ 
 * myaspire-url=aspire:\dir\template.html
 * 
 * 
 */
package com.ai.parts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;

import com.ai.htmlgen.*;
import com.ai.aspire.utils.*;
import com.ai.common.TransformException;

public class SessionHDSTransformerPart extends AHttpPart
{

	   protected Object executeRequestForHttpPart(String requestName
	         ,HttpServletRequest request
	         ,HttpServletResponse response
	         ,HttpSession session
	         ,Map inArgs)
	         throws RequestExecutionException
	   {
	   	 try
		 {
		   	 String variableName = 
		   	     AppObjects.getValue(requestName + ".sessionHDSVariableName");
		   	 
		   	 String aspireUrl = 
		   	     AppObjects.getValue(requestName + ".aspireUrlName");
		   	 
		   	 ihds data = (ihds)session.getAttribute(variableName);
		   	 
		   	 if (data == null)
		   	 {
		   	     throw new RequestExecutionException
		   	        ("There is no session variable named:" + variableName);
		   	 }
		   	 return TransformUtils.transformHdsToString(aspireUrl,(IFormHandler)data);
		 }
	   	 catch(ConfigException x)
		 {
	   	 	throw new RequestExecutionException("Error:config exception",x);
		 }
	   	 catch(TransformException x)
		 {
	   	 	throw new RequestExecutionException("Error:transform exception",x);
		 }
	   }//eof-function
}//eof-class

/*
 * Created on Dec 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.parts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;

/**
 * @author satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SessionVariablesPart extends AHttpPart
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
		   	 String variableName = AppObjects.getValue(requestName + ".variableName");
		   	 String newVariableName = AppObjects.getValue(requestName + ".newVariableName",variableName);
		   	 String variableValue = (String)inArgs.get(variableName.toLowerCase());
		   	 if (variableValue == null)
		   	 {
		   	 	AppObjects.info(this,"Variable %1s does not exist",variableName);
		   	 }
		   	 else
		   	 {
		   	 	AppObjects.info(this,"Placing %1s : %2s in session", variableName, variableValue);
		   	 	session.setAttribute(newVariableName, variableValue);
		   	 }
		   	 return new Boolean(true);
		 }
	   	 catch(ConfigException x)
		 {
	   	 	throw new RequestExecutionException("Error:config exception",x);
		 }
	   }//eof-function
}//eof-class

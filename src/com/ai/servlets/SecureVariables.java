/*
 * Created on Dec 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.servlets;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.htmlgen.ISecureVariables;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SecureVariables 
{
	private static ISecureVariables sSecureVariables = null;
	static {
	    try
	    {
	    	sSecureVariables = 
	    		(ISecureVariables)AppObjects.getIFactory().getObject(ISecureVariables.NAME,null);
	    }
	    catch (RequestExecutionException x)
	    {
	      AppObjects.log("Warn: Could not get a secure variable object",x);
	    }
	}
	  public static boolean isASecureVariable(final String variable)
	  {
	   if (sSecureVariables == null)
	   {
	      return false;
	   }
	   else
	   {
	      return sSecureVariables.isASecureVariable(variable);
	   }
	  }
}//eof-class

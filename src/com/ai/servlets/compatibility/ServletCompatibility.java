/*
 * Created on Sep 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.ai.servlets.compatibility;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Satya
 *
 */
public class ServletCompatibility 
{
	private static IServletCompatibility s_self
		= ServletCompatibilityFactory.getServletCompatibility();
	public static String getRequestURL(HttpServletRequest request)
	{
		return s_self.getRequestURL(request);
	}
	static public Hashtable parseQueryString(String queryString)
	{
		return s_self.parseQueryString(queryString);
	}
	static public void   putSessionValue(HttpSession session, String key, Object value)
	{
		s_self.putSessionValue(session,key,value);	
	}
	static public Object   getSessionValue(HttpSession session, String key)
	{
		return s_self.getSessionValue(session,key);	
	}
	
	static public Enumeration getSessionValueNames(HttpSession session)
	{
		return s_self.getSessionValueNames(session);
	}
}

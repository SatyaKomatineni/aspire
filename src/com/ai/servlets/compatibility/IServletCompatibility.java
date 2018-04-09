/*
 * Created on Sep 23, 2004
 *
 */
package com.ai.servlets.compatibility;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 */
public interface IServletCompatibility 
{
	public String getRequestURL(HttpServletRequest request);
	public Hashtable parseQueryString(String queryString);
	public Object getSessionValue(HttpSession session, String key);	
	public void  putSessionValue(HttpSession session, String key, Object value);	
	public Enumeration  getSessionValueNames(HttpSession session);
}

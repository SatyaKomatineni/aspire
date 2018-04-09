/*
 * Created on Sep 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.ai.servlets.compatibility;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;

/**
 * @author Satya
 *
 */
public class Servlet22Compatibility implements IServletCompatibility
{
	public String getRequestURL(HttpServletRequest request)
	{
		return request.getRequestURI();
	}
	public Hashtable parseQueryString(String queryString)
	{
		return HttpUtils.parseQueryString(queryString);
	}
	public Object getSessionValue(HttpSession session, String key)
	{
		return session.getValue(key);	
	}
	public void   putSessionValue(HttpSession session, String key, Object value)
	{
		session.putValue(key,value);	
	}
	public Enumeration getSessionValueNames(HttpSession session)
	{
		String[] values = session.getValueNames();
		Vector v = new Vector();
		for (int i=0;i<values.length;i++)
		{
			v.add(values[i]);
		}
		return v.elements();
	}
}

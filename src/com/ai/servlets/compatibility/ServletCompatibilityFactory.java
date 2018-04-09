/*
 * Created on Sep 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.ai.servlets.compatibility;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.servlets.ServletContextHolder;

/**
 * @author Satya
 *
 */
public class ServletCompatibilityFactory
{
	static private IServletCompatibility s_o = null;
	static
	{
		try
		{
			Object o = AppObjects.getObjectAbsolute("aspire.servletsupport.servletcompatibility",null);
			s_o = (IServletCompatibility)o;
		}
		catch(RequestExecutionException x)
		{
			int minor = ServletContextHolder.getServletContext().getMinorVersion();
			int major = ServletContextHolder.getServletContext().getMajorVersion();
			int completever = major * 10 + minor;
			if (completever < 23)
			{
				AppObjects.warn("ServletCompatibilityFactory"
					,"Servlet version less that 23 detected:" + completever);
				s_o = new Servlet22Compatibility();
			}
            else
            {
               s_o = new Servlet23Compatibility();
            }
		}
	}

	public static IServletCompatibility getServletCompatibility()
	{
		return s_o;
	}
}

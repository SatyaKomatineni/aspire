package com.ai.servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;

public class AspireURL 
{
	protected String baseConfigString;
	public static String URL_QUALIFIER_DISPLAY = "url";
	public static String URL_QUALIFIER_UPDATE = "request_name";
	
	public static String CONSTRAINT_REQUEST_NAMES="constraintRequestNames";
	public AspireURL(String inRootConfigString)
	{
		baseConfigString = inRootConfigString;
	}
	public String getConstraintRequestNames()
	{
		return AppObjects.getValue(baseConfigString + "." + CONSTRAINT_REQUEST_NAMES,null);
	}
	public String getBaseConfigString()
	{
		return baseConfigString;
	}
	public List<String> getConstraintRequestNamesAsAList()
	{
		String list = 
			getConstraintRequestNames();
		if (list == null) return null;
		//constraint names exist
		return Tokenizer.tokenizeAsList(list, ",");
	}	//public static AspireURL createAspireURL
	
	public String getMandatoryValueFor(String keyname) throws ConfigException
	{
		return AppObjects.getValue(baseConfigString + "." + CONSTRAINT_REQUEST_NAMES);
	}
	public String getValueFor(String keyname)
	{
		return AppObjects.getValue(baseConfigString + "." + CONSTRAINT_REQUEST_NAMES,null);
	}
	public static AspireURL createAppropriateAspireURL(HttpServletRequest request)
	{
		String urlname = request.getParameter(URL_QUALIFIER_DISPLAY);
		if (urlname != null)
		{
			return new AspireDisplayURL(urlname);
		}
		//it is not a display
		urlname = request.getParameter(URL_QUALIFIER_UPDATE);
		if (urlname != null)
		{
			return new AspireUpdateURL(urlname);
		}
		AppObjects.warn("AspireURL","It is not a display or an update URL");
		return new NonAspireURL();
	}
}

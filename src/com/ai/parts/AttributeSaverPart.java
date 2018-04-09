/*
 * Created on Jan 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.parts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.SubstitutorUtils;
import com.ai.common.Tokenizer;
import com.ai.db.DBException;

/**
 * @author satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AttributeSaverPart extends DBProcedure {

	protected Object executeDBProcedure(String requestName, Hashtable arguments)
			throws DBException 
	{
     
		try
		{
			//mandatory args
	        String attributeNames = AppObjects.getValue(requestName + ".attributeNames");
	        attributeNames = SubstitutorUtils.generalSubstitute(attributeNames,arguments);
	        AppObjects.info(this,"Working with attribute names:%1s", attributeNames);
	        
	        String attributeNameKey = AppObjects.getValue(requestName + ".attributeNameKey");
	        String attributeValueKey = AppObjects.getValue(requestName + ".attributeValueKey");
	        String attributeSaveRequest = AppObjects.getValue(requestName + ".attributeSaveRequest");

	        String lAttributeNameKey = attributeNameKey.toLowerCase();
	        String lAttributeValueKey = attributeValueKey.toLowerCase();
	        
	        Vector v = Tokenizer.tokenize(attributeNames,",");
	        Enumeration e = v.elements();
	        while(e.hasMoreElements())
	        {
	        	String attributeName = (String)e.nextElement();
	        	String attributeValue=(String)arguments.get(attributeName);
	        	arguments.put(lAttributeNameKey,attributeName);
	        	arguments.put(lAttributeValueKey,attributeValue);
	        	AppObjects.info(this,"Attmpting to save (%1s:%2s)",attributeName,attributeValue);
	        	AppObjects.getObject(attributeSaveRequest,arguments);
	        }
			return null;
		}
		catch(ConfigException x)
		{
			throw new DBException("Configuration error",x);
		}
		catch(RequestExecutionException x)
		{
			throw new DBException("Could not execute the individual save attribute",x);
		}
	}

}

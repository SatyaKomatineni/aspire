package com.ai.parts.configobjects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.ISingleThreaded;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.db.DBBaseJavaProcedure;
import com.ai.db.DBException;
import com.ai.parts.CollectionWorkSplitterObjectPart;
import com.ai.parts.DBProcedure;
import com.ai.parts.DBProcedureObject;

/**
 * @author Satya Komatineni
 *
 * Status: Experimental/draft
 * Modeled After: DBProcedureObject
 *
 * Why?
 * ******************
 * I need a base class to represent data objects.
 * Data objects just hold data
 * These are instantiated from config files
 * By default instantiated objects from config file are singletons
 * But these are not.
 * 
 * Logic 7/8/2016
 * ****************
 * Extend this object and  override initialize method
 * to read your paramenters. 
 * You can maintain state in instance variables.
 *
 * Caution
 * *********
 * This class is made multi-instance.
 * 
 * Key differentiation of this class
 * *************************************
 * You can maintain state in instance variables.
 * 
 * Drawbacks
 * **********************************
 * Does not have arguments that constructed it
 * 
 * 
 * @see ICreator
 * @see ISingleThreaded
 * @see IInitializable
 * @see DBProcedure 
 * @see CollectionWorkSplitterObjectPart
 * @see DBProcedureObject
 * @see AFactoryPart
 */
public abstract class DataObject 
implements ISingleThreaded, IInitializable, ICreator
{

	@Override
	abstract public void initialize(String requestName);
	abstract protected void initializeWithArgs(String requestName, Object args);
	
	//Should have been final and private
	@Override
	public Object executeRequest(String requestName, Object args) 
			throws RequestExecutionException 
	{
		initializeWithArgs(requestName, args);
		// To simulate a data object
		return this;
	}
	
}//eof-class

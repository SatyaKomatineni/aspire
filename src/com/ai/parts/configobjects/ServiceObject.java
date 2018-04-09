package com.ai.parts.configobjects;

import com.ai.application.interfaces.AFactoryPart;
import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.ISingleThreaded;
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
 * I need a base class to represent a service object.
 * A service object doesn't mean it is SOAP or REST
 * A service object is just a simple POJO
 * A service object is geared for behavior as opposed to data.
 * A service object is an implementor of an interface
 * These are usually instantiated from config files
 * By default instantiated objects from config file are singletons
 * The service objects are SINGLETONS. 
 * 
 * Logic 7/8/2016
 * ****************
 * Extend this object and  override initialize method
 * to read your paramenters from the config file. 
 * You can maintain state in instance variables because
 * these objects hang around as singletons.
 *
 * Caution
 * *********
 * This class is a SINGLETON.
 * 
 * Key differentiation of this class
 * *************************************
 * You can maintain state in instance variables.
 * 
 * Drawbacks
 * **********************************
 * not sure
 * 
 * Doubt
 * ************************************
 * It is not clear if a dynamic service object is necessary
 * A dynamic SO may have wanted args to initialize itself
 * But being a singleton which "args" to pass?
 * Each invocation may have different context and hence args.
 * I don't think it makes sense!
 * This class is constructed only once!
 * not sure I understand the "args" question correctly at this point!
 * 
 * Prefix for derived classes
 * *************************************
 * May be "SO". Example: MyServiceObject or MySO
 * So stick with SO or Fully Expand it
 * 
 * @see ICreator
 * @see ISingleThreaded
 * @see IInitializable
 * @see DBProcedure 
 * @see CollectionWorkSplitterObjectPart
 * @see DBProcedureObject
 * @see AFactoryPart
 * 
 * @see DataObject
 * @see SimpleDataObject
 * 
 * Usage
 * *************
 * Use this to implement Singleton services based on interfaces
 * Override initialize if you want to read additional args from properties file
 * By default the end result instantiates the class and expect the caller 
 * to cast it to the right type and make typed calls on it.
 * 
 */
public interface ServiceObject 
extends IInitializable
{
	public void initialize(String requestName);
}//eof-class

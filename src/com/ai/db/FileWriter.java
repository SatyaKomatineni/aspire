/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.ai.application.interfaces.AFactoryPart;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.FileUtils;
import com.ai.common.SubstitutorUtils;

/**
 * 
 * 7/6/2015
 * This is the original file writer going back to a decade
 * It writes key value pairs from a hashtable to the file
 * Correct name should have been "DebugFileWriter"
 * 
 * @see FileWriter1
 * 
 * FileWriter1 will do writing based on string substitutions
 * That one can selectively choose fields to write
 *
 */
public class FileWriter extends AFactoryPart { 

	private final static String MODE_APPEND = "append";
	private final static String MODE_OVERWRITE = "overwrite";
    protected Object executeRequestForPart(String requestName, Map args)
            throws RequestExecutionException
    {
        try 
        {       
	        String filename;
	        String appendMode;
	        try
	        {
	                filename = AppObjects.getIConfig().getValue(requestName + ".filename");
	                
	                //Translate the filename using args for substitutions
	                filename = SubstitutorUtils.generalSubstitute(filename, (Map)args);
	                AppObjects.trace(this, "Substituted filename:%1s", filename);
	                
	                //Translate it to relative aspire directories
	                filename = FileUtils.translateFileName(filename);
	                AppObjects.trace(this, "Aspire Directory translated filename filename:%1s", filename);
	                
	                appendMode = AppObjects.getValue(requestName + ".openmode", 
	                		FileWriter.MODE_OVERWRITE);
	        }
	        catch(ConfigException x)
	        {
	                AppObjects.log(x);
	                AppObjects.log("Error", "File name not in config file");
	                throw new RequestExecutionException (
	                        RequestExecutionException.LOADED_CLASS_CAN_NOT_EXECUTE_REQUEST );
	        }
	        PrintWriter fileWriter;
	        if (appendMode.equals(FileWriter.MODE_APPEND))
	        {
	            fileWriter = new PrintWriter( new FileOutputStream(filename,true) );
	        }
	        else
	        {
	            fileWriter = new PrintWriter( new FileOutputStream(filename) );
	        }                
	        writeTo(fileWriter, args, requestName);
	        fileWriter.close();   
	        return new com.ai.application.interfaces.RequestExecutorResponse(true);
        }
        catch (java.io.IOException x)
        {
            AppObjects.log(x);
            return new com.ai.application.interfaces.RequestExecutorResponse(false);
        }
    }//eom
        	
    /**
     * @param fileWriter
     * @param args
     * @param requestName
     * Overide this if you want to entirely take over.
     * Otherwise override the map one below.
     * This function should never write (promise not to write) if the args are a Map!
     * 
     */
	protected void writeTo(PrintWriter fileWriter, Map args, String requestName)
	{
		if (args == null) 
		{
			fileWriter.println("************************New record******************");
			fileWriter.println("Input arguments null");
			return;
		}
	    // arguments passed
	    // The assumptions is that this is a vector of or a hashtable 
	    if (args instanceof Vector)
	    {
			fileWriter.println("************************New record******************");
	       fileWriter.println("Vector passed. This should not be happening");
	       return;
	    }
       writeToUsingAMap(fileWriter, args, requestName);
	}//eof-function
	
	protected void writeToUsingAMap(PrintWriter fileWriter, Map args, String requestName)
	{
        for (Object o: args.keySet())
        {
          Object key = o;
          fileWriter.println(key + ":" + args.get(key) );
        }
	}//eof-function
}//eof-class 

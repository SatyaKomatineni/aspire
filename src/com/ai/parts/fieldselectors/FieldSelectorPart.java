package com.ai.parts.fieldselectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.application.interfaces.AFactoryPart1;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.SubstitutorUtils;
import com.ai.common.Tokenizer;

/**
 * Transforms a specified string with substitution arguments in it and returns it
 *
 *
 * Configuration
 * *********************
 * 
 * request.myselection.classname=\
 * com.ai.parts.fieldselectors.FieldSelectorPart
 * 
 * .select=column1:1,column2:2,column3:3
 * .row={some_key} \\or
 * .row=Crazy Fox Jumped Over the Fence
 * .splitterRequestName=WordSplitter
 * 
 * request.WordSplitter.classname=\
 * com.ai.parts.fieldselectors.WordSplitter
 *
 * //Or
 * request.MySpecialSplitter.classname=\
 * com.ai.parts.fieldselectors.ConfigRegexSplitter
 * .regex=your-regular-expression-here
 * 
 * Output
 * **********
 * A hashmap of key value pairs
 * FactoryPart1 will load the values into incoming hashtable
 * 
 * caution
 * *************
 * This is a pipeline part
 * No local variables
 *
 */

public class FieldSelectorPart extends AFactoryPart1
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
    	//Example: 1:column1,2:column2,3:column3
    	//key: select
    	String selectString;
    	
    	//Line to split
    	//May come from args as a key
    	//key: row
    	String rowStringToSplit;
    	
    	//splitterRequestName
    	String requestNameToSplit;
    	try
    	{
    	  //Read select string spec
          selectString = AppObjects.getValue(requestName + ".select").trim();
          
          //read the row
          String inputRow = AppObjects.getValue(requestName + ".row");
          rowStringToSplit = SubstitutorUtils.generalSubstitute(inputRow, inArgs).trim();
          
          //what service object is responsible for splitting the row
          requestNameToSplit = AppObjects.getValue(requestName + ".splitterRequestName").trim();
          
          //Split the row by parsing it out 
          List<String> splitValueList = 
        		  splitStringUsingRequest(
        				  requestNameToSplit, 
        				  rowStringToSplit);

          //Get the select statement with field names
          Map<String,Integer> fieldNamesToPositionMap 
          	= getFieldNamesToPositionMap(selectString);
          
          //return a hash map
          //Map<key,value>
          return getFinalMap(splitValueList, fieldNamesToPositionMap);
    	}
    	catch(ConfigException x)
    	{
          throw new RequestExecutionException("Error:config errror",x);
    	}
    }//eof-function
    
    private List<String> splitStringUsingRequest(String rname, String s)
    throws RequestExecutionException
    {
    	AppObjects.trace(this, "String to split is:%1s",s);
    	IRowSplitter so = (IRowSplitter)AppObjects.getObject(rname, null);
    	List<String> splitValueList = so.split(s);
    	AppObjects.trace(this, "Split Value list %1s", splitValueList);
    	return splitValueList;
    }
    
    private Map<String,Integer> getFieldNamesToPositionMap(String fieldNamesToPositionString)
    {
    	//An empty initial map
    	Map<String, Integer> fieldToPositionMap = new HashMap<String,Integer>();
    	
    	//The fieldNamesToPositionString should be already trimmed
    	List<String> segs = Tokenizer.tokenizeAsList(fieldNamesToPositionString, ",");
    	for(String seg: segs)
    	{
    		List<String> fieldPosition = Tokenizer.tokenizeAsList(seg.trim(), ":");
    		String field = fieldPosition.get(0);
    		int position = Integer.parseInt(fieldPosition.get(1));
    		//no need to lowercase the field. 
    		//that will be done anyway by AFactoryPart1
    		fieldToPositionMap.put(field, position);
    	}
    	AppObjects.trace(this, "fieldNamesToPositionMap: %1s",fieldToPositionMap);
    	return fieldToPositionMap;
    }
    
    //Make sure fieldNames are lower cased
    private Map<String, String> getFinalMap(List<String> splitValues, Map<String, Integer> fieldNames)
    {
    	Map<String, String> finalFieldMap = new HashMap<String,String>();
    	
    	if (splitValues == null) return finalFieldMap;
    	if (splitValues.isEmpty()) return finalFieldMap;
    	
    	AppObjects.trace(this, "There are valid field values: %1s", splitValues.size());
    	for(String keyname: fieldNames.keySet())
    	{
    		//for each key
    		int position = fieldNames.get(keyname);
    		if (position <= 0)
    		{
    			throw new RuntimeException("Sorry selector field position must be greater than 0");
    		}
    		String keyvalue = splitValues.get(position-1);
    		finalFieldMap.put(keyname, keyvalue);
    	}
    	AppObjects.trace(this, "Final key/value pairs going into arguments are:%1s", finalFieldMap);
    	return finalFieldMap;
    }
}//eof-class






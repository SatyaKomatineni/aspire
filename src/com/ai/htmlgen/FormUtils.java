/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.htmlgen;
import com.ai.application.utils.AppObjects;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import com.ai.application.interfaces.*;
import com.ai.data.DataException;
import com.ai.data.IDataRow;
import com.ai.data.IIterator;
import com.ai.data.IMetaData;

import java.util.Hashtable;

public class FormUtils {

        public FormUtils() {
        }
        static public String FORM_CLASS_CREATE_REQUEST_SUFFIX = ".form_handler.class_request";
        public static IFormHandler getFormHandlerFor(String formName,
                                                     Hashtable parameters)
                throws RequestExecutionException
        {
                // create the argument vector
                Vector args = new Vector();
                args.addElement(formName );

                // Add parameters if the parms are not null
                if (parameters != null)
                {
                   args.addElement(parameters);
                }

                Object obj =
                AppObjects.getIFactory().getObject(
                        formName +  FormUtils.FORM_CLASS_CREATE_REQUEST_SUFFIX
                        ,args );

                return (IFormHandler)obj;
        }
        public static Object getDataObjectFor(String url,
                                                     Hashtable parameters)
                throws RequestExecutionException
        {
           try
           {
               //find out
              String dataRequestName = getDataRequestName(url);
              String className = AppObjects.getValue("request." + dataRequestName + ".classname",null);
              if (className != null)
              {
                 return AppObjects.getObject(dataRequestName,parameters);
              }
              else
              {
            	 AppObjects.warn("FormUtils", "There may be a typo with request.%1s.classname",dataRequestName);
                 return AppObjects.getObject(dataRequestName + FormUtils.FORM_CLASS_CREATE_REQUEST_SUFFIX,parameters);
              }
           }
           catch(ConfigException x)
           {
              throw new RequestExecutionException("Error:Config Exception",x);
           }
        }
        private static String getDataRequestName(String url)
              throws ConfigException
        {
           String dataRequestName =
                 AppObjects.getValue(url + ".dataRequestName",null);
		   if (dataRequestName != null) return dataRequestName;
		   
		   //datarequest name not found                 
           return AppObjects.getValue(url + ".formHandlerName");
        }
        public static Map getMap(ihds hds) throws DataException
        {
        	Map mymap = new Hashtable();
        	
        	IMetaData fields = hds.getMetaData();
        	IIterator itr = fields.getIterator();
        	for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
        	{
        		String colname = (String)itr.getCurrentElement();
        		String colvalue = hds.getValue(colname);
        		mymap.put(colname.toLowerCase(),colvalue);
        	}
        	return mymap;
        }
        
        public static Object execRequestUsingHDS(String requestName, ihds hds)
       	throws DataException, RequestExecutionException
       {
       	  Map args = getMap(hds);
       	  return AppObjects.getObject(requestName,args);
       }
        
}//eof-class
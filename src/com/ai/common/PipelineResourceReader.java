/*
 * Created on Dec 4, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.ai.application.interfaces.IConfig;
import com.ai.application.interfaces.IResourceReader;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;

/**
 * @author Satya
 * 1. Structure of the resource will be that of a URI
 * 2. No host is needed and will be empty
 * 3. it will have the following structure
 * 		pipelinereader:///requestname?arg1=a&arg2=b&arg3=4
 * 
 * 12/19/2012
 * ***********
 * Bug: params are not lowercased
 * deprecated: use instead PipelineResourceReader1
 * Delete this file after that
 * 
 */
public class PipelineResourceReader implements IResourceReader
{
   public InputStream readResource(String resourceName, IConfig config) 
   throws java.io.IOException
   {
   	try
	{
   		URI resourceUri = new URI(resourceName);
   		String queryParams = resourceUri.getQuery();
   		String requestName = resourceUri.getPath();
   		int plen = requestName.length();
   		requestName = requestName.substring(1,plen);
   		Map params = PipelineResourceReader.getParams(queryParams);
   		
   		InputStream is = 
   			(InputStream)AppObjects.getObject(requestName,params);
   		
   		return is;
	}
   	catch(URISyntaxException x)
	{
   		AppObjects.log("Error:wrong request name",x);
   		IOException y = new IOException("Wrong URI for a pipeline: " + x.getMessage());
   		y.initCause(x);
   		throw y;
	}
   	catch(RequestExecutionException x)
	{
   		AppObjects.log("Error:Problem executing pipeline for inputstream:",x);
   		IOException y = new IOException("Error:Problem executing pipeline for inputstream: " + x.getMessage());
   		y.initCause(x);
   		throw y;
	}
   }//eof-function
   
   public static void main(String[] args)
   {
   	try
	{
   		URI test = new URI("pipelinereader:///requestname?arg1=a&arg2=b&arg3=4");
   		Map params = getParams(test.getQuery());
   		System.out.println(test.getQuery());
   		System.out.println(test.getPath());
   		System.out.println(test.getPath().substring(1,test.getPath().length()));
   		System.out.println(params);
	}
   	catch(Throwable t)
	{
   		t.printStackTrace();
	}
   }//eof-function
   
   private static Map getParams(String qs)
   throws UnsupportedEncodingException
   {
   		Map pmap = new Hashtable();
   		ArrayList l = new ArrayList();
   		Tokenizer.tokenizeInto(qs,"&",l);
   		Iterator itr = l.iterator();
   		while(itr.hasNext())
   		{
   			String keyvalue = (String)itr.next();
   			Vector v = Tokenizer.tokenize(keyvalue,"=");
   			String key = (String)v.get(0);
   			String value = (String)v.get(1);
   			pmap.put(URLDecoder.decode(key,"UTF-8"),URLDecoder.decode(value,"UTF-8"));
   		}
   		return pmap;
   }
}//eof-class

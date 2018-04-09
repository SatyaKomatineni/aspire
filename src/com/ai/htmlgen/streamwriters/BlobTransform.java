/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen.streamwriters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import com.ai.htmlgen.*;
import com.ai.servlets.AspireServletException;

import javax.servlet.http.*;
import javax.servlet.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.FileUtils;
import com.ai.common.StringUtils;
import com.ai.data.DataException;
import com.ai.data.FieldNameNotFoundException;
import com.ai.data.IDataCollection;
import com.ai.data.IDataRow1;
import com.ai.data.IIterator;

/**
 * Created as a way to write blobs from databases directly to
 * html response streams.
 * will work like a file download.
 * 
 * what it is supposed to do
 * ****************************
 * 1. Read a blob from a database 
 * (u need a blobRequestName to call the database)
 * 
 * 2. Use the incoming ihds or formhandler for parameters
 * 
 * 3. From the request expect the following parameters
 * 		1. filename (basename) (filenameKey)
 * 		2. filelength (filelengthKey)
 * 		3. blob field (blobKey)
 * 4. Read the above fields
 * 
 * 5. set the content type on the response
 * 
 * 6. write to the stream
 *
 */
public class BlobTransform implements IAIHttpTransform, 
	IInitializable, 
	ISingleThreaded
{

	private String filenameKey;
	private String filelengthKey;
	private String blobKey;
	private String blobRequestName;//mandatory

	public void initialize(String requestName) 
	{
		try
		{
			//initialize your keys
			blobRequestName = AppObjects.getValue(requestName + ".blobRequestName");
			blobKey = AppObjects.getValue(requestName + ".blobKey");
			filenameKey = AppObjects.getValue(requestName + ".filenameKey");
			filelengthKey = AppObjects.getValue(requestName + ".filelengthKey");
		}
		catch(ConfigException x)
		{
			throw new RuntimeException("could not read config information for BlobTransform");
		}
	}
	
   public void transform( String htmlFilename
                          , IFormHandler formHandler
                          , HttpServletRequest request
                          , HttpServletResponse response
                          , RequestDispatcher dispatcher
                         ) throws java.io.IOException, ServletException
   {
	   IDataCollection col = null;
	   try
	   {
		   col = executeBlobRequest(
				   this.getParams(formHandler));
		   IIterator colItr = col.getIIterator();
		   colItr.moveToFirst();
		   IDataRow1 firstRow = (IDataRow1)colItr.getCurrentElement();
		   String filename = firstRow.getValue(this.filenameKey);
		   Blob blob = (Blob)firstRow.getValueAsObject(this.blobKey);
		   writeBlobToStream(response,blob,filename,this.getFilelength(firstRow));
	   }
	   catch(Exception x)
	   {
		   throw new ServletException("Problem writing Blob",x);
	   }
	   finally
	   {
		   this.closeCollectionSilently(col);
	   }//eof-finally
   }//eof-function   
   
   private void writeBlobToStream(HttpServletResponse response
		   ,Blob blob
		   ,String filename
		   ,int filelength) 
   throws AspireServletException
   		,IOException
   		,SQLException
   {
	   InputStream is = null;
	   try
	   {
		   //set content type
		   response.setContentType(this.getContentType(filename));
		   
		   //set content length
		   if (filelength >= 0)
		   {
			   response.setContentLength(filelength);
		   }
		   
		   //set disposition
		   response.setHeader("Content-Disposition", this.getDisposition(filename));
		   
		   OutputStream os = response.getOutputStream();
		   is = blob.getBinaryStream();
		   FileUtils.copy(is,os);
	   }
	   finally
	   {
		   FileUtils.closeStream(is);
	   }
   }
   
   private String getDisposition(String filename)
   {
	   String a = "attachment;filename=" + filename;
	   return a;
   }
   
   private String getContentType(String filename)
   throws AspireServletException
   {
	   String ext = FileUtils.getFileExtension(filename);
	   AppObjects.trace(this,"looking for contentype for extension:%1s", ext);
	   return ExtensionToContentTypeMapping.getContentType(ext);
   }
   private int getFilelength(IDataRow1 row)
   {
	   try
	   {
		   String filelengthS = row.getValue(this.filelengthKey);
		   
		   if (StringUtils.isEmpty(filelengthS))
		   {
			   //empty filelength
			   AppObjects.trace(this,"Empty file length specified");
			   return -1;
		   }
		   AppObjects.trace(this,"Filelength:%1s", filelengthS);
		   return Integer.parseInt(filelengthS);
	   }
	   catch(FieldNameNotFoundException x)
	   {
		   AppObjects.info(this,"filelength not specified. Assuming 0");
		   return -1;
	   }
   }
   private void closeCollectionSilently(IDataCollection col)
   {
	   if (col != null)
	   {
		   try { col.closeCollection();}
		   catch(DataException x)
		   {
			   AppObjects.log("Error:Could not close collection", x);
		   }
	   }
   }
   
   private IDataCollection executeBlobRequest(Map params)
   throws RequestExecutionException
   {
	   return (IDataCollection)AppObjects.getObject(this.blobRequestName,params);
   }
   private Hashtable getParams(IFormHandler data) throws DataException
   {
	   Hashtable ht = new Hashtable();
	   IIterator keyIter = data.getKeys();
	   for(keyIter.moveToFirst();!keyIter.isAtTheEnd();keyIter.moveToNext())
	   {
		   String keyName = (String)keyIter.getCurrentElement();
		   ht.put(keyName, data.getValue(keyName));
	   }
	   return ht;
   }
}//eof-class 

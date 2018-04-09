/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen.streamwriters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.ISingleThreaded;
import com.ai.application.utils.AppObjects;
import com.ai.common.FileUtils;
import com.ai.common.SubstitutorUtils;
import com.ai.htmlgen.FormHandlerDictionary;
import com.ai.htmlgen.IAIHttpTransform;
import com.ai.htmlgen.IFormHandler;
import com.ai.servlets.AspireServletException;

/**
 * Created as a way to write blobs from databases directly to
 * html response streams.
 * will work like a file download.
 *
 */
public class FileDownloadTransform implements IAIHttpTransform, 
	IInitializable, 
	ISingleThreaded
{

	private String filenameC;

	public void initialize(String requestName) 
	{
		try
		{
			//initialize your keys
			filenameC = AppObjects.getValue(requestName + ".filename");
		}
		catch(ConfigException x)
		{
			throw new RuntimeException("filename not found",x);
		}
	}
	
   public void transform( String htmlFilename
                          , IFormHandler formHandler
                          , HttpServletRequest request
                          , HttpServletResponse response
                          , RequestDispatcher dispatcher
                         ) throws java.io.IOException, ServletException
   {
	   try
	   {
	   		String filename = formHandler.getValue(this.filenameC);
	   		String substitutedFilename = SubstitutorUtils
				.generalSubstitute(filename
							,new FormHandlerDictionary(formHandler));
	   		String finalFilename = FileUtils.translateFileName(substitutedFilename);
	   		writeToStream(response,finalFilename);
	   }
	   catch(Exception x)
	   {
		   throw new ServletException("Problem writing file",x);
	   }
   }//eof-function   
   
   private void writeToStream(HttpServletResponse response
		   ,String filename)
   throws AspireServletException
   		,IOException
   		,SQLException
   {
	   InputStream is = null;
	   try
	   {
		   //set content type
		   response.setContentType(this.getContentType(filename));
		   
		   File file = new File(filename);
		   long filelength = file.length();
		   is = new FileInputStream(file);
		   
		   //set content length
		   if (filelength >= 0)
		   {
			   response.setContentLength((int)filelength);
		   }
		   
		   String basefilename = FileUtils.basename(filename);
		   //set disposition
		   response.setHeader("Content-Disposition", this.getDisposition(basefilename));
		   
		   OutputStream os = response.getOutputStream();
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
}//eof-class 

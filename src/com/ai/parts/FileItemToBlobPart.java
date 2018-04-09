package com.ai.parts;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.ai.application.interfaces.AFactoryPart;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.FileUtils;
import com.ai.common.SubstitutorUtils;

/**
 *
 * what is this part supposed to do
 * ************************************
 * 1. Read a file item from the hashtable 
 * (u need a key to read this:fileitemKey)
 * 
 * 2. Get the stream from the file item and drop it in the hashtable 
 * (you need a key for this:filestreamKey)
 * 
 * 3. Find the length of the file
 * 4. Drop the length of the file into the hashtable as a key
 * (filestreamKey_length)
 * 
 * 5. Drop the name of the file in the hashtable
 * (filestreamKey_filename)
 * 
 * Prerequisites
 * ******************
 * 1. You have to run the file upload that will
 * place the fileitem in the hashtable.
 * 2.Such a part is the SingleFileUpload
 * 
 */
public class FileItemToBlobPart extends AFactoryPart implements IInitializable
{
   private String fileItemKey = null;
   private String fileStreamKey = null; //filestream
   private String filenameKey = null;
   private String filelengthKey = null;
   private String fileExtKey = null;
   
	public void initialize(String requestName) 
	{
		try
		{
			fileItemKey = AppObjects.getValue(requestName + ".fileItemKey");
			fileStreamKey = AppObjects.getValue(requestName + ".fileStreamKey","filestream");
			filenameKey = fileStreamKey + "_filename";
			filelengthKey = fileStreamKey + "_length";
			fileExtKey = fileStreamKey + "_ext";
		}
		catch(ConfigException x)
		{
			throw new RuntimeException("Could not read params for FileItemToBlobPart");
		}
		
		
	}
   protected Object executeRequestForPart(String requestName
         ,Map inArgs)
         throws RequestExecutionException
   {
	   try
	   {
	   		//locate file item
	   	   FileItem fi = (FileItem)inArgs.get(this.fileItemKey);
	   	   if (fi == null)
	   	   {
	   	   		throw new RequestExecutionException
					("Error:Could not find file item for key:" + fileItemKey);
	   	   }
	   	   // get filestream
	   	   InputStream is = fi.getInputStream();
	   	   String filename = fi.getName();
	   	   long length = fi.getSize();
	   	   String sLength = Long.toString(length);
	   	   
	   	   //place them in the hashtable
	   	   inArgs.put(this.fileStreamKey,is);
	   	   inArgs.put(this.filenameKey,filename);
	   	   inArgs.put(this.filelengthKey,sLength);
	       return new Boolean(true);
	   }
	   catch(Exception x)
	   {
		   throw new RequestExecutionException("Problem in copying uploaded file",x);
	   }
   }//eof-function
}//eof-class
package com.ai.parts;

import java.io.File;
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

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.FileUtils;
import com.ai.common.StringUtils;

/**
 * this part is to test the multipart post from a variety of clients
 * Dave asked for it. So here it is.
 * 
 * What it should do:
 * ******************
 * 1. Receive a file and params through multipart post
 * 2. clients can be browsers or mobile clients coming through http
 * 3. Parse the multi part successfully
 * 4. Throw exception back if not
 * 5. Write a success message with the size of the file
 * 6. Keep the file size ot 5k for now
 * 
 * In the future
 * ****************
 * 1. Print out the file based on an input param back to the browser
 * 
 * Caution
 * ****************
 * Through AHttpPart this is a singleton
 * Do not maintain instance state
 * 
 * Parameters in the config file
 * **********************************
 * maxMemorySizeInKb: 5kb
 * maxRequestSizeInKb: 10kb
 * fileUploadFormFieldName: filename
 * 
 * Updates to the args for downstream pipeline
 * *******************************************
 * aspire_reserved_filename
 * aspire_reserved_filelength
 * + all input form fields passed
 * 
 */
public class SingleFileUPloadTestMultiPartPostCommonsPart 
extends AHttpPart implements IInitializable
{
   private String FILE_UPLOAD_CONTEXT = "aspire.global-defaults.fileupload";
   String sMaxMemorySizeInKb = "5";
   String sMaxRequestSizeInKb = "10";
   String tempDirectory = "";

   //Because this is a singleton, this will be called only once
   //So do not read request specific attributes in this function
   //Read only global configuration variables
   //Unless, this object is expected to be only once in the config files
   public void initialize(String requestName) 
   {
	   fillDefaultSizes(requestName);
   }

   private void fillDefaultSizes(String requestName)
   {
	   try
	   {
		   tempDirectory
	       	= this.getTempDirectory();
		   AppObjects.trace(this, "Temp directory:%1s", tempDirectory);
	   }
	   catch(ConfigException x)
	   {
		   throw new RuntimeException("Could not locate temp directory in config file for file upload");
	   }
   }
   private int getMaxMemorySizeInBytes(String requestName)
   {
       String lsMaxMemorySizeInKb = AppObjects.getValue(requestName + 
      		 ".maxMemorySizeInKb",this.sMaxMemorySizeInKb);
       return Integer.parseInt(lsMaxMemorySizeInKb) * 1000; 
   }
   private long getMaxRequestSizeInBytes(String requestName)
   {
       String lsMaxRequestSizeInKb = AppObjects.getValue(requestName + 
        		 ".maxRequestSizeInKb",this.sMaxRequestSizeInKb);
         return Long.parseLong(lsMaxRequestSizeInKb) * 1000; 
	   
   }
   protected Object executeRequestForHttpPart(String requestName
         ,HttpServletRequest request
         ,HttpServletResponse response
         ,HttpSession session
         ,Map inArgs)
         throws RequestExecutionException
   {
	   try
	   {
	      AppObjects.info(this,"Single file upload invoked");
		 //look at the form field name 
	     String fileuploadFieldName = AppObjects.getValue(requestName + 
	    		 ".fileUploadFormFieldName","filename");
	     
	     //Create a factory for disk-based file items
	     DiskFileItemFactory factory = new DiskFileItemFactory();
	
	     //Parse the request
	     factory.setSizeThreshold(this.getMaxMemorySizeInBytes(requestName));
	     factory.setRepository(new File(this.tempDirectory));
	     ServletFileUpload upload = new ServletFileUpload(factory);
	     upload.setFileSizeMax(this.getMaxRequestSizeInBytes(requestName));
	     List fileItems = upload.parseRequest(request);
	
	     Iterator iter = fileItems.iterator();
	     while (iter.hasNext()) 
	     {
	         FileItem item = (FileItem) iter.next();
	
	         if (item.isFormField()) 
	         {
	             processFormField(item, inArgs);
	         } 
	         else 
	         {
	             processFileItem(item, inArgs);
	         }
	     }//eof-while
	     
		 //Place the final set of args in a strings
	     inArgs.put("aspire_reserved_argument_map", this.convertMapToString(inArgs));
	     return new Boolean(true);
	   }
	   catch(FileUploadException x)
	   {
		   throw new RequestExecutionException("Parsing the uploaded file and the multi part request", x);
	   }
	   
   }//eof-function
   
   private void processFormField(FileItem fi, Map args)
   {
	   //form field name
	   String fieldname = fi.getFieldName();
	   String fieldvalue = fi.getString();
	   
	   //place field in the args
	   args.put(fieldname.toLowerCase(), fieldvalue);
   }
   
   private void processFileItem(FileItem fi, Map args)
   {
	   //form field name
	   String fieldname = fi.getFieldName();
	   String filename = getBasename(fi.getName());
	   long fileLength = fi.getSize();
	   
	   //Do not place file item in the hashtable
	   //Because we don't intend to use the file item later
	   //AppObjects.info(this,"Placing a file item for %1s into the hashtable", fieldname);
	   //args.put(fieldname.toLowerCase(), fi);
	   //when this file item is garbage collected the temp file will be deleted
	   
	   args.put(fieldname.toLowerCase() + "_filename", filename);
	   args.put(fieldname.toLowerCase() + "_filelength", Long.toString(fileLength));
	   args.put("aspire_reserved_filelength",Long.toString(fileLength));
	   args.put("aspire_reserved_filename", filename);
	   
	   //delete the file item
	   AppObjects.info(this, "Deleting the file item %1s", fi.getName());
	   fi.delete();
   }
   private String getBasename(String fullPathname)
   {
	   return FileUtils.basename(fullPathname);
   }
   private String getTempDirectory() throws ConfigException
   {
	   String localTempDirectory
      	= AppObjects.getValue(this.FILE_UPLOAD_CONTEXT + 
      		 ".tempDirectory",null);
	   
	   if (StringUtils.isValid(localTempDirectory) == true)
	   {
		   localTempDirectory = localTempDirectory.trim();
		   //it is a valid temp directory
		   String finalTempDirectory = 
			   FileUtils.removeTrailingSlashFromPath(localTempDirectory);
		   return finalTempDirectory;
	   }
	   //invalid temp directory
	   AppObjects.trace(this,"Going to get system temp directory.");
	   localTempDirectory = FileUtils.getSystemTempDirectory();
	   String finalTempDirectory = 
		   FileUtils.removeTrailingSlashFromPath(localTempDirectory);
	   return finalTempDirectory;
   }
   private String convertMapToString(Map argsMap)
   {
	   StringBuffer sb = new StringBuffer("");
	   for(Object key: argsMap.keySet())
	   {
		   Object o = argsMap.get(key);
		   if (o instanceof String)
		   {
			   sb.append("{" + key + ":" + (String)o + "} \n");
		   }
	   }
	   return sb.toString();
   }
  
}//eof-class
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

public class SingleFileUPloadCommonsPart extends AHttpPart implements IInitializable
{
   
   private String FILE_UPLOAD_CONTEXT = "aspire.global-defaults.fileupload";
   String sMaxMemorySizeInKb = "1000";
   String sMaxRequestSizeInMb = "10";
   String tempDirectory = "";

   public void initialize(String requestName) 
   {
	   fillDefaultSizes(requestName);
   }

   private void fillDefaultSizes(String requestName)
   {
	   try
	   {
		   sMaxMemorySizeInKb 
		          = AppObjects.getValue(this.FILE_UPLOAD_CONTEXT + 
		        		 ".MaxMemorySizeInKb","1000");
		   
		   sMaxRequestSizeInMb
	       = AppObjects.getValue(this.FILE_UPLOAD_CONTEXT + 
	     		 ".maxRequestSizeInMb","10");
		   
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
       String lsMaxRequestSizeInMb = AppObjects.getValue(requestName + 
        		 ".maxRequestSizeInMb",this.sMaxRequestSizeInMb);
         return Long.parseLong(lsMaxRequestSizeInMb) * 1000000; 
	   
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
	     }
	     return new Boolean(true);
	   }
	   catch(FileUploadException x)
	   {
		   throw new RequestExecutionException("Parsing the uploaded file", x);
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
	   //place file item in the hashtable
	   AppObjects.info(this,"Placing a file item for %1s into the hashtable", fieldname);
	   args.put(fieldname.toLowerCase(), fi);
	   args.put(fieldname.toLowerCase() + "_filename", filename);
	   args.put(fieldname.toLowerCase() + "_filelength", Long.toString(fileLength));
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
   
}//eof-class
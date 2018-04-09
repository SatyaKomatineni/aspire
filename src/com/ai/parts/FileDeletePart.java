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

import com.ai.application.interfaces.AFactoryPart;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.FileUtils;
import com.ai.common.SubstitutorUtils;

/**
 *
 * Additional Parameters
 * **********************
 * .targetDirectory - name of the directory. you can use substitutions
 * .filename - what to copy to. you can use substitutions
 * .fileuploadFormFieldName - name of the form field that was used to upload the file
 */
public class FileDeletePart extends AFactoryPart
{
   protected Object executeRequestForPart(String requestName
         ,Map inArgs)
         throws RequestExecutionException
   {
	   //read the directory
	   //read the filename
	   //get a combined filename by combining dir and filename
	   //locate fileitem for uploaded form field name
	   //copy the fileitem to the file
	   try
	   {
		   String targetFilename = this.getFilename(requestName, inArgs);
		   String mode=this.getFilemode(requestName, inArgs);
		   File file = new File(targetFilename);
		   if (file.exists() == true)
		   {
			   AppObjects.trace(this, "Deleting file:%1s", targetFilename);
			   file.delete();
		       return new Boolean(true);
		   }
		   AppObjects.trace(this, "File does not exist:%1s", targetFilename);
		   if (mode.equalsIgnoreCase("silent"))
		   {
			   //if mode is silent
			   AppObjects.trace(this, "File does not exist but mode is set to sielent:" + targetFilename);
		       return new Boolean(true);
		   }
		   //file does not exist
		   throw new RequestExecutionException("Error:Specified file does not exist to delete" + targetFilename);
	   }
	   catch(Exception x)
	   {
		   throw new RequestExecutionException("Problem in copying uploaded file",x);
	   }
   }//eof-function
   
   private String getFilename(String rname, Map args) throws ConfigException
   {
	   String filename = AppObjects.getValue(rname + ".filename");
	   String substitutedFilename = 
		   SubstitutorUtils.generalSubstitute(filename, args);
	   AppObjects.info(this, "target filename:%1s", substitutedFilename);
	   String tFilename = FileUtils.translateFileName(substitutedFilename);
	   AppObjects.info(this, "Translated  filename :%1s", tFilename);
	   return tFilename;
   }

   private String getFilemode(String rname, Map args) throws ConfigException
   {
	   String mode = AppObjects.getValue(rname + ".mode","silent");
	   return mode;
   }
}//eof-class
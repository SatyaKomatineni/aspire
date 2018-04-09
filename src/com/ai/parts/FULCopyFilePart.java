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
public class FULCopyFilePart extends AFactoryPart
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
		   String targetDir = getDirectory(requestName, inArgs);
		   validateTargetDir(targetDir);
		   String targetFilename = this.getFilename(requestName, inArgs);
		   String combinedFilename = this.getCombinedFilename(targetDir, targetFilename);
		   FileItem fi = this.locateFileItem(requestName, inArgs);
		   this.copyFile(fi, combinedFilename);
	       return new Boolean(true);
	   }
	   catch(Exception x)
	   {
		   throw new RequestExecutionException("Problem in copying uploaded file",x);
	   }
   }//eof-function
   
   private void validateTargetDir(String targetDirectory)
   throws RequestExecutionException
   {
	   File dir = new File(targetDirectory);
	   if (dir.exists() == true)
	   {
		   return;
	   }
	   AppObjects.trace(this,"Sorry the target directory does not exist:%1s", targetDirectory);
	   boolean b = dir.mkdirs();
	   if (b == false)
	   {
		   throw new 
		   RequestExecutionException("Error: Could not creat target directory:" 
				   + targetDirectory);
	   }
	   AppObjects.info(this,"target directory created:%1s", targetDirectory);
   }
   
   private String getDirectory(String rname, Map args) throws ConfigException
   {
	   String dirname = AppObjects.getValue(rname + ".targetDirectory");
	   String substitutedDirname = 
		   SubstitutorUtils.generalSubstitute(dirname, args);
	   String translatedDirname = 
		   FileUtils.translateFileName(substitutedDirname);
	   AppObjects.info(this, "target directory for file upload is:%1s", translatedDirname);
	   return translatedDirname;
   }
   private String getFilename(String rname, Map args) throws ConfigException
   {
	   String filename = AppObjects.getValue(rname + ".filename");
	   String substitutedFilename = 
		   SubstitutorUtils.generalSubstitute(filename, args);
	   AppObjects.info(this, "target filename for file upload is:%1s", substitutedFilename);
	   return substitutedFilename;
   }
   private String getCombinedFilename(String dir, String filename)
   {
	   return dir + File.separator + filename;
   }
   private void copyFile(FileItem fi, String combinedFilename) throws Exception
   {
	   AppObjects.info(this,"Copying %1s", combinedFilename);
	   fi.write(new File(combinedFilename));
   }
   
   private FileItem locateFileItem(String rname, Map args) throws ConfigException
   {
	   //get the field name for the file item
	   String fieldname = AppObjects.getValue(rname + ".fileuploadFormFieldName");
	   FileItem fi = (FileItem)args.get(fieldname.toLowerCase());
	   return fi;
   }
   
}//eof-class
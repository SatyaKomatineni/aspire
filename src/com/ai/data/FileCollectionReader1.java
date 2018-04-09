package com.ai.data;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;

import java.io.*;

import com.ai.common.FileUtils;
import com.ai.common.SubstitutorUtils;
import com.ai.common.Tokenizer;
/**
 * Based on FileCollectionReader1
 * uses a more correct ListDataCollection
 * it should accept the parameter from a file
 * should take care of the date
 * Semantics may be different from that of the first one.
 */
public class FileCollectionReader1 extends ADataCollectionProducer
{
   public IDataCollection execute(String taskName, Map arguments)
      throws RequestExecutionException
   {
      try
      {
      //Read the files and return
      
	   String dirname = AppObjects.getValue(taskName + ".directory");
	   String substitutedDirname = 
		   SubstitutorUtils.generalSubstitute(dirname, arguments);
	   String translatedDirname = 
		   FileUtils.translateFileName(substitutedDirname);
      
	   AppObjects.trace(this,
			   "Translated directory name for reading files:" + translatedDirname);
       return getFileCollection(translatedDirname);
      }
      catch(ConfigException x)
      {
         throw new RequestExecutionException("Error:Can't read directory",x);
      }
   }
   public IDataCollection getFileCollection(String dirName)
   {
      File dir = new File(dirName);
      String[] filesArray = dir.list();
      AppObjects.trace(this,"Number of files:%1s",filesArray.length);

      Vector metaFields = new Vector();
      metaFields.add("filename");
      metaFields.add("length");
      metaFields.add("date");
      ListDataCollection ldc = new ListDataCollection(metaFields);
      IMetaData vmd = ldc.getIMetaData();

      for (int i=0;i<filesArray.length;i++)
      {
         ListDataRow ldr = getFileRow(new File(filesArray[i]),vmd);
         ldc.addDataRow(ldr);
      }

      return ldc;

   }
   private ListDataRow getFileRow(File file, IMetaData imd)
   {
	   String filename = file.getName();
	   String length = Long.toString(file.length());
	   String cdate = "some date";
	   List rowList = new ArrayList();
	   rowList.add(filename);
	   rowList.add(length);
	   rowList.add(cdate);
	   
	   ListDataRow ldr = new ListDataRow(imd,rowList);
	   return ldr;
   }
}//eof-class

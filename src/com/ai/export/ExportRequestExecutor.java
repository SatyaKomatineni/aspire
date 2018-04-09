package com.ai.export;

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import java.io.*;
import com.ai.common.Tokenizer;
import com.ai.htmlgen.*;
import com.ai.xml.*;
import com.ai.common.*;

/**
 * To export an aspire data set to a file using a template file
 * inputs:
 *       1. FormDataset (or form handler name)
 *       2. template file
 *       3. transform to use
 *       4. A hash table of arguments
 *
 * Logic:
 *    Get a form handler
 *    Get the template
 *    Get the transform
 *    Get the printwriter
 *    Call the transform with the print writer
 *    Throw if there is an exception
 *
 * Property file entries
 *
 * request.name.className=com.ai.export.ExportRequestExecutor
 * request.name.templateFilename=<comma or tab separated file>
 * request.name.transform.className=com.ai.xml.AITransform2
 * // key : Aspire.Export.Filename
 * request.name.formHandlerObj.className=com.ai.htmlgen.DBHashtableFormHandler
 * request.name.formHandlerObj.mainDataRequest.className=com.ai.htmlgen.DBHashtableFormHandler
 * request.name.formHandlerObj.mainDataRequest.db=<mydb>
 * .. 
 * Rest of the standard redirect urls and failure redirect urls follow
 * 
 */
public class ExportRequestExecutor implements ICreator 
{
   public Object executeRequest(String requestName, Object args)
          throws RequestExecutionException
   {
      // Common resources that needs to be reclaimed
      // in the finally clause
      
      java.io.PrintWriter fileWriter = null;
      IFormHandler dataSet = null;      
      try 
      {
         // Get the data set first
         dataSet = 
         FormUtils.getFormHandlerFor(Utils.getTrueRequestString(requestName) 
                                       + ".formHandlerObj"
                                     ,(Hashtable)args);

         // Make sure there is a filename to write in it
         String filename = FileUtils.translateFileName(dataSet.getValue("aspire.export.filename"));
         if (filename == null)
         {
            AppObjects.log("Null filename");
            throw new RequestExecutionException("Export filename not found in the dataset");
         }
         if (filename.equals(""))
         {
            AppObjects.log("Empty filename string");
            throw new RequestExecutionException("Export filename not found in the dataset");
         }
         //Get the template file
         String templateFilename =
         AppObjects.getIConfig().getValue(requestName + ".templateFilename");

         //Get the transform         
         Object obj=TransformUtils.getTransformObject(requestName);
         if (!(obj instanceof IAITransform))
         {
            AppObjects.log("error: Wrong transformation in place");
            throw new RequestExecutionException("Wrong transformation in place for export request executor");
         }
         IAITransform transform = (IAITransform)obj;
         fileWriter = new PrintWriter( new FileOutputStream(filename) );

         // transform now
         transform.transform(FileUtils.translateFileName(templateFilename),fileWriter,dataSet);      
      }
      catch(ConfigException x)
      {
         throw new RequestExecutionException("Could not read the config file", x);
      }
      catch(java.io.IOException x)
      {
         throw new RequestExecutionException("An io error reported", x);
      }
      finally
      {
         if (fileWriter != null)
         {
            fileWriter.close();
         }
         if (dataSet != null)
         {
            dataSet.formProcessingComplete();
         }
      }         
      return new RequestExecutorResponse(true);
   }
} 
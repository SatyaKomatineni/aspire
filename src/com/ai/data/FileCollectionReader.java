package com.ai.data;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import java.io.*;
import com.ai.common.Tokenizer;

public class FileCollectionReader extends ADataCollectionProducer
{
   public IDataCollection execute(String taskName, Map arguments)
      throws RequestExecutionException
   {
      try
      {
      //Read the files and return
      String dirName = (String)arguments.get("directory");
      return getFileCollection(dirName);
      }
      catch(InvalidVectorDataCollection x)
      {
         throw new RequestExecutionException("Error:Can't read the directory",x);
      }
   }
   public static IDataCollection getFileCollection(String dirName)
         throws InvalidVectorDataCollection
   {
      File dir = new File(dirName);
      String[] filesArray = dir.list();

      Vector lineVector = new Vector();
      for (int i=0;i<filesArray.length;i++)
      {
         lineVector.add(filesArray[i]);
      }
      Vector metaFields = new Vector();
      metaFields.add("filename");
      return new VectorDataCollection( metaFields,lineVector );

   }
}

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.extensions.xmlconfig;

import java.io.*;
import com.ai.common.*;
import java.util.*;

public class PropertiesFileConfigSectionProcessorOutput implements IConfigSectionProcessorOutput
{
   PrintWriter pw = null;
   public PropertiesFileConfigSectionProcessorOutput(String filename) 
      throws FileNotFoundException
   {
      String renamedFilename = renameExtension(filename,"properties");
      OutputStream os = new FileOutputStream(renamedFilename);
      pw = new PrintWriter(os);
   }
   public void setValue(String key, String value)
   {
      pw.println(key + "=" + value);
   }
   public void println(String line)
   {
      pw.println(line);
   }
   public void close()
   {
      pw.flush();
      pw.close();
   }



   static public String renameExtension(String filename, String extname)
   {
      File f  = new File(filename);
      String basename = f.getName();
      Vector parts = Tokenizer.tokenize(basename,".");
      if (parts.size() == 0)
      {
         return filename + "." + extname;
      }
      String extension =  (String)parts.get(parts.size()-1);
      String  path = f.getParent();

      StringBuffer newFilename  = new StringBuffer();
      if (!(path.equals("")))
      {
         // path is not empty
         newFilename.append(path).append(File.separator);
      }
      
      // remove the extension 
      parts.removeElementAt(parts.size() -1);
      boolean bFirstTime = true;
      for (Enumeration e=parts.elements();e.hasMoreElements();)
      {
         String part = (String)e.nextElement();
         if (bFirstTime == true)
         {
            newFilename.append(part);
            bFirstTime = false;
         }
         else
         {
            newFilename.append("." + part);
         }
      }
      // append new extension
      return newFilename.append(".").append(extname).toString();
   }
   
} 
/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import com.ai.jawk.*;
import java.io.*;
import java.util.StringTokenizer;
import com.ai.application.utils.AppObjects;
import java.util.*;
import com.ai.application.interfaces.*;

public class FileUtils
{
   public static StringBuffer convertToStringBuffer(String filename )
         throws java.io.IOException
   {
      ConvertToStringBufferListener listener = new ConvertToStringBufferListener();
      runFileProcessorWith(filename,listener );
      return listener.getStringBuffer();
   }
   
   public static void runFileProcessorWithAFilename(String filename
	        ,IFileProcessorListener fpListener)
	throws java.io.IOException
	{
   		FileProcessor fp = new FileProcessor();
   		fp.addFileProcessorListener(fpListener);
   		fp.processFile(filename);
	}
   
   public static void runFileProcessorWithAResource(String filename
        ,IFileProcessorListener fpListener)
   	throws java.io.IOException
	{
		FileProcessor fp = new FileProcessor();
		fp.addFileProcessorListener(fpListener);
		InputStream stream = FileUtils.readResource(filename);
	    fp.processStream(stream);
	}
   /**
    * @deprecated use the specific call
    * @param filename
    * @param fpListener
    * @throws java.io.IOException
    */
   public static void runFileProcessorWith(String filename
                                          ,IFileProcessorListener fpListener)
         throws java.io.IOException
   {
   	  //If it is stdin let it go
   	  if (filename.equalsIgnoreCase("stdin"))
   	  {
   	  	runFileProcessorWithAFilename(filename,fpListener);
   	    return;
   	  }
   	  
   	  //If it is an existing file let it go
   	  File file = new File(filename);
   	  if (file.exists()== true)
   	  {
   	  	runFileProcessorWithAFilename(filename,fpListener);
   	  }
   	  else
   	  {
   	  	runFileProcessorWithAResource(filename,fpListener);
   	  }
   }

   /******************************************************************************
    * translateFilename (uses the default IConfig from the application )
    ******************************************************************************
    */
	static public String translateFileName( String fileName)
	{
		String relativeHtmlFilename  = fileName;
      String specifiedFileSeparator = AppObjects.getIConfig().getValue("directories.file_separator","\\");
      if (specifiedFileSeparator.equals(File.separator) == false)
      {
         relativeHtmlFilename = relativeHtmlFilename.replace(specifiedFileSeparator.charAt(0)
                                             ,File.separatorChar);
      }
      File relativeHtmlFile = new File(relativeHtmlFilename );
      if (relativeHtmlFile.exists())
      {
         return relativeHtmlFilename;
      }
      // Do the substitution
      StringTokenizer tokenizer = new StringTokenizer(relativeHtmlFilename,":");
      if (tokenizer.countTokens() < 2 )
      {
         return relativeHtmlFilename;
      }

      // There certainly are two tokens with : separator
      // first part is the root path
      // second part is the relative path
      String rootSpec = tokenizer.nextToken();
      String rootSpecTranslated = AppObjects.getIConfig().getValue("directories." + rootSpec ,null);
      if (rootSpecTranslated == null)
      {
         //Can't find this protocol in the context
         //return the file name untranslated
         AppObjects.warn("FileUtils","Filename not translated as the protocol is not found");
         return fileName;
      }
      else
      {
         return rootSpecTranslated + tokenizer.nextToken();
      }
	}
   /******************************************************************************
    * translateFilename (Uses a specified properties file)
    ******************************************************************************
    */
	static public String translateFileName( String fileName, Properties props)
	{
		String relativeHtmlFilename  = fileName;
      String specifiedFileSeparator = props.getProperty("directories.file_separator","\\");
      if (specifiedFileSeparator.equals(File.separator) == false)
      {
         relativeHtmlFilename = relativeHtmlFilename.replace(specifiedFileSeparator.charAt(0)
                                             ,File.separatorChar);
      }
      File relativeHtmlFile = new File(relativeHtmlFilename );
      if (relativeHtmlFile.exists())
      {
         return relativeHtmlFilename;
      }
      // Do the substitution
      StringTokenizer tokenizer = new StringTokenizer(relativeHtmlFilename,":");
      if (tokenizer.countTokens() < 2 )
      {
         return relativeHtmlFilename;
      }

      // There certainly are two tokens with : separator
      // first part is the root path
      // second part is the relative path
      String rootSpec = tokenizer.nextToken();
      String rootSpecTranslated = props.getProperty("directories." + rootSpec.toLowerCase() ,null);
      if (rootSpecTranslated == null)
      {
         //Can't find this protocol in the context
         //return the file name untranslated
         System.out.println("Filename not translated as the protocol is not found");
         return fileName;
      }
      else
      {
         return rootSpecTranslated + tokenizer.nextToken();
      }
	}

    /******************************************************************************
     * translateFilename (Uses a specified properties file)
     ******************************************************************************
     */
     static public String translateFileName( String fileName, IConfig config)
     {
         String relativeHtmlFilename  = fileName;
       String specifiedFileSeparator = config.getValue("directories.file_separator","\\");
       if (specifiedFileSeparator.equals(File.separator) == false)
       {
          relativeHtmlFilename = relativeHtmlFilename.replace(specifiedFileSeparator.charAt(0)
                                              ,File.separatorChar);
       }
       File relativeHtmlFile = new File(relativeHtmlFilename );
       if (relativeHtmlFile.exists())
       {
          return relativeHtmlFilename;
       }
       // Do the substitution
       StringTokenizer tokenizer = new StringTokenizer(relativeHtmlFilename,":");
       if (tokenizer.countTokens() < 2 )
       {
          return relativeHtmlFilename;
       }

       // There certainly are two tokens with : separator
       // first part is the root path
       // second part is the relative path
       String rootSpec = tokenizer.nextToken();
       String rootSpecTranslated = config.getValue("directories." + rootSpec.toLowerCase() ,null);
       if (rootSpecTranslated == null)
       {
          //Can't find this protocol in the context
          //return the file name untranslated
          System.out.println("FileUtils:Filename not translated as the protocol is not found");
          return fileName;
       }
       else
       {
          return rootSpecTranslated + tokenizer.nextToken();
       }
     }
     /**
      * Given the left hand side of a filename will return a
      * translated file name.
      */
	static public String translateFileIdentifier( String fileIdentifier )
		throws com.ai.application.interfaces.ConfigException
	{
		String relativeHtmlFilename  = AppObjects.getIConfig().getValue(fileIdentifier);
        return translateFileName(relativeHtmlFilename);
	}
	/**
	 * Given a properties file file identifier
	 * open a fle output stream.
	 * 
	 * @param fileIdentifier indicates the left hand side of a filename
	 */
	static public OutputStream getOutputStreamForFileIdentifier(String fileIdentifier)
	throws ConfigException, FileNotFoundException
	{
		//This filename will be an absolute path
		String filename = translateFileIdentifier(fileIdentifier);
		return new FileOutputStream(filename);
	}
	static public InputStream getInputStreamForFileIdentifier(String fileIdentifier)
	throws ConfigException, FileNotFoundException
	{
		//This filename will be an absolute path
		String filename = translateFileIdentifier(fileIdentifier);
		return new FileInputStream(filename);
	}
    static public InputStream readResource(String resourceName) throws IOException
    {
       ProtocolResource protocolResource = new ProtocolResource(resourceName);
       return protocolResource.getAsInputStream();
    }
   static public boolean copy(String sourceFilename, String targetFilename, boolean bCreateDirectory )
   {
      return copy(new File(sourceFilename), new File(targetFilename),bCreateDirectory );
   }
   /******************************************************************************
    * various copy functions
    ******************************************************************************
    */
   static public boolean copy(String sourceFilename, String targetFilename)
   {
      return copy(new File(sourceFilename), new File(targetFilename),false);
   }
   static public boolean copy(File sourceFile, File targetFile)
   {
      return copy(sourceFile,targetFile,false);
   }
   static public boolean copy(File sourceFile, File targetFile, boolean bCreateDirectory )
   {
      FileInputStream reader = null;
      FileOutputStream writer = null;

      try
      {
         if (bCreateDirectory == true)
         {
            File targetDir = targetFile.getParentFile();
            if (targetDir != null)
            {
               // see if it exists
               if (!targetDir.exists())
               {
                  targetDir.mkdirs();
               }
            }
         }
         reader = new FileInputStream(sourceFile);
         writer = new FileOutputStream(targetFile );
         byte byteArray[] = new byte[4092];
         while(true)
         {
            int numOfBytesRead = reader.read(byteArray,0,4092);
            if (numOfBytesRead == -1)
            {
               break;
            }
            // else
            writer.write(byteArray,0,numOfBytesRead);
         }
         return true;
      }
      catch(java.io.IOException x)
      {
         AppObjects.log("Could not copy file " + sourceFile.getAbsolutePath()
                        + " to " + targetFile.getAbsolutePath());
         AppObjects.log(x);
         return false;
      }
      finally
      {
         closeStream(reader);
         closeStream(writer);
      }
   }
   static public boolean closeStream(InputStream is)
   {
      try
      {
         if (is == null) return true;
         is.close();
         return true;
      }
      catch(java.io.IOException x)
      {
         AppObjects.log("Can not close an input stream:" + x.getMessage());
         AppObjects.log(x);
         return false;
      }
   }
   static public boolean closeStream(OutputStream os)
   {
      try
      {
         if (os == null) return true;
         os.close();
         return true;
      }
      catch(java.io.IOException x)
      {
         AppObjects.log("Can not close an output stream:" + x.getMessage());
         AppObjects.log(x);
         return false;
      }
   }
   static public void copy(InputStream reader, OutputStream writer)
      throws IOException
   {
        byte byteArray[] = new byte[4092];
         while(true)
         {
            int numOfBytesRead = reader.read(byteArray,0,4092);
            if (numOfBytesRead == -1)
            {
               break;
            }
            // else
            writer.write(byteArray,0,numOfBytesRead);
         }
         return;
   }
   /******************************************************************************
    * basename
    ******************************************************************************
    */
    static public String basename(String filename)
   {
      File inFile = new File(filename);
      return inFile.getName();
   }
   /******************************************************************************
    * getAbolutepath
    ******************************************************************************
    */
   static public String getAbsolutePath(String filename)
   {
      File f  = new File(filename);
      return f.getAbsolutePath();
   }
   /******************************************************************************
    * getFileExtension
    ******************************************************************************
    */
   static public String getFileExtension(String filename)
   {
      File f  = new File(filename);
      String basename = f.getName();
      Vector parts = Tokenizer.tokenize(basename,".");
      if (parts.size() == 0)
      {
         return null;
      }
      return (String)parts.get(parts.size()-1);
   }

   /******************************************************************************
    * renameExtension
    ******************************************************************************
    */
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

   /******************************************************************************
    * exists
    ******************************************************************************
    */
    static public boolean exists(String filename)
   {
      File inFile = new File(filename);
      return inFile.exists();
   }

   static public String readFile(String filename)
      throws FileNotFoundException, IOException
   {

      ByteArrayOutputStream baos = null;
      FileInputStream fis  = null;
      try
      {
         baos = new ByteArrayOutputStream();
         fis = new FileInputStream(filename);
         copy(fis,baos);
         return baos.toString();
      }
      finally
      {
         if (fis !=null)
         {
            FileUtils.closeStream(fis);
         }
         if (baos != null)
            com.ai.common.FileUtils.closeStream(baos);
      }
   }

   static public String readStreamAsString(InputStream is)
      throws FileNotFoundException, IOException
   {

      ByteArrayOutputStream baos = null;
      try
      {
         baos = new ByteArrayOutputStream();
         copy(is,baos);
         return baos.toString();
      }
      finally
      {
         if (is !=null)
         {
            FileUtils.closeStream(is);
         }
         if (baos != null)
            com.ai.common.FileUtils.closeStream(baos);
      }
   }
   public static String getSystemTempDirectory()
   {
	    String dir = System.getProperty("java.io.tmpdir");
	    return dir;
   }
   
   public static String removeTrailingSlashFromPath(String inDir)
   {
	   int len = inDir.length();
	   char lastchar = inDir.charAt(len-1);
	   if ((lastchar == '\\') 
			   || (lastchar == '/')
			   || (lastchar == File.separatorChar)
			   )
	   {
		   //there is a trailing slash
		   return inDir.substring(0,len-1);
	   }
	   //there is no trailing slash
	  return inDir;
   }//eof-function
   
   public static void main(String[] args)
   {
      String ins1 = "c:\\abc\\";
      String ins2 = "c:\\abc";
      String ins3 = FileUtils.getSystemTempDirectory();
      
      System.out.println(FileUtils.removeTrailingSlashFromPath(ins1));
      System.out.println(FileUtils.removeTrailingSlashFromPath(ins2));
      System.out.println("Temp dir:" + ins3);
      System.out.println(FileUtils.removeTrailingSlashFromPath(ins3));
   }
}//eof-class

class ConvertToStringBufferListener extends AFileProcessorListener
{
   StringBuffer buf = null;
    public void beginOfFile(BufferedReader reader )
    {
      buf = new StringBuffer();
    }
    public void newLine( final String line )
    {
      buf.append(line);
    }
    StringBuffer getStringBuffer()
    {
       return buf;
    }
}//eof-private class

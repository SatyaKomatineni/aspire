package com.ai.common.base;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Should not use any aspire specific functionality
 * Should stay general.
 * use exceptions and not logging
 */
public class BaseFileUtils 
{
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
	            is.close();
	         }
	         if (baos != null)
	            baos.close();
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
}//eof-class

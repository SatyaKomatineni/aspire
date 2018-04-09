package com.ai.common;
import java.util.*;
import java.io.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;

public class ProtocolResource
{
   private static boolean bDifferentFileSeparators = false;
   private static char cSpecifiedFileSeparatorChar = '\\';
   static
   {
      String specifiedFileSeparator = AppObjects.getIConfig().getValue("directories.file_separator","\\");
      cSpecifiedFileSeparatorChar = specifiedFileSeparator.charAt(0);
      if (specifiedFileSeparator.equals(File.separator) == false)
      {
         AppObjects.warn("ProtocolResource","Different file separators between development and deployment environments");
         bDifferentFileSeparators = true;
      }
	 }
   private String m_fullResourcename = null;
   private String m_protocol = null;
   private String m_resource = null;

   public ProtocolResource(String resourcename)
   {
      m_fullResourcename = resourcename;
      if (resourcename.indexOf(':') == -1)
      {
         //There is no protocol identifier
         m_protocol = null;
         m_resource = resourcename;
         return;
      }
      //Protocol identifier exists
      StringTokenizer tokenizer = new StringTokenizer(resourcename,":");
      m_protocol = tokenizer.nextToken();

      if (this.doesADirectoryAliasExist())
      {
         m_resource = tokenizer.nextToken();
         return;
      }

      if (this.isThisAKnownProtocol())
      {
         //this is a known protocol
         m_resource = tokenizer.nextToken();
         return;
      }
      //not a known protocol
      m_protocol = null;
      m_resource = m_fullResourcename;
   }

   public String getProtocol() { return m_protocol; }
   public String getResource() { return m_resource; }
   public String getFullResource() { return m_fullResourcename; }

   public InputStream getAsInputStream() throws IOException
   {
      // 1 No protocol specified, Return an input stream using that as a file path
      if (m_protocol == null)
      {
         return getInputStreamUsingFilePath(m_resource);
      }

      // 2 protocol specified, See if is a direcotry alias
      if (doesADirectoryAliasExist())
      {
         //directory alias exist
         return getInputStreamUsingADirectoryAlias();
      }

      // 3 Protocl specified. No directory alias exist, unknown protocol
      if (!(isThisAKnownProtocol()))
      {
         //Unknown protocol Assume it is a file reference
         AppObjects.error(this, "Unknown protocol. Assuming a file reference");
         return getInputStreamUsingFilePath(m_fullResourcename);
      }

      // 4 This is a known protcol
      return getInputStreamUsingAProtocolHandler();
   }

   private InputStream getInputStreamUsingAProtocolHandler() throws IOException
   {
      try
      {
       IResourceReader reader = (IResourceReader)AppObjects.getObjectAbsolute("aspire.resourcehandlers." + m_protocol,null);
       return reader.readResource(m_resource, AppObjects.getIConfig());
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("Error:Could not find resource handlers for protocol:" + m_protocol,x);
         throw new IOException(x.getRootCause());
      }
   }

   private InputStream getInputStreamUsingADirectoryAlias() throws IOException
   {
      String dirAlias = AppObjects.getValue("directories." + m_protocol,null);
      if (dirAlias == null)
      {
         throw new IOException("Error:Directory alias doesn't exist for protocol:" + m_protocol);
      }
      String filename = dirAlias + m_resource;
      return getInputStreamUsingFilePath(filename);
   }
   private boolean doesADirectoryAliasExist()
   {
      //See if it the protocol is in the directory references
      String dirAlias = AppObjects.getValue("directories." + m_protocol,null);
      if (dirAlias != null)
      {
         //dir alias exist
         //so it is a known protocol
         return true;
      }

      return false;
   }
   private boolean isThisAKnownProtocol()
   {
      //See if it the protocol is in the directory references
      String dirAlias = AppObjects.getValue("directories." + m_protocol,null);
      if (dirAlias != null)
      {
         //dir alias exist
         //so it is a known protocol
         return true;
      }

      //directory alias does not exist.
      //It may be a known protocol

      String protocolHandlerKey = "aspire.resourcehandlers." + m_protocol + ".classname";
      String protocolHandler = AppObjects.getValue(protocolHandlerKey,null);
      if (protocolHandler != null)
      {
         //protocol handler exist
         //so it is a known protocol
         return true;
      }
      //protocol handler is null
      //So it is an unknown protoco0l
      AppObjects.trace(this,"Unknown protocl:%1s", m_fullResourcename);
      return false;
   }
   private InputStream getInputStreamUsingFilePath(String filepath)
         throws IOException
   {
      File testFile = new File(filepath);
      if (testFile.exists())
      {
         //File found
         return new FileInputStream(filepath);
      }
      //File does not exist
      //Try the path replacement
      if (bDifferentFileSeparators == true)
      {
         filepath = filepath.replace(cSpecifiedFileSeparatorChar
                                             ,File.separatorChar);
         testFile = new File(filepath);
         if (testFile.exists())
         {
            return new FileInputStream(filepath);
         }
      }
      //Replaced path does not exist either
      throw new IOException("Error:Could not locate the file for :" + filepath);
   }
}
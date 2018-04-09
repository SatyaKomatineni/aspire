package com.ai.servlets;
import com.ai.application.interfaces.*;
import java.io.InputStream;
import java.io.IOException;
import com.ai.common.*;
import java.util.Vector;
import javax.servlet.*;
import com.ai.application.utils.AppObjects;

/**
 * This could be invoked from the configuration. Don't use sophisticated functionality here
 */
public class WebAppResourceReader implements IResourceReader
{
   public InputStream readResource(String resourceName, IConfig config) throws java.io.IOException
   {
      String newResource = null;
      if (resourceName.startsWith("/"))
      {
         newResource = resourceName;
      }
      else
      {
         Vector protocolResourcePair = Tokenizer.tokenize(resourceName,":");
         String protocol = (String)protocolResourcePair.elementAt(0);
         String resource = (String)protocolResourcePair.elementAt(1);
         newResource = resource.replace('\\','/');
      }

      InputStream is = ServletContextHolder.getServletContext().getResourceAsStream(newResource);
      if (is != null) return is;

      throw new IOException("Error:Could not read resource:" + newResource);
   }

}
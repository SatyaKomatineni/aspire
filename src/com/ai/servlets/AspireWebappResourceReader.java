package com.ai.servlets;
import com.ai.application.interfaces.*;
import java.io.InputStream;
import java.io.IOException;
import com.ai.common.*;
import java.util.Vector;
import javax.servlet.*;
import com.ai.application.utils.AppObjects;

/**
 * Only invoked after initialization
 * Input is a resource name without protocol
 */
public class AspireWebappResourceReader implements IResourceReader
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
         newResource = resourceName.replace('\\','/');
      }

      InputStream is = ServletContextHolder.getServletContext().getResourceAsStream(newResource);
      if (is != null) return is;

      throw new IOException("Error:Could not read resource:" + newResource);
   }

}
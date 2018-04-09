package com.ai.aspire.examples;
import com.ai.application.utils.*;
import com.ai.data.*;
import java.util.Vector;
import java.util.Hashtable;
import com.ai.application.interfaces.*;
import java.util.Enumeration;
import com.ai.htmlgen.*;
import java.util.*;
import com.ai.common.*;

/**
 * Creates a hello world japanese name page data (IFormHandler)
 * Creator is a singleton class maintained by the factory
 */
public class HWJPageDataCreator implements ICreator
{
   
   public Object executeRequest(String requestName, Object args )
                throws RequestExecutionException
   {
      try
      {
      // retrieve the form name
          Vector vArgs = (Vector)args;
           String formName = (String)vArgs.elementAt(0);
           
      // retrieve the hashtable of arguments
           Hashtable urlArguments = null;
           if (vArgs.size() > 1)
           {
              urlArguments = (Hashtable)(vArgs.elementAt(1));
           }
      // Creat your object of interest and return           
      // The return object should be of type com.ai.htmlgen.IFormHandler
           return createPageData(formName,urlArguments );
      }
      catch(DataException x)
      {
         throw new RequestExecutionException("Error: Could not create page data",x);
      }
                 
   }
   
   /**
    * DUpdateFormHandler is an implementation of IFormHandler
    * @see IFormHandler
    * @see IUpdatableFormHandler
    * @see IFormHandler1
    */
   private IFormHandler createPageData(String formName, Map arguments)
      throws DataException
   {
      
      DUpdateFormHandler m_pageData = new DUpdateFormHandler();
      m_pageData.addDictionary(new MapDictionary(arguments));
      // All keys are lower case
      m_pageData.addKey("hello_world","\u4eca\u65e5\u306f\u4e16\u754c");
      return m_pageData;
   }
} 

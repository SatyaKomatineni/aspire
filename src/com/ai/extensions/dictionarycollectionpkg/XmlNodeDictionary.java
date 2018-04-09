
package com.ai.extensions.dictionarycollectionpkg;

import com.ai.common.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import com.ai.xml.*;
import java.util.*;

//xml imports
import javax.xml.parsers.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.util.*;
import com.ai.application.interfaces.*;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.*;
import com.ai.common.*;

import com.ai.extensions.xmlconfig.*;

/**
 * Factory object
 * Not a singleton
 *
 * Mandatory arguments
 * ***********************
 * request.name.filename
 * request.name.xpathParentNodeList
 *
 */
public class XmlNodeDictionary extends MapDictionary implements ICreator, ISingleThreaded
{
   /**
    * Expected arguments: A map
    * 1. xml_filename_url
    */

    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
    {
      try
      {
         // read a filename
         String filename = FileUtils.translateFileIdentifier(requestName + ".filename");
//         String newFilename = SubstitutorUtils.generalSubstitute(filename,(Map)args);
         String newFilename = SubstitutorUtils.generalSubstitute(filename,(Map)args);
         String xpathList = AppObjects.getValue(requestName + ".xpathParentNodeList");
         initialize(newFilename, xpathList);
         return this;
      }
      catch(ConfigException x)
      {
         throw new RequestExecutionException("Error:Config error",x);
      }
      catch(javax.xml.transform.TransformerException x)
      {
         throw new RequestExecutionException("Error:XPath related error",x);
      }
    }

    public void initialize(String filename, String xpathList)
      throws javax.xml.transform.TransformerException
    {
      // make a dom
      // for each xpath collect the nodes
      Document dom = DOMUtils.getDOM(filename);
      List v = Tokenizer.tokenize(xpathList,"|");
      Iterator itr = v.iterator();
      while(itr.hasNext())
      {
         String xpath = (String)itr.next();
         collectChildrenForNode(dom,xpath);
      }

    }
    public void collectChildrenForNode(Document dom, String xpath)
      throws javax.xml.transform.TransformerException
    {
         AppObjects.log("Info:XmlNodeDictionary: working with node " + xpath);
         Node node = XPathAPI.selectSingleNode(DOMUtils.getRootNode(dom),xpath);
         DefaultConfigSectionProcessor p = new DefaultConfigSectionProcessor();
         p.processSection(node,new DictionaryCollector(this));
    }
}// end of class

class DictionaryCollector implements IConfigSectionProcessorOutput
{
   private IUpdatableDictionary m_d = null;
   public DictionaryCollector(IUpdatableDictionary dict)
   {
      m_d = dict;
   }
   public void setValue(String key, String value)
   {
      m_d.set(key,value);
   }
   public void println(String line)
   {
      // no implementation
   }
   public void close()
   {
      //no implementation
   }
}
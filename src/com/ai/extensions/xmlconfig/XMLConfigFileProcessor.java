/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.extensions.xmlconfig;
 
import com.ai.data.*;
import com.ai.jawk.*;
import com.ai.application.utils.*;
import com.ai.application.defaultpkg.ApplicationHolder;
import java.io.*;
import java.util.*;
import com.ai.common.*;
import com.ai.htmlgen.*;
import com.ai.application.defaultpkg.*;

//xml imports
import javax.xml.parsers.*;
import java.io.*;
import org.w3c.dom.*; 
import org.xml.sax.SAXException;

/**
 * Processes XML config files for aspire
 * 1)
 *    Read the xml file
 *    process each node
 *    get output
 * 2) Process each node
 *    getConfigNodeProcessor for each node
 *    handOver processing responsibilities
 * 3) There will be a default node processor
 *
 */
public class XMLConfigFileProcessor extends com.ai.common.ACommandLineApplication
{

   private String m_xmlconfigFilename = null;
   // Base class level obligations
   public XMLConfigFileProcessor(String[] args)
   {
      super(args);
   }
  protected int internalStart(String[] args)
  {
   try 
   {
         String propertyFilename = null;
         if (args.length >= 2)
         {
            propertyFilename = args[1];
         }
         else
         {
            propertyFilename = "g:\\secondary\\ai\\cbjava\\xmlconfig\\aspire.properties";
         }            
         System.out.println("Generating properties for the given xml file");

         // initialize aspire         
         ApplicationHolder.initApplication(propertyFilename,args);

         processXMLConfigFile(args[0]);
         return 0;
     }
     catch(FileNotFoundException x)
     {
         BaseSupport.log(x);
      return 0;
     }         
  }
  
  protected boolean  verifyArguments(String[] args)
  {
      if (args.length < 1)  return false;
      return true;
  }
  public static void main(String[] args)
  {
     // we need two arguments
    XMLConfigFileProcessor  app = new XMLConfigFileProcessor(args);
    app.start();
  }
  
  protected String getHelpString()
  {
      String line1 = "Command line:\t\t\t java com.ai.xmlconfig.XMLConfigFileProcessor [/h|/?] <xml file> <application property file>";
      String line2 = "\nxml config file: \t[mandatory]Absolute path of the xmlconfig filename.";
      String line3 = "\nProperty file: \t[optional] Absolute path of the property filename.";           
      return super.getHelpString() + "\n" + line1 + line2 + line3;
  }
/*
********************************************************
* Real work begins
********************************************************
*/
   static public void processXMLConfigFile(String filename)
      throws FileNotFoundException
   {
      Document dom = getDOM(filename);
      Node root = getRootNode(dom);
      List childElementNodes = getChildElementNodes(root);

      IConfigSectionProcessorOutput out = new PropertiesFileConfigSectionProcessorOutput(filename);
//      IConfigSectionProcessorOutput out = new DefaultConfigSectionProcessorOutput();
      try
      {
         for(int i=0;i<childElementNodes.size();i++)
         {
            Node en = (Node)childElementNodes.get(i);
            IConfigSectionProcessor csp = getSectionProcessor(en.getNodeName());
            csp.processSection(en,out);
         }
      }
      finally
      {
         if (out != null)
            out.close();
      }
      
   }
   static private IConfigSectionProcessor getSectionProcessor(String sectionName)
   {
//      if (sectionName.equalsIgnoreCase("popchartsDisplayUrl"))
//      {
//         return new com.ai.popcharts.xmlconfig.PopChartsDisplayURLProcessor();
//      }
      return new DefaultConfigSectionProcessor();
   }
   static private Node getRootNode(Document dom)
   {
      return dom.getDocumentElement();
   }
   static private List getChildElementNodes(Node domNode)
   {
      List onl = new ArrayList();
      NodeList nl  = domNode.getChildNodes();
      for (int i=0;i<nl.getLength();i++)
      {
         Node n = nl.item(i);
         if (n.getNodeType() == Node.ELEMENT_NODE)
         {
            onl.add(n);
            System.out.println(n.getNodeName());
         }
      }
      return onl;
   }
   static private Document getDOM(String filename)
   {
      try 
      { 
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder(); 
         Document doc = builder.parse(filename);
         return doc;
      }       
      catch(ParserConfigurationException x)
      {
         BaseSupport.log("Error", "XMLConfigFileProcessor:processxXMLConfigFile");
         BaseSupport.log(x);
      }  
      catch(IOException x)
      {
         BaseSupport.log("Error","XMLConfigFileProcessor:processxXMLConfigFile");
         BaseSupport.log(x);
      }                     
      catch(SAXException x)
      {
         BaseSupport.log("Error", "XMLConfigFileProcessor:processxXMLConfigFile");
         BaseSupport.log(x);
      }                     
      return null;
   }
} 



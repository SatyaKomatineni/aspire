/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.xml;

//xml imports
import javax.xml.parsers.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.util.*;
import com.ai.application.interfaces.*;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.*;
import com.ai.application.utils.*;

public class DOMUtils
{

    /**
     * get the root Node of the passed in Document
     * @param dom - Document
     * @return root node of the document
     */
   static public Node getRootNode(Document dom)
   {
      return dom.getDocumentElement();
   }

   public static List getChildElementNodes(Node domNode)
   {
      List onl = new ArrayList();
      NodeList nl  = domNode.getChildNodes();
      for (int i=0;i<nl.getLength();i++)
      {
         Node n = nl.item(i);
         if (n.getNodeType() == Node.ELEMENT_NODE)
         {
            onl.add(n);
         }
      }
      return onl;
   }
   public static Node getChildTextNode(Node domNode)
   {
      NodeList nl  = domNode.getChildNodes();
      for (int i=0;i<nl.getLength();i++)
      {
         Node n = nl.item(i);
         if (n.getNodeType() == Node.TEXT_NODE)
         {
            return n;
         }
      }
      return null;
   }
   public static String getAttributeValue(Node domNode, String atttribute, String defaultVal)
   {
      NamedNodeMap aNodes = domNode.getAttributes();
      Node  nameNode = aNodes.getNamedItem("name");
      if (nameNode == null) return defaultVal;
      return nameNode.getNodeValue();
   }
   public static String getChildValue(Node domNode, String childName, String defaultVal)
      throws TransformerException
   {
      Node n = XPathAPI.selectSingleNode(domNode,childName + "/text()");
      if (n == null)
      {
         return defaultVal;
      }
      return n.getNodeValue();
   }
   public static String getChildValue(Node domNode, String childName)
      throws ConfigException, TransformerException
   {
      Node n = XPathAPI.selectSingleNode(domNode,childName + "/text()");
      if (n == null)
      {
         throw new ConfigException("Expected child " + childName + " not found in " + domNode.getNodeName());
      }
      return n.getNodeValue();
   }

   static public Document getDOM(String filename)
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
         AppObjects.log("Error: XMLConfigFileProcessor:processxXMLConfigFile",x);
      }
      catch(IOException x)
      {
         AppObjects.log("Error: XMLConfigFileProcessor:processxXMLConfigFile",x);
      }
      catch(SAXException x)
      {
         AppObjects.log("Error: XMLConfigFileProcessor:processxXMLConfigFile",x);
      }
      return null;
   }

   public static Document createDocument(String url) throws ParserConfigurationException
                                                    ,IOException
                                                    ,org.xml.sax.SAXException
   {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document dom = db.parse(url);
      return dom;
   }

   public static Document replaceValueForNode(Document inDOM, String xpathAddress, String newValue)
           throws TransformerException
   {
        Node oldNode = XPathAPI.selectSingleNode(inDOM.getDocumentElement(),xpathAddress);
        Node textNode =   DOMUtils.getChildTextNode(oldNode);
        textNode.setNodeValue(newValue);
        return inDOM;
   }

}

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.extensions.xmlconfig;

//xml imports
import javax.xml.parsers.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.util.*;
import com.ai.xml.*;

public class DefaultConfigSectionProcessor extends AConfigSectionProcessor
{
  public void processSection(Node xmlNode, IConfigSectionProcessorOutput out)
  {
      String nodeName = xmlNode.getNodeName();
      String nameKey = DOMUtils.getAttributeValue(xmlNode,"name","");
      out.println("\n#**************************************************************************");
      out.println("# " + nodeName + "/" + nameKey);
      out.println("#**************************************************************************");
      walkTheNode("",xmlNode,out);
  }

  public void walkTheNode(String prefix,  Node xmlNode, IConfigSectionProcessorOutput out)
  {
      // see if I have any attributes
      String nodeName = xmlNode.getNodeName();

      String nameAttribute = null;
      boolean bAttributesExist = false;
      // does it have an attribute called name
      NamedNodeMap aNodes = xmlNode.getAttributes();
      if (aNodes != null)
      {
         if (aNodes.getLength() != 0)
         {
         // attribute nodes exist
            Node nameNode = aNodes.getNamedItem("name");
            if (nameNode != null)
            {
              nameAttribute = nameNode.getNodeValue();
            }
            bAttributesExist = true;
         }
      }

      StringBuffer lPrefix = new StringBuffer(prefix);
      if (prefix.length() != 0)
      {
         lPrefix.append(".");
      }
      if (nameAttribute != null)
      {
         lPrefix.append(nodeName).append(".").append(nameAttribute);
      }
      else
      {
         lPrefix.append(nodeName);
      }

      // add the attributes if they exist
      if (bAttributesExist)
      {
         for (int i=0;i<aNodes.getLength();i++)
         {
            Node an = aNodes.item(i);
            String aKey = an.getNodeName();
            String aValue = an.getNodeValue();
            if (!(aKey.equals("name")))
            {
               out.setValue(lPrefix + "." + aKey,aValue);
            }
         }
      }
      // process child nodes
      List nl = DOMUtils.getChildElementNodes(xmlNode);
      if(nl.size() == 0)
      {
         // no child elements
         // register its text
         Node text = DOMUtils.getChildTextNode(xmlNode);
         if (text == null) return;
         // text node exists
         out.setValue(lPrefix.toString(),text.getNodeValue());
         return;
      }
      // children exist
      for (int i=0;i<nl.size();i++)
      {
         Node en = (Node)nl.get(i);
         walkTheNode(lPrefix.toString(),en,out);
      }
  }


}

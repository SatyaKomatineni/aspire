/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.xml;

import java.util.*;

// xml imports
import org.w3c.dom.*;

import java.io.PrintWriter;
import javax.xml.parsers.*;

import java.io.*;

import com.ai.htmlgen.*;
import com.ai.data.*;
import com.ai.application.utils.AppObjects;
import com.ai.application.interfaces.*;
import com.ai.common.*;

// No member variables allowed unless you convert this class to
// an ISingleThreaded class.

public class FormHandlerToXMLTransform extends AFormHandlerTransform implements ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }

   public void transform(IFormHandler formHandler,PrintWriter writer)
    throws TransformException
   {
      try
      {
         // from the form handler get a DOM document
         Document doc = transformToXML((IFormHandler1)formHandler);
         XMLUtils.output(doc,writer);
//            writeDOMToOutput(writer,doc);
      }
      catch(ParserConfigurationException x)
      {
         AppObjects.log("Error: Could not create the DOM document",x);
         throw new TransformException("Error: XML parsing error",x);
      }
      catch(IOException x)
      {
         AppObjects.log("Error: IO exception",x);
         throw new TransformException("Error: IO Exception",x);
      }
   }

   private void writeDOMToOutput(PrintWriter out, Document doc) throws IOException, TransformException
   {
      XMLUtils.output(doc,out);
   }

  // xml stuff

   private static Document createDocument() throws ParserConfigurationException
   {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      return db.newDocument();
   }
   public static Document transformToXML(IFormHandler1 inForm ) throws ParserConfigurationException
   {

      Document doc = createDocument();

      Element root = doc.createElement("AspireDataset");
       try
       {
          IIterator keys = inForm.getKeys();
          for(keys.moveToFirst();!keys.isAtTheEnd();keys.moveToNext())
          {
            String key = (String)keys.getCurrentElement();
            String value = (String)inForm.getValue(key);
            root.appendChild(createKeyValueNode(doc,key,value));
          }

          // Go through each of the control handlers
          for(Enumeration cHandlers=inForm.getControlHandlerNames();cHandlers.hasMoreElements();)
          {
            String handlerName = (String)cHandlers.nextElement();
            try {
               IControlHandler1 handler = (IControlHandler1)inForm.getControlHandler(handlerName);
               root.appendChild(createControlHandlerNode(doc, handlerName, handler));
            }
            catch (com.ai.htmlgen.ControlHandlerException x) {
               AppObjects.log("Control handler not found for : " + handlerName );
               AppObjects.log(x);
               continue;
            }
          }
       }
       catch(com.ai.data.DataException x)
       {
         x.printStackTrace();
       }
      doc.appendChild(root);
      return doc;
   } // transformToXML

   /**
    * Create key and value as a tag and value pair
    */
   private static Node createKeyValueNode(Document doc, String key, String val)
   {
      Element tag = doc.createElement("key");

//      Attr attribute = doc.createAttribute("name");
//      attribute.setValue(key);

      tag.setAttribute("name",key);

      Text tagVal = doc.createTextNode(val);
      tag.appendChild(tagVal);
      return tag;
   }
   private static Node createControlHandlerNode(Document doc, String handlerName, IControlHandler1 cHandler)
   {
      AppObjects.log("xsl: Creating control handler for : " + handlerName );
      Element loopNode = doc.createElement("loop");
      loopNode.setAttribute("name",handlerName);
      try
      {
         IDataCollection dc = (IDataCollection)(cHandler.getDataCollection());
         IIterator itr = dc.getIIterator();
//         Element handlerE = doc.createElement("loop");
//         handlerE.setAttribute("name",handlerName);
         for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
         {
            Node rowNode = createRowNode(doc,(IDataRow)itr.getCurrentElement());
            loopNode.appendChild(rowNode);
         }
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log("xsl: Could not retrieve row data for : " + handlerName );
         AppObjects.log(x);
      }
      return loopNode;
   }
   private static Node createRowNode(Document doc, IDataRow row )
   {
      Element rowNode = doc.createElement("row");
      AppObjects.log("xsl: Creating a row ");
      try
      {
         // break up the row
         IIterator columnNames = row.getColumnNamesIterator();
         for(columnNames.moveToFirst();!columnNames.isAtTheEnd();columnNames.moveToNext())
         {
            String columnName = (String)columnNames.getCurrentElement();
            String columnValue = row.getValue(columnName);
            rowNode.appendChild(createKeyValueNode(doc,columnName, columnValue));
         }
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log("Could not retrieve column data ");
         AppObjects.log(x);
      }
      catch(com.ai.data.FieldNameNotFoundException x)
      {
         AppObjects.log( x );
         AppObjects.log("Could not find field name");
      }
      return rowNode;
   }

}

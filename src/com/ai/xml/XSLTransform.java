/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.xml;

import java.util.Enumeration;

// xml imports
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Element;

import java.io.PrintWriter;

// import org.apache.xerces.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import com.ai.htmlgen.IFormHandler1;
import com.ai.htmlgen.IControlHandler1;
import com.ai.data.IDataRow;
import com.ai.application.utils.AppObjects;
import com.ai.data.IDataCollection;
import com.ai.data.IIterator;
import com.ai.htmlgen.IFormHandler;

import com.ai.htmlgen.IAITransform;
import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;

// No member variables allowed unless you convert this class to
// an ISingleThreaded class.

public class XSLTransform implements IAITransform, ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }

   public void transform(String htmlFilename, PrintWriter writer, IFormHandler formHandler)
   throws java.io.IOException
   {
      try
      {
         // from the form handler get a DOM document
         Document doc = transformToXML((IFormHandler1)formHandler);
         Transformer xslt = getTransformer(htmlFilename);
         xslt.transform(new DOMSource(doc), new StreamResult(writer));
      }
      catch(TransformerException x)
      {
         AppObjects.log("Error: XSL Transformation failed",x);
      }
      catch(ParserConfigurationException x)
      {
         AppObjects.log("Error: Could not create the DOM document",x);
      }

      finally
      {
         formHandler.formProcessingComplete();
      }
   }

  // xml stuff

   private Document createDocument() throws ParserConfigurationException
   {
//      doc = new TXDocument();
//      return new DocumentImpl();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      return db.newDocument();
   }
   public Document transformToXML(IFormHandler1 inForm ) throws ParserConfigurationException
   {

      Document doc = createDocument();
//      ((TXDocument)doc).setVersion("1.0");

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
//      try{ ((TXDocument)doc).printWithFormat(new PrintWriter( System.out));}
//      catch(java.io.IOException x) { x.printStackTrace(); }
      return doc;
   } // transformToXML

   /**
    * Create key and value as a tag and value pair
    */
   Node createKeyValueNode(Document doc, String key, String val)
   {
      Element tag = doc.createElement(key);
      Text tagVal = doc.createTextNode(val);
      tag.appendChild(tagVal);
      return tag;
   }
   Node createControlHandlerNode(Document doc, String handlerName, IControlHandler1 cHandler)
   {
      AppObjects.log("xsl: Creating control handler for : " + handlerName );
      Element loopNode = doc.createElement(handlerName);
      try
      {
         IDataCollection dc = (IDataCollection)(cHandler.getDataCollection());
         IIterator itr = dc.getIIterator();
         Element handlerE = doc.createElement(handlerName);
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
   Node createRowNode(Document doc, IDataRow row )
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
            rowNode.appendChild(this.createKeyValueNode(doc,columnName, columnValue));
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

   private Transformer getTransformer(String xslFilename) throws TransformerConfigurationException
   {
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer t = tf.newTransformer(new StreamSource(xslFilename));
      return t;
   }
}

/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.xml;

// xml imports
import org.w3c.dom.Document;

import java.io.PrintWriter;

// import org.apache.xerces.dom.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import java.io.*;

import com.ai.application.utils.AppObjects;
import com.ai.htmlgen.IFormHandler;

import com.ai.htmlgen.*;
import com.ai.application.interfaces.*;
import com.ai.generictransforms.IhdsDOMConverter;
import com.ai.common.*;

// No member variables allowed unless you convert this class to
// an ISingleThreaded class.

public class CommonXSLTransform implements IAITransform, ICreator, ISingleThreaded, IInitializable
{
    private String m_requestName = null;

   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }

   public void initialize(String requestName)
   {
       m_requestName = requestName;
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
      catch(TransformException x)
      {
         AppObjects.log("Error: XSL Transformation failed. Throwing an IOExcpeiton in its place",x);
         throw new IOException("Error: XSL Transformation failed. Throwing an IOExcpeiton in its place");
      }
      catch(javax.xml.transform.TransformerConfigurationException x)
      {
          AppObjects.log("Error: XSL Transformation failed. Throwing an IOExcpeiton in its place",x);
         throw new IOException("Error: XSL Transformation failed. Throwing an IOExcpeiton in its place");
      }
      catch(javax.xml.transform.TransformerException x)
      {
          AppObjects.log("Error: XSL Transformation failed. Throwing an IOExcpeiton in its place",x);
         throw new IOException("Error: XSL Transformation failed. Throwing an IOExcpeiton in its place");
      }
   }

  // xml stuff

   public Document transformToXML(IFormHandler1 inForm ) throws TransformException
   {
       try
       {
           String domConverterRequestName = AppObjects.getValue(m_requestName + ".domConverterAbsoluteRequestName");
           AppObjects.log("Info:domconverter absolute request name=" + domConverterRequestName);
           IhdsDOMConverter domConverter = (IhdsDOMConverter)AppObjects.getObjectAbsolute(domConverterRequestName,null);
           Document doc = domConverter.convert(inForm);
           return doc;
       }
       catch(CommonException x)
       {
           throw new TransformException("Error:Aspire common exception",x);
       }
   } // transformToXML


   private Transformer getTransformer(String xslFilename) throws TransformerConfigurationException
   {
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer t = tf.newTransformer(new StreamSource(xslFilename));
      return t;
   }
}


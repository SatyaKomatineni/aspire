package com.ai.xml;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.application.interfaces.*;

import java.io.PrintWriter;
import com.ai.htmlgen.*;
import com.ai.servletutils.*;

import org.w3c.dom.*;
import java.io.*;
import com.ai.common.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XMLUtils
{
   public static void writeXML(PrintWriter out, String aspireURL, Hashtable parameters)
   {
      IConfig config = AppObjects.getIConfig();
      IFactory factory = AppObjects.getIFactory();

      //Get template file
      String templateHtml = null;
      try
      {
          // For the given URL get the html template file
          String url = aspireURL;

          if (url == null )
          {
             PrintUtils.writeCompleteMessage(out,"Parameter called 'url' is required");
             return;
          }
          // Get html template file for this url
          templateHtml = FileUtils.translateFileIdentifier(url);
          // Get a form handler that can handle this form
          String formHandlerName = AppObjects.getValue(url+".formHandlerName");
          IFormHandler formHandler =  FormUtils.getFormHandlerFor(formHandlerName
                                                                  ,parameters);
          Object trObj = getTransformObject(url);
          if (trObj instanceof IAITransform)
          {
            ((IAITransform)trObj).transform(templateHtml
                                          ,out
                                          ,formHandler);
          }
          else
          {
             PrintUtils.writeCompleteMessage(out,"Unsupported transform");
             return;
          }
      }
      catch( com.ai.application.interfaces.ConfigException x)
      {
         PrintUtils.writeException(out,x);
         AppObjects.log("Error: Configuration exception",x);
      }
      catch( com.ai.application.interfaces.RequestExecutionException x)
      {
         PrintUtils.writeException(out,x);
         AppObjects.log("Error: Request execution excpetion",x);
      }
      catch(java.io.IOException x)
      {
         PrintUtils.writeCompleteMessage(out,templateHtml + "not found");
         AppObjects.log("Error: IOException", x);
      }
   }

   public static Object getTransformObject(String url)
   {

      try
      {
         // See if there is a special transform for this object
         Object pageLevelTransform = AppObjects.getIFactory().getObject(url + ".transform", null);
         return pageLevelTransform;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("pd: Page level transformation not available");
         AppObjects.log("pd: Continuing with Application level transformation");
      }
      try
      {
         //See if you can locate a transformation object
         Object obj = AppObjects.getIFactory().getObject(IAITransform.GET_TRANSFORM_OBJECT,null);
         return obj;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("pd: Could not obtain the transform from the config file");
         AppObjects.log("pd: Using the default HtmlParser as the transformation object");
//         AppObjects.log(x);
         return new com.ai.xml.XSLTransform();
      }
   }

   static public void output(Document doc, Writer writer) throws IOException, TransformException
   {
      try
      {
         IXMLOutputter op = (IXMLOutputter)AppObjects.getIFactory().getObject(IXMLOutputter.NAME,null);
         op.output(doc,writer);
      }
      catch(RequestExecutionException x)
      {
         throw new TransformException("Error: could not write xml output",x);
      }
   }
   public static Document createDocument() throws ParserConfigurationException
   {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      return db.newDocument();
    }
   public static void transform(String xmlString, String xsltString, Writer outputWriter)
   throws TransformException
   {
   	try
	{
   		TransformerFactory tf = TransformerFactory.newInstance();
   		Transformer t = tf.newTransformer(new StreamSource(new StringReader(xsltString)));
   		t.transform(new StreamSource( new StringReader(xmlString)),new StreamResult(outputWriter));
	}
   	catch(TransformerConfigurationException x)
	{
   		throw new TransformException("Error:xslt not properly configured.",x);
	}
   	catch(TransformerException x)
	{
   		throw new TransformException("Error:xslt transformation error.",x);
	}
   }
   
   public static String transform(String xmlString, String xsltString)
   throws TransformException
   {
   	try
	{
   		TransformerFactory tf = TransformerFactory.newInstance();
   		Transformer t = tf.newTransformer(new StreamSource(new StringReader(xsltString)));
   		StringWriter outString = new StringWriter();
   		t.transform(new StreamSource( new StringReader(xmlString)),new StreamResult(outString));
   		return outString.toString();
	}
   	catch(TransformerConfigurationException x)
	{
   		throw new TransformException("Error:xslt not properly configured.",x);
	}
   	catch(TransformerException x)
	{
   		throw new TransformException("Error:xslt transformation error.",x);
	}
   }
}//eof-class

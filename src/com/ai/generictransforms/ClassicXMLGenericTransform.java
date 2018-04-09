package com.ai.generictransforms;
import com.ai.htmlgen.*;
import com.ai.common.TransformException;
import java.io.*;
import com.ai.data.*;
import javax.servlet.http.*;
import org.w3c.dom.*;
import com.ai.xml.XMLUtils;
import javax.xml.parsers.*;

/**
 * A transform to support debugging
 * Content-Type=application/vnd.ms-excel|Content-Disposition=filename=form.xls
 */
public class ClassicXMLGenericTransform
        extends AHttpGenericTransform
        implements IFormHandlerTransform, IhdsDOMConverter {

    // from AHttpGenericTransform
    protected String getDerivedHeaders(HttpServletRequest request)
    {
        return "Content-Type=text/xml";
    }
    //From IGenericTransform
    public void transform(ihds data, PrintWriter out) throws TransformException
    {
        try
        {
            Document doc = convert(data);
            XMLUtils.output(doc,out);
        }
        catch(java.io.IOException x)
        {
            throw new TransformException("Error:io exception",x);
        }
    }
    //From IFormHandlerTransform
    public void transform(IFormHandler data, PrintWriter out) throws TransformException
    {
        try
        {
            Document doc = convert(data);
            XMLUtils.output(doc,out);
        }
        catch(java.io.IOException x)
        {
            throw new TransformException("Error:io exception",x);
        }
    }
    //From IhdsDOMConverter
    public Document convert(ihds hds) throws TransformException
    {
        try
        {
            return ClassicXMLGenericTransformUtils.transformToXML(hds);
        }
        catch(DataException x)
        {
            throw new TransformException("Error:DataException",x);
        }
        catch(ParserConfigurationException x)
        {
            throw new TransformException("Error:ParserConfigurationError",x);
        }
    }
    public Document convert(IFormHandler data) throws TransformException
    {
        return convert((ihds)data);
    }

}
/**************************************************************************
 * ClassicXMLGenericTransformUtils
 **************************************************************************
 */
class ClassicXMLGenericTransformUtils
{
    public static Document transformToXML(ihds hds )
            throws DataException, ParserConfigurationException
    {
       Document doc = XMLUtils.createDocument();

       Element root = doc.createElement("AspireDataset");
       IMetaData colData = hds.getMetaData();
       IIterator keys = colData.getIterator();
       hds.moveToFirst();

       for(keys.moveToFirst();!keys.isAtTheEnd();keys.moveToNext())
       {
         String key = (String)keys.getCurrentElement();
         String value = hds.getValue(key);
         root.appendChild(createKeyValueNode(doc,key,value));
       }

       // Go through each of the control handlers
       IIterator childNames = hds.getChildNames();
       for(childNames.moveToFirst();!childNames.isAtTheEnd();childNames.moveToNext())
       {
         String handlerName = (String)childNames.getCurrentElement();
         ihds handler = hds.getChild(handlerName);
         root.appendChild(createControlHandlerNode(doc, handlerName, handler));
       }
       doc.appendChild(root);
       return doc;
    } // transformToXML

    /**
     * Create key and value as a tag and value pair
     */
    private static Node createKeyValueNode(Document doc, String key, String val)
            throws DataException
    {
       Element tag = doc.createElement("key");

//      Attr attribute = doc.createAttribute("name");
//      attribute.setValue(key);

       tag.setAttribute("name",key);

       Text tagVal = doc.createTextNode(val);
       tag.appendChild(tagVal);
       return tag;
    }
    private static Node createControlHandlerNode(Document doc, String handlerName, ihds cHandler)
            throws DataException
    {
       Element loopNode = doc.createElement("loop");
       loopNode.setAttribute("name",handlerName);
       for(cHandler.moveToFirst();!cHandler.isAtTheEnd();cHandler.moveToNext())
       {
             Node rowNode = createRowNode(doc,cHandler);
             loopNode.appendChild(rowNode);
       }
       return loopNode;
    }
    private static Node createRowNode(Document doc, ihds loopData)
            throws DataException
    {
      Element rowNode = doc.createElement("row");
      IIterator columnNames = loopData.getMetaData().getIterator();
      for(columnNames.moveToFirst();!columnNames.isAtTheEnd();columnNames.moveToNext())
      {
         String columnName = (String)columnNames.getCurrentElement();
         String columnValue = loopData.getValue(columnName);
         rowNode.appendChild(createKeyValueNode(doc,columnName, columnValue));
      }
      //See if there are any children
      IIterator children = loopData.getChildNames();
      for(children.moveToFirst();!children.isAtTheEnd();children.moveToNext())
      {
          String childLoopName =  (String)children.getCurrentElement();
          ihds childLoopData = loopData.getChild(childLoopName);
          Node childLoopNode = createControlHandlerNode(doc,childLoopName,childLoopData);
          rowNode.appendChild(childLoopNode);
      }
      return rowNode;
   }
}
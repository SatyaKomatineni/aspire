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
public class ObjectXMLGenericTransform
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
            return ObjectXMLGenericTransformUtils.transformToXML(hds);
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
    public static Document transformToXML(ihds hds )
    throws DataException, ParserConfigurationException
	{
    	return ObjectXMLGenericTransformUtils.transformToXML(hds);
	}

}//eof-class
/**************************************************************************
 * ClassicXMLGenericTransformUtils
 **************************************************************************
 */
class ObjectXMLGenericTransformUtils
{
    private static String getDataSetName(ihds hds)
    {
        String defaultDsn = "AspireDataSet";
        String dsn = hds.getValue("aspire_reserved_xml_dataset_name");
        if (dsn == null)
            return defaultDsn;
        if (dsn.equals(""))
            return defaultDsn;
        return dsn;
    }
    private static String getLoopItemXMLName(String loopName, ihds curParentRow)
    {
        String defaultLoopItemName = "row";
        String din = curParentRow.getValue("aspire_reserved_xml_" + loopName + "_itemname");
        if (din == null) return defaultLoopItemName;
        if (din.equals("")) return defaultLoopItemName;
        return din;
    }
    public static Document transformToXML(ihds hds )
            throws DataException, ParserConfigurationException
    {

       hds.moveToFirst();
       String dataSetName = getDataSetName(hds);

       Document doc = XMLUtils.createDocument();

       Element root = doc.createElement(dataSetName);
       IMetaData colData = hds.getMetaData();
       IIterator keys = colData.getIterator();

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
       Element tag = doc.createElement(key);
       Text tagVal = doc.createTextNode(val);
       tag.appendChild(tagVal);
       return tag;
    }
    private boolean isXML(String ins)
    {
       int lindex = ins.indexOf('<');
       if (lindex == -1)
       {
          //There is no xml
          return false;
       }
       //less than sign exists
       int gindex = ins.indexOf('>',lindex);
       if (gindex == -1)
       {
          //there is no trailing xml
          return false;
       }
       return true;
    }
    private static Node createControlHandlerNode(Document doc, String handlerName, ihds cHandler)
            throws DataException
    {
       Element loopNode = doc.createElement(handlerName);
       for(cHandler.moveToFirst();!cHandler.isAtTheEnd();cHandler.moveToNext())
       {
             Node rowNode = createRowNode(getLoopItemXMLName(handlerName,cHandler.getParent()),doc,cHandler);
             loopNode.appendChild(rowNode);
       }
       return loopNode;
    }
    private static Node createRowNode(String rowNodeName, Document doc, ihds loopData)
            throws DataException
    {
      Element rowNode = doc.createElement(rowNodeName);
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

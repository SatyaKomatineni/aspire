package com.ai.generictransforms;

import java.util.Iterator;
import java.util.Vector;

import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;
import com.ai.common.TransformException;
import com.ai.htmlgen.ihds;

public class GenericXSLTransformMP1 extends GenericXSLTransformMP
{

    protected String getXMLFieldValue(ihds data)
    throws TransformException
    {
    	StringBuffer targetXml = new StringBuffer();
    	targetXml.append("<AspireDataSet>");
    	String xmlFieldName = data.getValue("xmlSourceField");
    	String xsltArguments = data.getValue("xsltArguments");
    	targetXml.append("<arguments>");
    	if (xsltArguments != null)
    	{
        	Vector xsltArgumentVector = Tokenizer.tokenize(xsltArguments,",");
        	Iterator argItr = xsltArgumentVector.iterator();
	    	while(argItr.hasNext())
	    	{
	    		String thisarg = (String)argItr.next();
	    		String thisValue = data.getValue(thisarg);
	    		targetXml.append("<" + thisarg + ">" + thisValue + "</" + thisarg + ">");
	    	}
    	}
    	targetXml.append("</arguments>");
    	targetXml.append("<xmldata>");
    	String xmlFieldValue = data.getValue(xmlFieldName);
    	targetXml.append(xmlFieldValue);
    	targetXml.append("</xmldata>");
    	targetXml.append("</AspireDataSet>");
    	String targetXmlString = targetXml.toString();
    	AppObjects.info(this,"transformedxml:" + targetXmlString);
    	return targetXmlString;
    }
}

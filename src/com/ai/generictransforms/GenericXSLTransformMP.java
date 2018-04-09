package com.ai.generictransforms;
import com.ai.htmlgen.*;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.IUpdatableMap;
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
public class GenericXSLTransformMP
        extends AHttpGenericTransform
        implements IFormHandlerTransform, IhdsDOMConverter 
{
	private String m_requestName = null;
	private String m_xsltRequestName = null;
	private String m_transformRequestName = null;
	
    public void initialize(String requestName)
    {
    	try
		{
    		m_requestName = requestName;
    		m_xsltRequestName = AppObjects.getValue(requestName + ".xsltRequestName");
    		m_transformRequestName = AppObjects.getValue(requestName + ".transformRequestName");
    		super.initialize(requestName);
		}
    	catch(ConfigException x)
		{
    		throw new RuntimeException("Error:You need to specify xslt requestname",x);
		}
    }
    // from AHttpGenericTransform
    protected String getDerivedHeaders(HttpServletRequest request)
    {
        return "Content-Type=text/html";
    }
    //From IGenericTransform
    public void transform(ihds data, PrintWriter out) throws TransformException
    {
       	transformUsingField(data,out);
    }
    private boolean isXmlModeOn(ihds hds)
    {
    	String mode = hds.getValue("mode");
    	if (mode == null)
    	{
    		return false;
    	}
    	if (mode.equals(""))
    	{
    		return false;
    	}
    	return true;
    }
    private boolean transformUsingField(ihds data, PrintWriter out)
    throws TransformException
    {
    	try
		{
	    	String xmlFieldValue = this.getXMLFieldValue(data);
	    	if (isXmlModeOn(data))
	    	{
	    		out.print(xmlFieldValue);
	    		return true;
	    	}
	    		
	    	String transformedXMLValue = 
	    		XMLUtils.transform(xmlFieldValue,getXSLString(data));
	    	addXMLToHDS(transformedXMLValue, data);
	    	//transform data
	    	IGenericTransform transformObj = 
	    		(IGenericTransform)AppObjects.getObjectAbsolute(this.m_transformRequestName,null);
	    	transformObj.transform(data,out);
	    	return true;
		}
    	catch(DataException x)
		{
    		throw new TransformException("Error:Error getting the xslt string",x);
		}
    	catch(RequestExecutionException x)
		{
    		throw new TransformException("Error:Error getting the xslt string",x);
		}
    }
    protected String getXMLFieldValue(ihds data)
    throws TransformException
    {
    	String xmlFieldName = data.getValue("xmlSourceField");
    	if (xmlFieldName == "")
    	{
    		throw new TransformException("xmlSourceField is a required field");
    	}
    	String xmlFieldValue = data.getValue(xmlFieldName);
    	return xmlFieldValue;
    }
    private void addXMLToHDS(String xmlValue, ihds hds) 
    throws TransformException
    {
    	if (!(hds instanceof IUpdatableMap))
		{
			//This is not supported
    		throw new TransformException("Error: This release does not support this. Requires IUpdatableMap");
		}
    	//so far so good
    	IUpdatableMap map = (IUpdatableMap)hds;
    	map.addKey("transformedxmlvalue",xmlValue);
    	return;
    }
    private boolean transformUsingDoc(ihds data, PrintWriter out)
    {
    	return true;
    }
    
    private String getXSLString(ihds hds)
    throws RequestExecutionException, DataException
    {
    	return (String)FormUtils.execRequestUsingHDS(this.m_xsltRequestName,hds);
    }
    
    
    //From IFormHandlerTransform
    public void transform(IFormHandler data, PrintWriter out) throws TransformException
    {
    	if (data instanceof ihds)
    	{
    		this.transform((ihds)data,out);
    	}
    	else
    	{
    		throw new TransformException("Error:Not suppoerted at this time");
    	}
    }
    //From IhdsDOMConverter
    public Document convert(ihds hds) throws TransformException
    {
        try
        {
            return ObjectXMLGenericTransform.transformToXML(hds);
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
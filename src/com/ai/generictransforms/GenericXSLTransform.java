package com.ai.generictransforms;
import com.ai.htmlgen.*;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
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
public class GenericXSLTransform
        extends AHttpGenericTransform
        implements IFormHandlerTransform, IhdsDOMConverter 
{
	private String m_requestName = null;
	private String m_xsltRequestName = null;
	
    public void initialize(String requestName)
    {
    	try
		{
    		m_requestName = requestName;
    		m_xsltRequestName = AppObjects.getValue(requestName + ".xsltRequestName");
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
    private boolean transformUsingField(ihds data, PrintWriter out)
    throws TransformException
    {
    	try
		{
	    	String xmlFieldName = data.getValue("xmlSourceField");
	    	if (xmlFieldName == "")
	    	{
	    		//empty field
	    		return false;
	    	}
	    	String xmlFieldValue = data.getValue(xmlFieldName);
	    	XMLUtils.transform(xmlFieldValue,getXSLString(data),out);
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
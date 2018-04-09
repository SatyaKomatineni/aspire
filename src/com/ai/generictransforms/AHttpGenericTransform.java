package com.ai.generictransforms;

import com.ai.common.TransformException;
import java.io.*;
import javax.servlet.http.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
/**
 *  Provides multiple output formats for Aspire data set
 *  Outputs formats include:
 *  xml, tab-delimited, text, varieties of html formats
 *  public interface IFormHandlerTransform
 *  {
 *      public String getHeaders(HttpServletRequest request);
 *      public void transform(IFormHandler data, PrintWriter out) throws TransformException;
 *  }
*/


public abstract class AHttpGenericTransform implements IHttpGenericTransform, IInitializable
{

    private String m_headers = null;
    public AHttpGenericTransform() {
    }

    /**
     * Make sure the derived class gives this class a chance
     * @param requestName
     */
    public void initialize(String requestName)
    {
        //Read the content headers
        m_headers =   AppObjects.getValue(requestName + ".headers",null);
    }

    public String getHeaders(HttpServletRequest request)
    {
        //Give precedence to headers in the config file
        if (m_headers != null) return m_headers;

        //If the config file headers dont exist
        // use the headers from the derived classes
        String derivedHeaders = getDerivedHeaders(request);
        if (derivedHeaders != null) return derivedHeaders;

        return null;
    }
    protected String getDerivedHeaders(HttpServletRequest request)
    {
        return null;
    }
}

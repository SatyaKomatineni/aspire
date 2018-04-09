package com.ai.htmlgen;
import com.ai.common.TransformException;
import java.io.*;
import javax.servlet.http.*;

/**
 *  Provides multiple output formats for Aspire data set
 *  Outputs formats include:
 *  xml, tab-delimited, text, varieties of html formats
 */
public interface IFormHandlerTransform
{
    public String getHeaders(HttpServletRequest request);
    public void transform(IFormHandler data, PrintWriter out) throws TransformException;
}
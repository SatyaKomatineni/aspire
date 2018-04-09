package com.ai.servlets;
import javax.servlet.http.*;
import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.common.*;
import com.ai.application.utils.*;
import com.ai.htmlgen.IFormHandlerTransform;
import com.ai.generictransforms.IHttpGenericTransform;

public class DefaultSetHeaders implements ISetHeaders, ICreator
{
    public DefaultSetHeaders() {
    }

    /**
     * Responsible for setting the headers for DisplayServlet
     * @param displayURL
     * @param request
     * @param response
     * @throws AspireServletException
     */
    public void setHeaders(String displayUrl, HttpServletRequest request, HttpServletResponse response)
         throws AspireServletException
    {
        try
        {
           // Set expiration so that the document is always current
           response.setDateHeader("Expires",0);
            //see if there is a request aspire output format
            String aspireOutputFormatSpec = (String)request.getParameter("aspire_output_format");
            if (aspireOutputFormatSpec != null)
            {
                setHeadersForAspireOutputFormat(aspireOutputFormatSpec,request,response);
                AppObjects.info(this,"Headers set using generic data transforms for output spec %1s",aspireOutputFormatSpec);
                return;
            }

           AppObjects.info(this,"Setting headers for Aspire URL: %1s",displayUrl);

           // For backward compatibility set content type
           String contentType = AppObjects.getIConfig().getValue(displayUrl + ".contentType",null);
           if (contentType != null)
           {
              response.setContentType(contentType);
           }
           else
           {
              response.setContentType("text/html");
           }
           //set any additional headers
           String headersString = getHeadersStringFromURL(displayUrl);
           setHeadersOnResponse(headersString, response);
           return;
        }
        catch(RequestExecutionException x)
        {
            throw new AspireServletException("Error: Factory object creation error",x);
        }
    }

    private void setHeadersForAspireOutputFormat(String aspireOutputFormatSpec,
            HttpServletRequest request,
            HttpServletResponse response)
            throws RequestExecutionException
    {
        //Locate the formhandlertransform for the output format spec
        Object o = AppObjects.getObjectAbsolute("GenericTransform." + aspireOutputFormatSpec,null);
        String headersString = null;

        if (o instanceof IFormHandlerTransform)
        {
           IFormHandlerTransform ifht = (IFormHandlerTransform)o;
           headersString = ifht.getHeaders(request);
        }
        else if (o instanceof IHttpGenericTransform)
        {
           IHttpGenericTransform ifht = (IHttpGenericTransform)o;
           headersString = ifht.getHeaders(request);
        }
        if (headersString == null)
        {
            AppObjects.log("Warn: AspireGenericTransform has returned a null header string. using text/html as default");
            response.setContentType("text/html");
            return;
        }
        //On a valid header string set the headers
        setHeadersOnResponse(headersString,response);
    }
    private void setHeadersOnResponse(String headersString, HttpServletResponse response)
    {
        if (headersString == null)
        {
           return;
        }
        AppObjects.info(this,"Setting headers:%1s",headersString);
        // A valid header specification exists
        Vector headers = Tokenizer.tokenize(headersString,"|");
        for(Enumeration e=headers.elements();e.hasMoreElements();)
        {
           String singleHeaderString = (String)e.nextElement();
           Vector headerNameValuePair = StringUtils.splitAtFirst(singleHeaderString,'=');
           response.setHeader((String)headerNameValuePair.elementAt(0)
                    ,(String)headerNameValuePair.elementAt(1));
       }

    }
    private String getHeadersStringFromURL(String url)
    {
       String urlResponseHeaders = AppObjects.getIConfig().getValue(url + ".headers",null);
       String globalResponseHeaders = AppObjects.getIConfig().getValue(AspireConstants.RESPONSE_HEADERS,null);
       if (globalResponseHeaders == null)
       {
          // global response headers not available
          return urlResponseHeaders;
       }
       // global response headers not null and available
       if (urlResponseHeaders == null)
       {
          // no url headers
          // just return the global ones
          return globalResponseHeaders;
       }
       // both are available
       // append them and return: global first and urlnext
       return globalResponseHeaders.trim() + "|" + urlResponseHeaders.trim();
    }

    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
    {
        return this;
    }
}
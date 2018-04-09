package com.ai.servlets.tags;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.ai.application.utils.AppObjects;
import com.ai.common.IUpdatableMap;
import com.ai.servlets.AspireConstants;

public class AspireHDSCaptureTag extends BodyTagSupport
{
  private String captureAsName;

  public void setCaptureAsName(String inCaptureAsName)
  {
	AppObjects.trace(this,"Setting the capture name:%1s", inCaptureAsName);
    this.captureAsName = inCaptureAsName;
  }

  public void setBodyContent(BodyContent inBody)
  {
    super.setBodyContent(inBody);
    AppObjects.trace(this,"jspfragment:%1s",inBody.toString());
  }
  
  public int doAfterBody()
  {
	    try 
	    {    
	      BodyContent bodyContent = super.getBodyContent();
	      String      bodyString  = bodyContent.getString();
	      bodyContent.clear(); // empty buffer for next evaluation
	      
		  JspContext ctx = this.pageContext;
		  Object aspirePageData = ctx.getAttribute("Aspire.formHandler", PageContext.REQUEST_SCOPE);
		  if (aspirePageData == null)
		  {
			  AppObjects.trace(this,"Aspire.formHandler page data not found");
		  }
		  else
		  {
			  //IHDS available in the request
			  IUpdatableMap dataMap = (IUpdatableMap)aspirePageData;
			  dataMap.addKey(this.captureAsName.toLowerCase(), bodyString);
		  }
		  ctx.setAttribute(this.captureAsName, bodyString, PageContext.REQUEST_SCOPE);
          return SKIP_BODY;
	    }
	    catch (IOException e) 
	    {
	    	throw new RuntimeException("IOEXception",e);
	    } // end of catch
  }//eof-function
}//eof-class

/*
    <jsp-config>
        <taglib>
            <taglib-uri>/aic</taglib-uri>
            <taglib-location>/WEB-INF/capture-tags.tld</taglib-location>
        </taglib>
    </jsp-config>
    
    
<!DOCTYPE taglib
  PUBLIC "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN"
   "http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd">

    <!-- a tab library descriptor -->
<taglib xmlns="http://java.sun.com/JSP/TagLibraryDescriptor">
  <tlib-version>1.0</tlib-version>
  <jsp-version>1.2</jsp-version>
  <short-name>capture tags</short-name>

  <!-- this tag manipulates its body content by converting it to upper case
    -->
  <tag>
    <name>CaptureTag</name>
    <tag-class>com.ai.servlets.CaptureTag</tag-class>
    <body-content>JSP</body-content>
    <attribute>
      <name>captureAsName</name>
    </attribute>
  </tag>
  
  <tag>
    <name>HdsCaptureTag</name>
    <tag-class>com.ai.servlets.AspireHDSCaptureTag</tag-class>
    <body-content>JSP</body-content>
    <attribute>
      <name>captureAsName</name>
    </attribute>
  </tag>
</taglib>    
*/

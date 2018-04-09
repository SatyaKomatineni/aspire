package com.ai.masterpage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ai.application.utils.AppObjects;
import com.ai.aspire.utils.TransformUtils;
import com.ai.common.StringUtils;
import com.ai.common.TransformException;
import com.ai.htmlgen.DefaultPostTransform;
import com.ai.htmlgen.IAIPostTransform;
import com.ai.htmlgen.IAITransform;
import com.ai.htmlgen.IFormHandler;
import com.ai.htmlgen.IUpdatableFormHandler;
import com.ai.sm.capturetags.CaptureTagUtils;

public class MasterpagePostTransform implements IAIPostTransform 
{
	   public void transform( 
		        String htmlFilename    //template file, could be JSP
		        ,PrintWriter out	//may or may not be available
		        ,Object dataObject
		        ,Object transformObject //could be any type 
		        ,HttpServletRequest request
		        ,HttpServletResponse response
		       ) 
      throws java.io.IOException, ServletException
      {
		   try
		   {
			   realTransform(htmlFilename,out,dataObject,transformObject,request,response);
		   }
		   catch(Exception x)
		   {
			   AppObjects.log(x);
			   throw new ServletException(x);
		   }
      }
	   
	   public void realTransform( 
		        String htmlFilename    //template file, could be JSP
		        ,PrintWriter out	//may or may not be available
		        ,Object dataObject
		        ,Object transformObject //could be any type 
		        ,HttpServletRequest request
		        ,HttpServletResponse response
		       ) 
	   throws java.io.IOException, ServletException, TransformException
	   {
		   String masterPageUrl = this.getMasterPageUrl(dataObject);
		   if (masterPageUrl == null)
		   {
			   AppObjects.trace(this,"master page is not requested");
			   DefaultPostTransform.self.transform(
					   htmlFilename
					   , out
					   , dataObject
					   , transformObject
					   , request
					   , response);
			   return;
		   }
		   
		   AppObjects.trace(this,"masterpage transform indicated with masterpageurl:%1s", masterPageUrl);
		   
		   //get an inbetween writer
		   StringWriter intermediateWriter = new StringWriter();
		   
		   //transform onto the intermediate
		   AppObjects.trace(this,"Transforming the original");
		   ((IAITransform)transformObject).transform(htmlFilename
		            ,new PrintWriter(intermediateWriter)
		            ,(IFormHandler)dataObject);
		   
		   //break the intermediate into a hashtable
		   Map capturedMap = getCapturedMap(intermediateWriter);
		   //AppObjects.trace(this,"Get transformed map:");
		   
		   //transform using a master page onto the original out
		   AppObjects.trace(this,"master page transformation");
		   TransformUtils.transformHdsMp(masterPageUrl, out, 
				   (IFormHandler)dataObject, capturedMap);
	   }
	   
	   private Map getCapturedMap(StringWriter sout)
	   throws TransformException
	   {
		   try
		   {
			   StringBuffer sbuf = sout.getBuffer();
			   Map map = CaptureTagUtils.getMapFromString(sbuf.toString());
			   return map;
		   }
		   catch(Exception x)
		   {
			   throw new TransformException("Could not get a map out of the first transformation");
		   }
	   }
	   private String getMasterPageUrl(Object dataObject)
	   {
		   IFormHandler fh
		   = (IFormHandler)dataObject;
		   String url = fh.getValue("url");
		   String masterPageUrl = fh.getValue("aspire_masterpageurl");
		   if (StringUtils.isValid(masterPageUrl))
		   {
			   //gotten the masterpage url from incoming url
			   return masterPageUrl;
		   }
		   //master page url from the properties file
		   return AppObjects.getValue(url + ".masterpageUrl",null);
	   }
}//eof-class

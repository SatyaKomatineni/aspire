package com.ai.htmlgen;

import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Assumes the transform is an IAITransform 
 *
 */
public class DefaultPostTransform implements IAIPostTransform
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
		    ((IAITransform)transformObject).transform(htmlFilename
		            ,out
		            ,(IFormHandler)dataObject);
       }
	   
	   public static IAIPostTransform self = new DefaultPostTransform();  
}//eof-class

package com.ai.htmlgen;

import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IAIPostTransform 
{
	   public void transform( 
		        String htmlFilename    //template file, could be JSP
		        ,PrintWriter writer	//may or may not be available
		        ,Object dataObject
		        ,Object transformObject //could be any type 
		        ,HttpServletRequest request
		        ,HttpServletResponse response
		       ) 
		       throws java.io.IOException, ServletException;

}

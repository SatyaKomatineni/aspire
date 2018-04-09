/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.jsp;

import com.ai.htmlgen.*;
import javax.servlet.http.*;
import javax.servlet.*;
import com.ai.application.interfaces.*;

public class JSPIncludeTransform implements IAIHttpTransform, ICreator
{

   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
   public void transform( String htmlFilename
//                          ,PrintWriter writer
                          , IFormHandler formHandler
                          , HttpServletRequest request
                          , HttpServletResponse response
                          , RequestDispatcher dispatcher
                         ) throws java.io.IOException, ServletException
   {
      request.setAttribute("Aspire.formHandler",formHandler);
      dispatcher.include(request,response);
   }                         
} 

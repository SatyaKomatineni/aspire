/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import java.io.PrintWriter;
import javax.servlet.http.*;
import javax.servlet.*;

/**
 * Transform the templatefile onto an httpresponse utilizing the datastream represented by IFormHandler.
 * Unlike IAITransform this transformation targets directly the http response
 * The target for an IAITransform is the print writer
 */
public interface IAIHttpTransform {

   public static final String NAME = "AppObjects.httpTransform";
   public void transform( 
         String htmlFilename           //template file, could be JSP
//        ,PrintWriter writer
        , IFormHandler formHandler     //data
        , HttpServletRequest request
        , HttpServletResponse response
        , RequestDispatcher dispatcher //Dispatcher if forwarding is required
       ) 
       throws java.io.IOException, ServletException;
} 




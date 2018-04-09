/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * Redirect internal and external urls
 *
 * @param url        relative url to be redirected to.
 *                   Could be relative or absolute
 * @param request    Useful for determining absolute urls from relative urls
 * @param response   To do sendRedirect
 * @param out        Incase redirect is internal
 */
public interface IRedirector 
{
   static public String NAME=AspireConstants.PAGE_REDIRECTOR;
   public void redirect(final String url
                        , HttpServletRequest request
                        , HttpServletResponse response 
                        , ServletContext  servletContext)
      throws   java.net.MalformedURLException,
               java.io.IOException,
               ServletException,
               RedirectorException;
} 

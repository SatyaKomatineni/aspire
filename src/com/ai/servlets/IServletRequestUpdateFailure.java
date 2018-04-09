/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Hashtable;

/**
 * A strategy pattern for responding to update errors on a web form
 */
public interface IServletRequestUpdateFailure 
{
   public static final String NAME = AspireConstants.DEFAULT_OBJECTS_CONTEXT
                              + ".ServletRequestUpdateFailure";
   public void respondToFailure(HttpServletRequest request
                                ,HttpServletResponse response
                                ,ServletContext servletContext
                                ,Throwable t
                                ,Hashtable parameters
                                ,String requestName)
               throws AspireServletException, ServletException;
} 
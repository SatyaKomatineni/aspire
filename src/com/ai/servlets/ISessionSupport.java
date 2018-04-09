/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import javax.servlet.*;
import javax.servlet.http.*;


public interface ISessionSupport
{
   /**
    * returns a session object if possible or null otherwise.
    */
   public HttpSession getSession(HttpServletRequest request
                                , HttpServletResponse response)
         throws AspireServletException;

}

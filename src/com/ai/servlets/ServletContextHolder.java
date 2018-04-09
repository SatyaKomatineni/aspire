package com.ai.servlets;
import javax.servlet.*;

/**
 * Set by initialization
 */
public class ServletContextHolder
{
   private static ServletContext s_servletContext = null;
   public static void initialize(ServletContext ctx)
   {
      s_servletContext = ctx;
   }
   public static ServletContext getServletContext()
   {
      return s_servletContext;
   }
}
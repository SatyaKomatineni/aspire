package com.ai.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.utils.AppObjects;
import com.ai.aspire.authentication.AuthorizationException;
import com.ai.servletutils.PrintUtils;
import com.ai.servletutils.ServletUtils;

/**
 * @author Satya Komatineni Jan 21, 2006
 */
public class DefaultGlobalExceptionHandler1 implements IGlobalExceptionHandler
{

    /***************************************************************************
     * global exception support
     ***************************************************************************
     */
    public IOException dealWithIOException(IOException x
            ,String user
            ,HttpSession session
            ,Hashtable parameters
            ,PrintWriter out
            ,HttpServletRequest request
            ,HttpServletResponse response )
    {
        AppObjects.log("Error: IO Exception caught in BaseServlet for URI:" + request.getRequestURI(),x);
        PrintUtils.writeException(
                ServletUtils.getSuitablePrintWriter(request,response,out)
                ,x );
        return x;
    }
    public ServletException dealWithServletException(ServletException x
            ,String user
            ,HttpSession session
            ,Hashtable parameters
            ,PrintWriter out
            ,HttpServletRequest request
            ,HttpServletResponse response )
    {
        AppObjects.log("Error: Servlet Exception caught in BaseServlet for URI:" + request.getRequestURI(),x);
        PrintUtils.writeException(
                ServletUtils.getSuitablePrintWriter(request,response,out)
                ,x );
        return x;
    }
    
    public Throwable dealWithThrowable(Throwable t
            ,String user
            ,HttpSession session
            ,Hashtable parameters
            ,PrintWriter out
            ,HttpServletRequest request
            ,HttpServletResponse response )
    {
        AppObjects.log("Error: Throwable caught in BaseServlet for URI:" + request.getRequestURI(),t);
        if (t instanceof AuthorizationException)
        {
        	PrintUtils.writeAuthorizationException(
	                ServletUtils.getSuitablePrintWriter(request,response,out)
	                ,(AuthorizationException)t );
        }
        else
        {
	        PrintUtils.writeException(
	                ServletUtils.getSuitablePrintWriter(request,response,out)
	                ,t );
        }
        return null;
    }
}

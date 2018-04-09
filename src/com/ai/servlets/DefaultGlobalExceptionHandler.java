package com.ai.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.utils.AppObjects;
import com.ai.servletutils.PrintUtils;
import com.ai.servletutils.ServletUtils;

/**
 * @author Satya Komatineni Jan 21, 2006
 */
public class DefaultGlobalExceptionHandler implements IGlobalExceptionHandler
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
        PrintUtils.writeException(
                ServletUtils.getSuitablePrintWriter(request,response,out)
                ,t );
        return null;
    }
}

package com.ai.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.servlets.compatibility.ServletCompatibility;

/**
 * @author Satya Komatineni Jan 21, 2006
 */
public class GlobalExceptionHandler 
{
    private static IGlobalExceptionHandler igeh = null;
    static
    {
        try
        {
            igeh = (IGlobalExceptionHandler)AppObjects.getObject(IGlobalExceptionHandler.NAME,null);
            AppObjects.info("GlobalExceptionHandler", "Specified exception handler instantiated.");
        }
        catch(RequestExecutionException x)
        {
            igeh = new DefaultGlobalExceptionHandler1();
            AppObjects.info("GlobalExceptionHandler", "No global exception handler specified. Using the default.");
        }
    }

    /***************************************************************************
     * global exception support
     ***************************************************************************
     */
    public static IOException dealWithIOException(IOException x
            ,String user
            ,HttpSession session
            ,Hashtable parameters
            ,PrintWriter out
            ,HttpServletRequest request
            ,HttpServletResponse response )
    {
        logException(x,user,session,parameters,out,request,response);
        return igeh.dealWithIOException(x,user,session,parameters,out,request,response);
    }
    public static ServletException dealWithServletException(ServletException x
            ,String user
            ,HttpSession session
            ,Hashtable parameters
            ,PrintWriter out
            ,HttpServletRequest request
            ,HttpServletResponse response )
    {
        logException(x,user,session,parameters,out,request,response);
        return igeh.dealWithServletException(x,user,session,parameters,out,request,response);
    }
    public static Throwable dealWithThrowable(Throwable t
            ,String user
            ,HttpSession session
            ,Hashtable parameters
            ,PrintWriter out
            ,HttpServletRequest request
            ,HttpServletResponse response )
    {
        logException(t,user,session,parameters,out,request,response);
        return igeh.dealWithThrowable(t,user,session,parameters,out,request,response);
    }
    private static void logException(Throwable t
            ,String user
            ,HttpSession session
            ,Hashtable parameters
            ,PrintWriter out
            ,HttpServletRequest request
            ,HttpServletResponse response )
    {
    	//Log this exception with context
    	AppObjects.error("GlobalExceptionHandler", "\n********************");
    	AppObjects.error("GlobalExceptionHandler", "Serving URL:%s",ServletCompatibility.getRequestURL(request));
    	AppObjects.error("GlobalExceptionHandler", "User is: %s",user);
    	AppObjects.error("GlobalExceptionHandler", "Parameters are: %s",parameters);
    	AppObjects.log(AppObjects.LOG_ERROR,t);
    }
}

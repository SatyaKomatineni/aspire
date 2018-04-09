package com.ai.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Satya Komatineni Jan 21, 2006
 */
public interface IGlobalExceptionHandler 
{

    public static String NAME="IGlobalExceptionHandler";
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
            ,HttpServletResponse response );
    
    public  ServletException dealWithServletException(ServletException x
            ,String user
            ,HttpSession session
            ,Hashtable parameters
            ,PrintWriter out
            ,HttpServletRequest request
            ,HttpServletResponse response );
    
    public  Throwable dealWithThrowable(Throwable t
            ,String user
            ,HttpSession session
            ,Hashtable parameters
            ,PrintWriter out
            ,HttpServletRequest request
            ,HttpServletResponse response );
}

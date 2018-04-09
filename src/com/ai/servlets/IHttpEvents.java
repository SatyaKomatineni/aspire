package com.ai.servlets;

import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * usage:
 * *************
 * request.IHttpEvents.classname=<implementation>
 *
 * Example:
 * *************
 *
 * //Specify the primary event handler
 * request.IHttpEvents.classname=com.ai.servlets.HttpEventDistributor
 *
 * //Specify any number of event handlers
 * //Comment this out if there are none
 * //The event handlers will be called in that order
 * request.IHttpEvents.eventHandlerList=eventHandler1,eventHandler2
 *
 * // individual handlers
 *
 * //This handler will debug stuff
 * request.eventHandler1.classname=com.ai.servlets.DefaultHttpEvents
 *
 * //This handler will set the character encoding to UTF-8 if none specified
 * //Exclude this class if you want the servlet default encoding
 * //Specialize this class if you need more dynamic setting
 * request.eventHandler2.classname=com.ai.servlets.HttpRequestCharacterEncodingHandler
 * request.eventHandler2.encoding=UTF-8
 *
 * Related files
 * **************
 * IHttpEvents
 * DefaultHttpEvents
 * DefaultHttpEvents1
 * HttpEventDistributor
 * HttpRequestCharacterEncodingHandler
 * SWIHttpEvents
 *
 * Build needed
 * *********************
 * Build 19 or above
 *
 */
public interface IHttpEvents
{
   public static String NAME="IHttpEvents";
   public boolean applicationStart() throws AspireServletException;
   public boolean applicationStop() throws AspireServletException;
   public boolean sessionStart(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws AspireServletException;
   public boolean sessionStop() throws AspireServletException;
   public boolean beginRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException;
   public boolean endRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException;
   public boolean userLogin(String username, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws AspireServletException;
   public boolean userChange(String oldUser, String newUser, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws AspireServletException;
   //Added 2014
   public boolean beginAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
		           ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException; 
   public boolean endAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
           ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException; 

}
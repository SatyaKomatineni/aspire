package com.ai.aspire.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ai.servlets.AspireConstants;

/**
 * Take a request and response and implmment 
 * an http authenication method.
 * 
 * Return a valid user once authenticated.
 * Return a null if the user is not valid
 * 
 * I suppose you can use request URI to see if 
 * the URI is a public or a private URI
 * 
 * It is anticipated to have two types of methods at a high level
 * 
 * 1. Basic Auth
 * 2. Digest Auth
 * 
 * May be more or a variation of each
 *
 */
public interface IHttpAuthenticationMethod 
{
	public static String SELF 
	= AspireConstants.AUTHENTICATION_CONTEXT + ".httpAuthenticationMethodObject";
	
	public String getUserIfValid(HttpServletRequest request,
               HttpServletResponse response )
	throws AuthorizationException;
}

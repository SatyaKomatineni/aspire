package com.ai.aspire.authentication;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.servletutils.ServletUtils;

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
public class BaseAuthenticationMethod
implements IHttpAuthenticationMethod 
,IInitializable
{
	private IAuthentication2 authHandler = null;
	
	//Initialize the auth handler
	@Override
	public void initialize(String requestName) 
	{
		try
		{
			authHandler = (IAuthentication2)
				AppObjects.getObject(IAuthentication.NAME, null);
		}
		catch(RequestExecutionException x)
		{
			throw new RuntimeException("Not able to get authentication object",x);
		}
	}
	
	/*******************************************************************************
	 * String getUserIfValid(HttpServletRequest request )
	 * return null if the user authorization fails
	 * if no authorization is indicated then the user is returned as annonymous
	 *******************************************************************************
	 */
	public String getUserIfValid(HttpServletRequest request,
               HttpServletResponse response )
	throws AuthorizationException
	{
		AppObjects.info(this,"Inside Http Base Auth");
		String auth = request.getHeader("Authorization");
		String realm = authHandler.getRealm();
		AppObjects.info(this,"Authorization :%1s", auth);
		//set the header if the authroization for this url is missing
		if (auth == null)
		{
		   response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		   response.setHeader("WWW-authenticate","Basic realm=\"" + realm + "\"");
		   //Indicate that authenticated user is null
		   //This will in turn return a null session
		   //The null session inturn forces the base servlet to return immediately
		   //with the unauthorized code set above.
		   return null;
		}
	
	    String user = null;
	    boolean valid = false;
	    try
	    {
	       String userPassEncoded = auth.substring(6);
	       // decode the userPass
	       sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
	       String userPassDecoded = new String(dec.decodeBuffer(userPassEncoded));
	       AppObjects.secure(this,"Userid + password :%1s",userPassDecoded);
	       // Separate userid,  password
	       StringTokenizer tokenizer = new StringTokenizer(userPassDecoded,":");
	       if (!tokenizer.hasMoreTokens()){ return null; }
	       user = tokenizer.nextToken();
	       String password = tokenizer.nextToken();

	       // See if this is the valid user
	       valid = ServletUtils.verifyPassword(user,password);
	    }
	    catch(IOException x)
	    {
	       AppObjects.log(x);
	    }
	    catch (IndexOutOfBoundsException x)
	    {
	       AppObjects.log(x);
	    }
	    catch(AuthorizationException x)
	    {
	       AppObjects.error(this,"Could not authorize user");
	       AppObjects.log(x);
	    }
	    finally
	    {
	       // valid user
	       if (valid) return user;
	       // Invalid user
	       AppObjects.warn(this,"Invalid user");
	       response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	       response.setHeader("WWW-authenticate","Basic realm=\"" + realm + "\"");
	       return null;
	    }
	   }   // end-getUserIfValid
}//eof-class

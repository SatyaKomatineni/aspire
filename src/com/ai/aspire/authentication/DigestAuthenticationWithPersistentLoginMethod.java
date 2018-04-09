package com.ai.aspire.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.authentication.pls.PLSUtils;
import com.ai.aspire.authentication.pls.PersistentLoginSupport;
import com.ai.common.StringUtils;
import com.ai.servlets.DefaultSessionSupport2;
import com.ai.servletutils.ServletUtils;

/**
 * Take a request and response and implement 
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
 * See http://satyakomatineni.com/item/4559 for http digest
 * 
 * This extends it with persistent login support
 * 
 * Note: Consider using PLSUtils for cookie management
 * 
 * 6/17/17
 * ***********
 * @see PersistentLoginSupport 
 * @see IPersistentLoginSupport
 * @see DefaultSessionSupport2
 * @see PLSUtils
 */
public class DigestAuthenticationWithPersistentLoginMethod
implements IHttpCookieEnabledAuthenticationMethod 
,IInitializable
{
	//The singleton authentication and authroization 
	//handler for AKC
	private IAuthentication2 authHandler = null;
	private IPersistentLoginSupport plsHandler = null;
	
	//A frequently updated random number
	//from server to apply digest.
    public String nonce;
    //The code that updates nonce frequently
    public ScheduledExecutorService nonceRefreshExecutor;
    
    //possible client challenge QOP are
    //auth and authint
    //auth: just authentication
    //authint: authentication with body integrity
    //See http://satyakomatineni.com/item/4559 for details
    //QOP: stands for quality of protection
	private String authMethod = "auth";
	
	private String realm = null;
	
	/*******************************************************************************
	 * Persistent Login Support: PLS
	 *******************************************************************************
	 */
	
	private static final String RLK_COOKIE_NAME = "rlk";
	/*******************************************************************************
	 * Initialization
	 *******************************************************************************
	 */
	//Initialize the auth handler
	@Override
	public void initialize(String requestName) 
	{
		try {internalInitialize(requestName);}
		catch(Throwable x)	{
			AppObjects.error(this,"Failed to initialize DigestAuthenticationMethod");
			throw new RuntimeException("Failed to initialize DigestAuthenticationMethod",x);
		}
	}
	
	private void internalInitialize(String requestName)
	throws AuthorizationException
	{
        AppObjects.info(this,"Initialization begin.");
		//get an auth handler
		authHandler = getAuthHandler();
		plsHandler = authHandler.getPersistentLoginSupport();
		
		realm = authHandler.getRealm();
		
		//deal with nonce
        nonce = calculateNonce();
        
        nonceRefreshExecutor = Executors.newScheduledThreadPool(1);

        nonceRefreshExecutor.scheduleAtFixedRate(new Runnable() {

            public void run() {
                AppObjects.info(this,"Refreshing Nonce....");
                nonce = calculateNonce();
            }
        }, 1, 1, TimeUnit.MINUTES);
        
        AppObjects.info(this,"Initialization complete.");
	}

	private IAuthentication2 getAuthHandler()
	{
		try
		{
			return (IAuthentication2)
				AppObjects.getObject(IAuthentication.NAME, null);
		}
		catch(RequestExecutionException x)
		{
			throw new RuntimeException("Not able to get authentication object",x);
		}
		
	}
	
    public String calculateNonce() {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
        String fmtDate = f.format(d);
        Random rand = new Random(100000);
        Integer randomInt = rand.nextInt();
        return DigestUtils.md5Hex(fmtDate + randomInt.toString());
    }	
	/*******************************************************************************
	 * String getUserIfValid(HttpServletRequest request )
	 * return null if the user authorization fails
	 * A null user will force a return from the base servlet
	 * response will be altered witha  challenge if the user is null
	 *******************************************************************************
	 */
	public String getUserIfValid(HttpServletRequest request,
               HttpServletResponse response )
	throws AuthorizationException
	{
        String authHeader = request.getHeader("Authorization");
        
        //on empty header challenge a response and return a null user
        if (StringUtils.isEmpty(authHeader)) 
        {
        	AppObjects.info(this,"Empty auth header. Go for validation using Persistent login cookies");
        	//if the header is empty see if you can get the user
        	//from the persistent login
        	//If the user is not valid using PL then a null will be returned
        	return getUserUsingPLS(request, response);
        }
        
		//Header is present
        AppObjects.info(this,"authHeader:%1s", authHeader);
        if (!authHeader.startsWith("Digest"))
        {
        	//it doesn't startw with digest
        	AppObjects.error(this,"authHeader doesn't start with digest:%1s", authHeader);
        	challengeDigest(request,response,"Unsupported authorization header");
        	return null;
        }
        //It is a digest header 
        
        // parse the values of the Authentication header into a hashmap
        HashMap<String, String> headerValues = parseHeader(authHeader);
        
        String method = request.getMethod();
        String userName = headerValues.get("username");
        String password = authHandler.getPassword(userName);
        
        String ha1 = DigestUtils.md5Hex(userName + ":" + realm + ":" + password);

        String qop = headerValues.get("qop");

        String ha2;

        String reqURI = headerValues.get("uri");

        AppObjects.info(this,"Gathered headers and userids and passwords");
        if (StringUtils.isValid(qop) && qop.equals("auth-int")) 
        {
        	AppObjects.info(this,"Calculating based on auth-int qop");
        	String requestBody = readRequestBody1(request);
            String entityBodyMd5 = DigestUtils.md5Hex(requestBody);
            ha2 = DigestUtils.md5Hex(method + ":" + reqURI + ":" + entityBodyMd5);
        } 
        else 
        {
        	AppObjects.info(this,"Calculating based on auth qop");
            ha2 = DigestUtils.md5Hex(method + ":" + reqURI);
        }

        AppObjects.info(this,"Going to calculate server response to compare to the client");
        String serverResponse;

        if (StringUtils.isEmpty(qop)) 
        {
            serverResponse = DigestUtils.md5Hex(ha1 + ":" + nonce + ":" + ha2);
        } 
        else 
        {
            String domain = headerValues.get("realm");
            String nonceCount = headerValues.get("nc");
            String clientNonce = headerValues.get("cnonce");
            serverResponse = DigestUtils.md5Hex(ha1 + ":" + nonce + ":"
                    + nonceCount + ":" + clientNonce + ":" + qop + ":" + ha2);

        }
        String clientResponse = headerValues.get("response");

        if (!serverResponse.equals(clientResponse)) 
        {
        	//Problem they don't match
        	challengeDigest(request, response,null);
        	return null;
        }
        AppObjects.info(this,"Digest Auth Successful! Going to return the user");
        //They all matched. this is a good user
        
        AppObjects.info(this,"Going to issue a new persistent login token if warranted");
        //See if this user wants to use persistent logins
        registerUserForPeristentLogin(userName, request, response);
        return userName;
	}
	
	/**
	 * If the user has asked for register user for
	 * persistent login.
	 *  
	 */
	private void registerUserForPeristentLogin(String username, 
			HttpServletRequest request,
            HttpServletResponse response )
	{
		boolean f = plsHandler.isPersistentLoginRequested(username);
		if (f == false)
		{
			//no the user did not request for persisten login
			return;
		}
		//User did request
		AppObjects.info(this, "User %1s requested a persisten login", username);
		String rlk = createRLKForUser(username);
		setRLKCookie(response, rlk);
		plsHandler.setRandomLoginKeyForUserFirstTime(username, rlk);
	}
	
	private String getPassword(String username)
	throws AuthorizationException
	{
		if (StringUtils.isEmpty(username))
		{
			throw new AuthorizationException("Username is emapty!");
		}
		//username is good
		return authHandler.getPassword(username);
	}
	private void challengeDigest(HttpServletRequest request,
            HttpServletResponse response,
            String optionalErrorMessage)
	{
		try
		{
			AppObjects.info(this, "Challenging for http digest");
	        response.addHeader("WWW-Authenticate", getAuthenticateHeader());
	        if (optionalErrorMessage == null) {
	        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        } 
	        else {
	        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED,optionalErrorMessage);
	        }
		}
		catch(IOException x)
		{
			AppObjects.error(this,"not able to set digest header on response");
		}
	}
    private String getAuthenticateHeader() {
        String header = "";

        header += "Digest realm=\"" + realm + "\",";
        if (StringUtils.isValid(authMethod)) {
            header += "qop=" + authMethod + ",";
        }
        header += "nonce=\"" + nonce + "\",";
        header += "opaque=\"" + getOpaque(realm, nonce) + "\"";

        return header;
    }
    /**
     * Opaque is a string that is sent to the client from the server.
     * It comes right back unchanged like a boomerang.
     * this is used to understand the context in which the challenge
     * is sent to the client! In a sense it could keep the state of the 
     * server when the nonce was sent.
     * 
     * Not sure what sending a nonce again here does for security!!
     * Something I need to mull over! 
     */
    private String getOpaque(String domain, String nonce) {
        return DigestUtils.md5Hex(domain + nonce);
    }    
    /**
     * This is needed when the QOP is authint
     * when this happens the entire body of the http request is 
     * scrambled as MD5
     */
    
    private String readRequestBody1(HttpServletRequest request) 
    throws AuthorizationException
    {
    	try { return readRequestBody(request); }
    	catch(IOException x)
    	{
    		throw new AuthorizationException("IO Exception from reading the request",x);
    	}
    }
    private String readRequestBody(HttpServletRequest request) 
    throws IOException 
    {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(
                        inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        String body = stringBuilder.toString();
        return body;
    }
    /**
     * Gets the Authorization header string minus the "AuthType" and returns a
     * hashMap of keys and values
     */
    private HashMap<String, String> parseHeader(String headerString) {
        // seperte out the part of the string which tells you which Auth scheme is it
        String headerStringWithoutScheme = headerString.substring(headerString.indexOf(" ") + 1).trim();
        HashMap<String, String> values = new HashMap<String, String>();
        String keyValueArray[] = headerStringWithoutScheme.split(",");
        for (String keyval : keyValueArray) {
            if (keyval.contains("=")) {
                String key = keyval.substring(0, keyval.indexOf("="));
                String value = keyval.substring(keyval.indexOf("=") + 1);
                values.put(key.trim(), value.replaceAll("\"", "").trim());
            }
        }
        return values;
    }
    
    //************************************************************
    //* Persisten login methods
    //* PLS: Persisten Login Support
    //* 
    //************************************************************
    
    private String getUserUsingPLS(HttpServletRequest request,
            HttpServletResponse response )
    {
    	//you are here because of no auth header
    	//Look for the RLK: Random Login Key
    	String rlk = getRLKCookie(request);
    	if (StringUtils.isEmpty(rlk))
    	{
    		//No header, no login key: challenge
    		AppObjects.info(this,"No auth header, No key, Challenge user and return null");
    		challengeDigest(request, response, null);
    		return null;
    	}
    	
    	//Ok the rlk exists
    	AppObjects.info(this,"rlk present: %1s. Going to get the user for it.",rlk);
    	String username = getUserForRLK(rlk);
    	if (username == null)
    	{
    		AppObjects.info(this, "No user for this rlk");
    		challengeDigest(request, response, null);
    		return null;
    	}
    	//user is located for the rlk
    	//Allow user to login
    	//But issue a different rlk for that user
    	AppObjects.info(this,"Located the user for RLK: %1s. Create a new RLK now",username);
    	String newrlk = createRLKForUser(username);
    	
    	//6/17/17: Looks like this happens everytime a user logs in
    	//So the randomkeys can grow in number!
    	AppObjects.info(this,"Set the new RLK for this user %1s",username);
    	plsHandler.replaceRandomLoginKeyForUser(username, rlk, newrlk);
    	
    	AppObjects.info(this,"Set the RLK cookie for user %1s",username);
    	setRLKCookie(response, newrlk);
    	
    	AppObjects.info(this,"Returning user %1s from the PL branch",username);
    	//finally return the user
    	return username;
    }
    
    private String getRLKCookie(HttpServletRequest request)
    {
    	//This cookie could be null
    	Cookie c = ServletUtils.getCookie(RLK_COOKIE_NAME, request);
    	if (c == null)
    	{
    		AppObjects.info(this,"RLK cookie is not available");
    		return null;
    	}
    	else
   		{
    		AppObjects.info(this,"RLK cookie is available");
    		return c.getValue();
   		}
    }
    
    //Can return a null user
    //Locate the user in the database for this RLK
    private String getUserForRLK(String rlk)
    {
    	if (StringUtils.isEmpty(rlk))
    	{
    		AppObjects.warn(this, "Random login key is empty for me to look it up in the database");
    		return null;
    	}
    	//rlk is available
    	String username = plsHandler.getUserForRandomLoginKey(rlk);
    	
    	//username can be null because 
    	//a) this key stale
    	//b) someone spuriously sent it as a middle man attack
    	
    	AppObjects.info(this,"User %1s found for rlk %2s", username, rlk);
    	return username;
    }
    
    private String createRLKForUser(String username)
    {
        Random rand = new Random();
        Long l = rand.nextLong();
        String sl = l.toString();
        return username + ":" + sl;
    }
    
	private void setRLKCookie(HttpServletResponse response, String rlk)
	{
    	//set the cookie on the response
		AppObjects.info(this, "Setting RLK cookie: %1s", rlk);
		
		//Create a 3 month cookie
		Cookie pc = createPersistentCookie(RLK_COOKIE_NAME,rlk);
		
		int m3 = 3 * 30 * 24 * 60 * 60;
		pc.setMaxAge(m3);
    	response.addCookie(pc);
	}

	private Cookie createPersistentCookie(String name, String value)
	{
		Cookie pc = new Cookie(name, value);
		pc.setPath("/");
		return pc;
	}
	private void cancelRLKCookie(HttpServletResponse response)
	{
    	//set the cookie on the response
		AppObjects.info(this, "Deleting the RLL cookie");
		
		//Create a 3 month cookie
		Cookie pc = createPersistentCookie(RLK_COOKIE_NAME,"");
		//value of 0 deletes the cookie
		pc.setMaxAge(0);
    	response.addCookie(pc);
	}
	/**
	 * This method is useful when you want to allow a 
	 * login purely based on a persistent cookie. 
	 * this method does not challenge for a password.
	 * 
	 * Merely returns a null user if user is not located.
	 * It is upto the caller to challenge if needed.
	 * 
	 * if a good user is returned that user wil be accepted
	 * and joined to the current session.
	 */
	@Override
	public String getUserFromCookieIfValid(HttpServletRequest request,
			HttpServletResponse response) 
	throws AuthorizationException 
	{
		AppObjects.info(this, "Figuring out if the cookie has a valid user");
		String rlk = getRLKCookie(request);
		if (rlk == null)
		{
			AppObjects.info(this,"No cookie available. returning null user");
			return null;
		}
		AppObjects.info(this,"RLK cookie is avaialble: %1s", rlk);
		String username = getUserForRLK(rlk);
		if (username == null)
		{
			//No user for this cookie
			AppObjects.info(this,"No user for this cookie %1s. Going to cancel it and return null", rlk);
			cancelRLKCookie(response);
			return null;
		}
		AppObjects.info(this,"located user %1s for cookie %2s", username, rlk);
		//does the user want a cookies
		if (!plsHandler.isPersistentLoginRequested(username) == true)
		{
			//Persistent login is not requested
			AppObjects.info(this,"persistent login is not requested for user %1s",username);
			cancelRLKCookie(response);
			return null;
		}
		
		//want a persistent login
		AppObjects.info(this, "Persistent login in place for user %1s",username);
		issueANewCookieLogin(username, response,rlk);
		return username;
	}
	
	private void issueANewCookieLogin(String username, HttpServletResponse response, String oldrlk)
	{
		String newrlk = createRLKForUser(username);
		AppObjects.info(this,"Issue an RLK cookie %1s to the client and register it on the server for user %2s", newrlk, username);
		setRLKCookie(response, newrlk);
		plsHandler.replaceRandomLoginKeyForUser(username, oldrlk, newrlk);
	}
	
}//eof-class

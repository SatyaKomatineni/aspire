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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import com.ai.application.interfaces.IInitializable;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.StringUtils;

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
 */
public class DigestAuthenticationMethod
implements IHttpAuthenticationMethod 
,IInitializable
{
	//The singleton authentication and authroization 
	//handler for AKC
	private IAuthentication2 authHandler = null;
	
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
	
	//Initialize the auth handler
	@Override
	public void initialize(String requestName) 
	{
		try {internalInitialize(requestName);}
		catch(Exception x)	{
			throw new RuntimeException("Failed to initialize DigestAuthenticationMethod",x);
		}
	}
	
	private void internalInitialize(String requestName)
	throws AuthorizationException
	{
		//get an auth handler
		authHandler = getAuthHandler();
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
        AppObjects.info(this,"initialziation complete");
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
        	AppObjects.info(this,"Empty auth header");
           challengeDigest(request, response, null);
           return null;
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
        
        AppObjects.info(this, "userid, password obtained");
        String ha1 = DigestUtils.md5Hex(userName + ":" + realm + ":" + password);

        String qop = headerValues.get("qop");

        String ha2;

        String reqURI = headerValues.get("uri");

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

        AppObjects.info(this,"Calculating server response");
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
            AppObjects.info(this,"Digest auth did not match. Going to challenge again");
        	challengeDigest(request, response,null);
        	return null;
        }
        AppObjects.info(this,"Digest Auth Successful! Returning username");
        //They all matched
        return userName;
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
}//eof-class

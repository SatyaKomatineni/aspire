package com.ai.aspire.authentication;

import com.ai.aspire.authentication.pls.PersistentLoginSupport;
import com.ai.servlets.AspireConstants;

/**
 * 
 * 6/15/2017
 * ****************
 * @author satya
 * Document each method as to what one should expect
 * who would call etc.
 * 
 * @see PersistentLoginSupport
 * @see DigestAuthenticationWithPersistentLoginMethod
 * 
 * Previously
 * ***************
 * 6/1/2014
 * Original code
 * 
 */
public interface IPersistentLoginSupport 
{
	public static final String SELF = AspireConstants.AC_PERSISTENT_LOGIN_SUPPORT_OBJECT;
	
	public boolean 	isPersistentLoginRequested(String username);
	
	//Set the key for the first time
	public void  	setRandomLoginKeyForUserFirstTime(String username, String randomKey);
	
	//As user logs in multiple times, each time a new key is generated
	//Replace the old key with the new key so that the keys are not
	//growing continuously.
	public void  	replaceRandomLoginKeyForUser(String username
												, String randomKey
												, String withRandomKey);
	
	public String 	getUserForRandomLoginKey(String randomKey);
	
	public void clearAllUsers();
	public void requestPersistentLoginFor(String username);
	public void clearPersistentLoginFor(String username);
}

package com.ai.aspire.authentication.pls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.ai.application.utils.AppObjects;
import com.ai.aspire.authentication.DigestAuthenticationWithPersistentLoginMethod;
import com.ai.aspire.authentication.IPersistentLoginSupport;

/**
 * 
 * Previously
 * *****************
 * This will be a singleton class.
 * Initial implementation will be memory based.
 * A future one will move it to the database.
 * 
 * 6/16/17
 * *************
 * @see DigestAuthenticationWithPersistentLoginMethod
 * The above class is a driver
 *
 * The following class uses a true persistence model
 * @see PersistentLoginSupport2
 * 
 */
public class PersistentLoginSupport 
implements IPersistentLoginSupport 
{

	//A list of registered users
	//A user is placed here if a persistent login is requested
	private ArrayList<String> registeredUserList = new ArrayList<String>();
	
	//HashMap<RandomLoginKey, User>
	private HashMap<String, String> registeredUserKeys = new HashMap<String, String>();
	
	@Override
	public boolean isPersistentLoginRequested(String username) 
	{
		if (registeredUserList.contains(username))
		{
			return true;
		}
		return false;
	}

	/**
	 * It is possible to have multiple random keys for the same users
	 * for different machines I suppose.
	 */
	@Override
	public void setRandomLoginKeyForUserFirstTime(String username, String randomKey) 
	{
		AppObjects.info(this,"Setting RLK %1s for user %2s", randomKey, username);
		registeredUserKeys.put(randomKey, username);
	}
	
	//As user logs in multiple times, each time a new key is generated
	//Replace the old key with the new key so that the keys are not
	//growing continuously.
	@Override
	public void	replaceRandomLoginKeyForUser(String username
												, String randomKey
												, String withRandomKey)
	{
		//For now, as this class is going to be deprecated
		//I will just call the other method
		setRandomLoginKeyForUserFirstTime(username, withRandomKey);
	}
	

	@Override
	public String getUserForRandomLoginKey(String randomKey) 
	{
		return registeredUserKeys.get(randomKey);
	}

	@Override
	public void clearAllUsers() 
	{
		registeredUserList.clear();
		registeredUserKeys.clear();
	}

	@Override
	public void requestPersistentLoginFor(String username) 
	{
		AppObjects.info(this,"Setting persistent login for %1s", username);
		registeredUserList.add(username);
	}

	/**
	 * when you clear a persistent login for a user remove all the keys
	 * for that user.
	 */
	@Override
	public void clearPersistentLoginFor(String username) 
	{
		AppObjects.info(this,"Removing persistent login for %1s", username);
		registeredUserList.remove(username);
		
		AppObjects.info(this,"Going to remove all the keys for the user %1s", username);
		Iterator<String> keyItr = registeredUserKeys.keySet().iterator();
		while(keyItr.hasNext())
		{
			String curKey = keyItr.next();
			String curUser = registeredUserKeys.get(curKey);
			if (curUser.equals(username))
			{
				AppObjects.info(this,"Removing user %1s with key %2s", username, curKey);
				keyItr.remove();
			}
		}
	}

}//eof-class

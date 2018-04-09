package com.ai.aspire.authentication.pls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.ai.application.interfaces.IInitializable;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.authentication.DigestAuthenticationWithPersistentLoginMethod;
import com.ai.aspire.authentication.IPersistentLoginSupport;

/**
 * 
 * 6/16/17
 * *************
 * The above class is a driver
 * 
 * Improves upon prev by being synchronized
 * Future versions may optimize synchronization
 * 
 * Key enhancements
 * **********************
 * 1. Synchronization
 * 2. Persistence to a file
 * 
 * Sync approach
 * **********************
 * Just make public methods synchronized for now
 *
 * High level Design
 * Change PLSPersistentLoginSupport2 class so that
 * 
 * 1: It is synchronized across multiple logins and threads
 * 
 * 2: Every state change invokes a save to a local drive of the state of persistent logns
 * 
 * 3: state is kept in a hashmap. This state is converted to a list first and streamed as xml to the local drive
 * 
 * 4: jaxb with annotations is used to save the resulting xml
 * 
 * 5: FileUtils.translateFileIdentifier() is used to get access to the filename needed to read and write from
 * 
 * 6: No changes to the underlying protocol of digest based logins 
 * 
 * 
 * Previously
 * *****************
 * This will be a singleton class.
 * Initial implementation will be memory based.
 * A future one will move it to the database.
 * 
 * @see DigestAuthenticationWithPersistentLoginMethod
 * @see LoggedInUserKeysDataset
 * @see PersistentLoginSupport
 * @see IPersistentLoginSupport
 * @see IPLSDataSetPersistence
 * @see PLSDataSetFilePersistence
 */
public class PersistentLoginSupport2 
implements IPersistentLoginSupport, IInitializable
{
	
	/*
	 * **************************************
	 * Static helper classes
	 * This class is used to save and retrieve
	 * **************************************
	 */
	private static IPLSDataSetPersistence dbObject =
			(IPLSDataSetPersistence)
			AppObjects.getImplementation(IPLSDataSetPersistence.SELF);

	/*
	 * **************************************
	 * Initialization
	 * **************************************
	 */
	@Override
	public void initialize(String requestName) {
		//Read the previous login state
		//as it may have been persisted to a disk or a db
		AppObjects.info(this, "Recovering login state during initialization");
		this.recoverState();
	}
	/*
	 * **************************************
	 * Private data
	 * **************************************
	 */
	
	//A list of registered users
	//A user is placed here if a persistent login is requested
	private ArrayList<String> registeredUserList = new ArrayList<String>();
	
	//HashMap<RandomLoginKey, User>
	private HashMap<String, String> registeredUserKeys = new HashMap<String, String>();
	
	/*
	 * **************************************
	 * Main functionality: Read methods
	 * **************************************
	 */
	//read method
	@Override
	synchronized public boolean isPersistentLoginRequested(String username) 
	{
		if (registeredUserList.contains(username))
		{
			return true;
		}
		return false;
	}
	
	@Override
	synchronized
	public String getUserForRandomLoginKey(String randomKey) 
	{
		return registeredUserKeys.get(randomKey);
	}

	/*
	 * **************************************
	 * Main functionality: Write methods
	 * **************************************
	 */
	/**
	 * It is possible to have multiple random keys for the same users
	 * for different machines I suppose.
	 * 
	 * update method. state changes
	 */
	@Override
	synchronized 
	public void setRandomLoginKeyForUserFirstTime(String username, String randomKey) 
	{
		AppObjects.info(this,"Setting RLK %1s for user %2s", randomKey, username);
		registeredUserKeys.put(randomKey, username);
		saveState();
	}
	
	//As user logs in multiple times, each time a new key is generated
	//Replace the old key with the new key so that the keys are not
	//growing continuously.
	@Override
	synchronized
	public void	replaceRandomLoginKeyForUser(String username
												, String oldKey
												, String newKey)
	{
		AppObjects.info(this,"Going to remove a key %1s for the user %2s with %3s"
				, oldKey
				, username
				, newKey);
		
		Iterator<String> keyItr = registeredUserKeys.keySet().iterator();
		while(keyItr.hasNext())
		{
			String curKey = keyItr.next();
			if (curKey.equals(oldKey))
			{
				AppObjects.info(this,"Removing user %1s with key %2s", username, curKey);
				keyItr.remove();
			}
		}
		//if present old keys are removed
		//insert the new key
		registeredUserKeys.put(newKey, username);
		saveState();
	}
	

	@Override
	synchronized
	public void clearAllUsers() 
	{
		registeredUserList.clear();
		registeredUserKeys.clear();
		saveState();
	}

	@Override
	synchronized
	public void requestPersistentLoginFor(String username) 
	{
		AppObjects.info(this,"Setting persistent login for %1s", username);
		registeredUserList.add(username);
		saveState();
	}

	/**
	 * when you clear a persistent login for a user remove all the keys
	 * for that user.
	 */
	@Override
	synchronized
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
		saveState();
	}

	/**
	 * ***********State Management***********
	 * 
	 * Save the state of the user keys
	 * For now use a local file
	 * This is usually called from a synchronized method
	 * 
	 * **************************************
	 */
	private void saveState()
	{
		LoggedInUserKeysDataset data = getUserDataSet();
		dbObject.save(data);
	}
	private PLSUserDetail getUserDetailFor(String username)
	{
		PLSUserDetail ud = new PLSUserDetail(username);
		for(String key:this.registeredUserKeys.keySet())
		{
			//key is the random key
			String uname = registeredUserKeys.get(key);
			if (uname == null) continue;
			//uname is there
			ud.addRandomKey(key);
		}
		//All keys are added
		return ud;
	}
	private LoggedInUserKeysDataset getUserDataSet()
	{
		LoggedInUserKeysDataset data = new LoggedInUserKeysDataset();
		for(String username:this.registeredUserList)
		{
			//for each user get the detail
			PLSUserDetail ud = getUserDetailFor(username);
			data.addUserDetail(ud);
		}
		return data;
	}
	
	/**
	 * **************************************
	 * Recover state methods
	 * This is expected during initialization
	 * Already guarded by a thread due to singleton and factories
	 * Make sure code is there to call this method.
	 * **************************************
	 */
	private void recoverState()
	{
		//You may have an empty data set
		LoggedInUserKeysDataset data = dbObject.recover();
		
		//Must work with empty data stream
		populateStateFrom(data);
	}
	private void populateStateFrom(LoggedInUserKeysDataset data)
	{
		//Yes works with empty data stream
		for(PLSUserDetail ud:data.getData())
		{
			populateStateFromUserDetail(ud);
		}
	}
	private void populateStateFromUserDetail(PLSUserDetail ud)
	{
		//register the user interest
		this.registeredUserList.add(ud.userid);
		//unravel the keys
		for(String key:ud.randomKeyList)
		{
			//register each key against the user
			//<key,user>
			this.registeredUserKeys.put( key, ud.userid);
		}
		//thats it
	}
}//eof-class

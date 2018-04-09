package com.ai.aspire.authentication.pls;

import com.ai.servlets.AspireConstants;

/**
 * @author satya
 * To assist save and retrieve for logging keys
 *
 * @see PLSDataSetFilePersistence
 * @see LoggedInUserKeysDataset
 * @see PersistentLoginSupport2
 */
public interface IPLSDataSetPersistence 
{
	public static final String SELF = AspireConstants.AC_PLS_DATA_PERSISTENCE_OBJECT;
	public void save(LoggedInUserKeysDataset data);
	
	//will return an empty data set 
	public LoggedInUserKeysDataset recover();
}

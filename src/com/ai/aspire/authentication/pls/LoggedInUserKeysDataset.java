package com.ai.aspire.authentication.pls;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * 6/16/17
 * ***************
 * A class designed to hold only a data representation
 * of logged in users for the sake of persistent login state
 * of those users.
 * 
 * This class is testable in the IDE
 * Approach
 * **************
 * 1. Provide a representation of data that is close to xml
 * 
 * @see PLSDataSetFilePersistence
 * @see LoggedInUserKeysDataset
 * @see PersistentLoginSupport2
 * @see IPLSDataSetFilePersistence
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LoggedInUserKeysDataset {
	
	/*
	 * **************************************
	 * Private data
	 * **************************************
	 */
	@XmlElement (name="PLSUserDetail")
	private List<PLSUserDetail> data = new ArrayList<PLSUserDetail>();
	
	public void addUserDetail(PLSUserDetail ud){data.add(ud);}
	public List<PLSUserDetail> getData() { return data;}
	

	/*
	 * **************************************
	 * Testing support
	 * **************************************
	 */
	public static LoggedInUserKeysDataset createASample()
	{
		LoggedInUserKeysDataset lds = new LoggedInUserKeysDataset();
		
		PLSUserDetail ud = createASampleUserDetail("1");
		lds.data.add(ud);
		
		ud = createASampleUserDetail("2");
		lds.data.add(ud);
		
		ud = createASampleUserDetail("3");
		lds.data.add(ud);
		return lds;
	}
	private static PLSUserDetail createASampleUserDetail(String variation)
	{
		//Create user1
		PLSUserDetail ud = new PLSUserDetail("user" + variation);
		ud.addRandomKey("key1");
		ud.addRandomKey("key2");
		return ud;
	}
}//eof-class

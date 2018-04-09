package com.ai.aspire.authentication.pls;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class PLSUserDetail {

	public String userid;
	public boolean bPersistenLoginRequested;
	@XmlElementWrapper (name="random-keys")
	@XmlElement(name="random-key")
	public List<String> randomKeyList = new ArrayList<String>();
	
	//For xml maps and such
	public PLSUserDetail(){}
	
	//Just to keep it simple for simple cases
	public PLSUserDetail(String user)
	{
		userid = user;
		bPersistenLoginRequested = true;
	}
	public void addRandomKey(String key)
	{
		randomKeyList.add(key);
	}
}

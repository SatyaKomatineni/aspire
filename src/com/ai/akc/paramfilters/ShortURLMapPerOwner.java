package com.ai.akc.paramfilters;

import java.util.HashMap;
import java.util.Map;

import com.ai.application.utils.AppObjects;

public class ShortURLMapPerOwner 
{
	private String ownerUserId;
	
	//Map<string:path, string:url>
	Map urlmap = new HashMap();
	public ShortURLMapPerOwner(String inOwnerUserId)
	{
		ownerUserId = inOwnerUserId;
	}
	public void add(String path, String url)
	{
		AppObjects.trace(this,"Adding:%1s:%2s",path,url);
		urlmap.put(path,url);
	}
	public String get(String path)
	{
		return (String)urlmap.get(path);
	}
	public String getOwnerUserId(){ return ownerUserId;}
}

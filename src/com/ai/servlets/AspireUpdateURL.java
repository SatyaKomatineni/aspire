package com.ai.servlets;

import java.util.List;

import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;

public class AspireUpdateURL extends AspireURL
{
	protected String updateRequestName;
	public AspireUpdateURL(String inUpdateRequestname)
	{
		super("request." + inUpdateRequestname);
		updateRequestName = inUpdateRequestname;
	}
	public String getName()
	{
		return updateRequestName;
	}
}

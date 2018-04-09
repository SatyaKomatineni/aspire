package com.ai.servlets;

import java.util.List;

import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;

public class AspireDisplayURL extends AspireURL
{
	public AspireDisplayURL(String inDisplayURLname)
	{
		super(inDisplayURLname);
	}
	public String getName()
	{
		return this.baseConfigString;
	}
}

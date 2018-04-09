/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.filters;

import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.data.PipelineReturn;

// import java.util.ST
/**
 * to be done
 */
public class DiscontinuePipelineOnSuccessFilter implements ICreator
{
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
    	if (args == null)
    	{
    		return new PipelineReturn(null,true);
    	}
    	else
    	{
    		return new PipelineReturn(args,false);
    	}
   }
}//eof-class





package com.ai.data;

/**
 * To provide for two possible returns 
 *
 */
public class PipelineReturn
{
	   public boolean continueFlag = true;
	   public Object rtnObject = null;
	   public PipelineReturn(Object inObj, boolean inContinueFlag)
	   {
		   continueFlag = inContinueFlag;
		   rtnObject = inObj;
	   }
}

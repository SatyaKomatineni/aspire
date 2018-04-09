package com.ai.resourcecleanup;

import com.ai.common.CommonException;
import com.ai.data.IClosableResource;

public interface IResourceCleanupRegistry 
{
	public void addResource(IClosableResource icr)
	throws CommonException;
	public void removeResource(IClosableResource icr)
	throws CommonException;
	public void cleanup()
	throws CommonException;
}

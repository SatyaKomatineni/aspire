package com.ai.resourcecleanup;

import com.ai.application.utils.AppObjects;
import com.ai.common.CommonException;
import com.ai.data.IClosableResource;

public class SWIResourceCleanup 
{
	public static ThreadLocal m_ResourceRegistryTL = new ThreadLocal();
	
	public static IResourceCleanupRegistry getRegistry()
	{
		return (IResourceCleanupRegistry)m_ResourceRegistryTL.get();
	}
	private static void setRegistryOnThisThread()
	{
		IResourceCleanupRegistry ircr = getRegistry();
		if (ircr == null)
		{
			AppObjects.trace("com.ai.resourcecleanup.SWIResourceCleanup"
					,"Setting up registry for this thread");
			m_ResourceRegistryTL.set(new ResourceCleanupRegistry());
		}
	}
	private static void resetRegistry()
	{
		AppObjects.trace("com.ai.resourcecleanup.SWIResourceCleanup"
				,"Resetting Registry to null for this thread");
		m_ResourceRegistryTL.set(null);
	}
	
	public static void addResource(IClosableResource icr)
	throws CommonException
	{
		setRegistryOnThisThread();
		getRegistry().addResource(icr);
	}
	public static void removeResource(IClosableResource icr)
	throws CommonException
	{
		IResourceCleanupRegistry ircr = getRegistry();
		if (ircr == null)
		{
			AppObjects.warn("com.ai.resourcecleanup.SWIResourceCleanup"
					,"No registry to remove the resource from.");
			return;
		}
		getRegistry().removeResource(icr);
	}
	public static void cleanup()
	throws CommonException
	{
		IResourceCleanupRegistry ircr = getRegistry();
		if (ircr == null)
		{
			AppObjects.warn("com.ai.resourcecleanup.SWIResourceCleanup"
					,"No registry to cleanup.");
			return;
		}
		getRegistry().cleanup();
		resetRegistry();
	}
}//eof-class

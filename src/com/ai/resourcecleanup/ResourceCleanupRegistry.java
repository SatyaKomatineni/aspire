package com.ai.resourcecleanup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ai.application.utils.AppObjects;
import com.ai.common.CommonException;
import com.ai.data.IClosableResource;

public class ResourceCleanupRegistry implements IResourceCleanupRegistry
{
	//map<IClosableResource>
	private List m_resourceList = new ArrayList();
	
	public ResourceCleanupRegistry(){}
	public void addResource(IClosableResource icr)
	throws CommonException
	{
		m_resourceList.add(icr);
	}
	public void removeResource(IClosableResource icr)
	throws CommonException
	{
		m_resourceList.remove(icr);
	}
	public void cleanup()
	throws CommonException
	{
		int numOfResources = this.m_resourceList.size();
		AppObjects.trace(this, "Number of unclosed resources:%1s", numOfResources);
		if (numOfResources == 0)
		{
			return;
		}
		//There are resources to be cleaned up
		//You may have to copy the list so that a close won't alter this list
		List clonedList = new ArrayList();
		clonedList.addAll(m_resourceList);
		
		Iterator itr = clonedList.iterator();
		while(itr.hasNext())
		{
			IClosableResource icr = 
				(IClosableResource)itr.next();
			icr.close();
		}
		//By now the m_resourceList should be empty
		numOfResources = m_resourceList.size();
		if (numOfResources > 0)
		{
			AppObjects.warn(this,"There is a problem. After clean up there are still resources left:%1s", numOfResources);
			
		}
	}
}//eof-class

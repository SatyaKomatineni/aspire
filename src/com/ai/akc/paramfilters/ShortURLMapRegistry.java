package com.ai.akc.paramfilters;

import java.util.Hashtable;

import javax.servlet.ServletException;

import com.ai.application.utils.AppObjects;
import com.ai.cache.CacheUtils;
import com.ai.common.CommonException;
import com.ai.data.DataUtils;
import com.ai.data.FieldNameNotFoundException;
import com.ai.data.IDataCollection;
import com.ai.data.IDataRow;
import com.ai.data.IIterator;

public class ShortURLMapRegistry 
{
	public static String getUrl(String owner, String path)
	throws ServletException
	{
		ShortURLMapPerOwner sum = getShortURLMapPerOwner(owner);
		return sum.get(path);
	}
	public static ShortURLMapPerOwner
	getShortURLMapPerOwner(String owner)
	throws ServletException
	{
		String  cachekey = "/" + owner + "/shorturlmap";
		Object obj = CacheUtils.getObjectFromCache(cachekey);
		if (obj != null)
		{
			return (ShortURLMapPerOwner)obj;
		}
		ShortURLMapPerOwner sum = create(owner);
		CacheUtils.putObjectInCache(cachekey, sum);
		return sum;
	}//eof-function
	
	private static ShortURLMapPerOwner create(String owner)
	throws ServletException
	{
		IDataCollection col = null;
		try
		{
			//the object is not in the cache
			ShortURLMapPerOwner sum = new ShortURLMapPerOwner(owner);
			Hashtable args = new Hashtable();
			args.put("owneruserid", owner);
			col = (IDataCollection)AppObjects.getObject("GetShortURLsForUser", args);
			IIterator itr = col.getIIterator();
			for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
			{
				IDataRow idr = (IDataRow)itr.getCurrentElement();
				String path = idr.getValue("surl_map");
				String url = idr.getValue("surl_url");
				AppObjects.info("URL MapRetriever", "Adding mapping:" + path + ":" + url);
				sum.add(path, url);
			}
			return sum;
		}
		catch(CommonException ce)
		{
			throw new ServletException("Cannot get url mapper",ce);
		}
		catch(FieldNameNotFoundException ce)
		{
			throw new ServletException("Cannot get url mapper",ce);
		}
		finally
		{
			DataUtils.closeCollectionSilently(col);
		}
	}
	private static ShortURLMapPerOwner create1(String owner)
	throws ServletException
	{
		ShortURLMapPerOwner sum = new ShortURLMapPerOwner(owner);
		sum.add("/projects", "/display?url=DisplayNoteIMPURL&reportId=3329&ownerUserId=satya");
		return sum;
	}
}//eof-class

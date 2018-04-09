/*
 * Created on Dec 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.akc.security;

import java.util.Hashtable;

import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.data.DataException;
import com.ai.data.DataUtils;
import com.ai.data.FieldNameNotFoundException;
import com.ai.data.IDataCollection;
import com.ai.data.IDataRow;
import com.ai.data.IIterator;
import com.ai.db.DBException;
import com.ai.filters.FilterUtils;
import com.ai.parts.AspireLoginPart;
import com.ai.parts.DBProcedure;

/**
 * @author Satya
 *
 * 
select r.report_id, r.owner_user_id, f.public \
from reports r, folders f \
where 1=1 \
and fi.folder_id = f.folder_id \
and r.report_id = {reportId}

 */
public class DocumentSecurityProvider 
extends DBProcedure
{
	protected Object executeDBProcedure(String requestName, Hashtable arguments)
			throws DBException 
	{
		Document d = getDocument(arguments);
		if (d == null)
		{
			//no such document, deny access
			return new Boolean(false);
		}
		//document exists
		if (d.userDisabled == true)
		{
			//user is disabled, deny access
			return new Boolean(false);
		}
		//if it is public or private return true
		if (!(d.access.equals("secure")))
		{
			//document is not secure, it may be public or private
			//allow access
			return new Boolean(true);
		}
		
		//document is private
		//see if the user is logged in
		HttpSession session = this.getSession(arguments);
		if (session == null)
		{
			//No session, not loggedin, but a private document, deny access
			return new Boolean(false);
		}
		//session is there
		if (AspireLoginPart.isLoggedIn(session) == false)
		{
			//not logged in, but a private document, deny access
			return new Boolean(false);
		}
		//logged in
		String loggedInUser = AspireLoginPart.loggedInUser(session);
		if (loggedInUser.equals(d.owner))
		{
			//loggedin user is same as the owner, allow access
			return new Boolean(true);
		}
		//logged in user is different, deny access
		return new Boolean(false);
	}
	
	private DocumentSecurityProvider.Document 
	getDocument(Hashtable arguments)
	{
		IDataCollection dc = null;
		try
		{
			dc = (IDataCollection)AppObjects.getObject("documentsecurity",arguments);
			IIterator itr = dc.getIIterator();
			itr.moveToFirst();
			if (itr.isAtTheEnd())
			{
				//THere are no rows
				AppObjects.warn(this,"No document exist for this request");
				return null;
			}
			//There is atleast one row
			IDataRow idr = (IDataRow)itr.getCurrentElement();
			String owner = (String)idr.getValue("owner_user_id");
			String access = (String)idr.getValue("public");
			String secureAccess = (String)idr.getValue("secure");
			String docid = (String)idr.getValue("report_id");
			String userActive = (String)idr.getValue("active");
			Document d = new Document();
			d.id = docid;
			d.owner = owner;
			if (access.equalsIgnoreCase("y"))
			{
				d.access = "public";
			}
			else
			{
				d.access = "private";
			}
			if (FilterUtils.convertToBoolean(secureAccess,false) == true)
			{
				d.access = "secure";
			}
			if (FilterUtils.convertToBoolean(userActive,false) == true)
			{
				d.userDisabled = false;
			}
			else
			{
				d.userDisabled = true;
			}
			return d;
		}
		catch(RequestExecutionException x)
		{
			AppObjects.log("Error: Problem retrieveing the document object",x);
			//No document with that id
			return null;
		}
		catch(DataException x)
		{
			AppObjects.log("Error: Problem retrieveing the rows",x);
			//No document with that id
			return null;
		}
		catch(FieldNameNotFoundException x)
		{
			AppObjects.log("Error: Problem retrieveing the rows",x);
			//No document with that id
			return null;
		}
		finally
		{
			DataUtils.closeCollectionSilently(dc);
		}
	}
	private HttpSession getSession(Hashtable args)
	{
		return (HttpSession)args.get("aspire_session");
	}
	public class Document 
	{
		public String id;
		public String owner;
		public String access;
		public boolean userDisabled;
	}//eof-class
}//eof-class

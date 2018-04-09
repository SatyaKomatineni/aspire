/*
 * Created on Nov 25, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.db.rel2;

import java.sql.SQLException;
import java.util.Map;

import com.ai.application.interfaces.RequestExecutionException;

/**
 * @author Satya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IProcedureBaseExtender 
{
	   public Object executeRequestForPart(DBBaseJavaProcedure3 proc
	   		,String requestName
			, Map inArgs)
	   throws RequestExecutionException, SQLException;
}

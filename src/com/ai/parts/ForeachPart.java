package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.*;
import com.ai.data.DataUtils;
import com.ai.data.IDataCollection;
import com.ai.data.IDataRow;
import com.ai.data.IIterator;

import java.io.*;
import java.util.Map;
/**
 * Executes an individual request for each row in the collection
 *
 * Additional property file arguments
 * 1. collectionRequestName
 * 2. individualRequestName
 *
 * Output
 * 1.resultName: Boolean(true)
 *
 */

public class ForeachPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
        throws RequestExecutionException
    {
        PrintWriter w = null;
        String aspireFilename = null;
        try
        {
        //mandatory args
            String collectionRequestName = AppObjects.getValue(requestName + ".collectionRequestName");
            String individualRequestName = AppObjects.getValue(requestName + ".individualRequestName");
            
            IDataCollection col = (IDataCollection)AppObjects.getObject(collectionRequestName,inArgs);
            IIterator itr = col.getIIterator();
            for(itr.moveToFirst();
            	!itr.isAtTheEnd();
            	itr.moveToNext())
            {
            	IDataRow curRow = (IDataRow)itr.getCurrentElement();
            	AppObjects.info(this,"Executing individual Request:%1s", individualRequestName);
            	DataUtils.execRequestUsingDataRow(individualRequestName,curRow,inArgs);
            }
            return new Boolean(true);
        }
        catch(CommonException x)
        {
            throw new RequestExecutionException("Error: during the execution of individual requests", x);
        }
    }
}

package com.ai.parts;

import com.ai.application.interfaces.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.data.*;
import com.ai.servlets.compatibility.ServletCompatibility;

import javax.servlet.http.*;

/**
 * Load a series of variables into session
 *
 * Additional property file arguments
 * 1. loadVariablesRequestName: request name to get a data collection
 * 2. session: Session object
 *
 * Output
 * 1.resultName: true if successful, exception otherwise
 *
 * Expected args in the hashtable
 * 1. aspire_session: This key should be set to the HttpSession
 */

public class SessionLoaderPart extends AFactoryPart implements IInitializable
{
   private String m_sessionVarRequestName = null;

   public void initialize(String requestName)
   {
      m_sessionVarRequestName = AppObjects.getValue(requestName + ".loadVariablesRequestName",null);
   }
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
       // If there is no session request name to call
       // to load variables return with true
       if (m_sessionVarRequestName == null) return new Boolean(true);


       //Prepare to call the session request
       IDataCollection dataCol = null;
       HttpSession session = (HttpSession)inArgs.get("aspire_session");
       if (session == null)
       {
          AppObjects.log("Error:Session variable 'aspire_session' missing in the input");
          return new Boolean(true);
       }
       try
       {
          //Execute for data collection
          dataCol = (IDataCollection)AppObjects.getObject(m_sessionVarRequestName,inArgs);

          //Get the iterator
          IIterator itr = dataCol.getIIterator();
          itr.moveToFirst();

          //If there is no data return true
          if (itr.isAtTheEnd() == true)
          {
             //No data
             return new Boolean(true);
          }

          // data is there
          IDataRow dr = (IDataRow)itr.getCurrentElement();
          IMetaData colCol = dataCol.getIMetaData();
          IIterator colItr = colCol.getIterator();

          //for each columns retrieve the value and put it in the session
          //Convert the keys to lower case
          for(colItr.moveToFirst();!colItr.isAtTheEnd();colItr.moveToNext())
          {
             //for each columns
             String colName = (String)colItr.getCurrentElement();
             String colValue = dr.getValue(colName);
             ServletCompatibility.putSessionValue(session,colName.toLowerCase(),colValue);
          }
          return new Boolean(true);
       }
       catch(DataException x)
       {
          throw new RequestExecutionException("Error:Data Exception " + x.getRootCause(),x);
       }
       catch(FieldNameNotFoundException x)
       {
          throw new RequestExecutionException("Error:Field name not found Exception ",x);
       }

       finally
       {
          //Close the collection finally
          if (dataCol != null)
          {
             try
             {
                dataCol.closeCollection();
             }
             catch(DataException x)
             {
                AppObjects.log("Error: data error",x);
             }
          }
       }

    }//eof-function
}//eof-class


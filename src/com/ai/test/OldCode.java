package com.ai.test;

/**
 * <p>Title: Your Product Name</p>
 * <p>Description: Your description</p>
 * <p>Copyright: Copyright (c) 1999</p>
 * <p>Company: Your Company</p>
 * @author Your Name
 * @version
 */

public class OldCode
{

   public OldCode()   {   }

/*
******************************************************************************
*
* Aug 28th 2004, RequestExecutorServletT2
* *******************************************
*
public boolean processValidation(String user,
                           HttpSession session,
                             String uri,
                             String query,
                             Hashtable parameters,
                             PrintWriter out
                             ,HttpServletRequest request
                             ,HttpServletResponse response )
   throws ServletException,IOException
{
    String validateRequestName = getValidateRequestName(request);
    if (validateRequestName == null)
    {
       //There is no validation requested
       return true; // continue
    }
    //validation request available
    IDataCollection errorSet = (IDataCollection)AppObjects.getObject(validateRequestName,parameters);

    //Nature of the collection: metadata
    //fieldname, error
    if (empty(errorSet))
    {
       //There are no rows
       //Assume the fields are good
       return true;//valid fields
    }
    //There are some errors
    logErrors(errorSet);

    //Find the referer redirect url string
    String preSubstRefererURLString = getRefererURL(requestName);

    String substitutedReferalURL = getSubstitutedReferalURL(preSubstRefererURL, parameters);

    Map errorMap = getFieldErrorMap(errorSet);

    internalRedirect(substitutedReferalURL, errorMap, parameters);
    return false;
}//end of processvalidation

**
 * Validation failure
 *
void sendValidationFailure(PrintWriter out
                  , String requestName
                  , IFormHandler errorDataSet)
{
   try {
       // get the page name
       String failurePage = ServletUtils.translateFileIdentifier("RequestExecutor.validationFailurePage");
       if (errorDataSet != null)
       {
         ((IAITransform)PageDispatcherServlet.getTransformObject("NoURL")).transform(failurePage
                                                              ,out
                                                              ,errorDataSet );
       }
       else
       {
         AppObjects.log("re_s: No error data set specified");
       }
   }
   catch(com.ai.application.interfaces.ConfigException x)
   {
      AppObjects.log("re_s: No validation failure page mentioned");
      PrintUtils.writeException(out,x);
   }
   catch(java.io.IOException x)
   {
      PrintUtils.writeException(out,x);
   }
}

 *
 * Return null if there are no errors
 *
private IFormHandler validateAndGetErrorDataSet(String validateReq, Hashtable parms )
      throws com.ai.common.CommonException, com.ai.application.interfaces.ConfigException
{
     Object obj = AppObjects.getIFactory().getObject(validateReq, parms);
     IDataCollection col = (IDataCollection)obj;
     if (col.getIIterator().isAtTheEnd())
     {
       col.closeCollection();
       return null;
     }
     ConstructableFormHandler dataSet = new ConstructableFormHandler("ErrorSetForm",null);
     dataSet.addArgument("request_name",validateReq);
     dataSet.addControlHandler("ErrorSet"
                        , new GenericTableHandler1("ErrorSet", dataSet, col ));
     return dataSet;
}

package com.ai.servlets;

import javax.servlet.http.*;
import java.util.*;
import javax.servlet.*;
import java.io.*;

public interface IRequestValidator
{

   public boolean processValidation(String user,
                              HttpSession session,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
      throws ServletException,IOException;
}
   package com.ai.servlets;

import javax.servlet.http.*;
import java.util.*;
import javax.servlet.*;
import java.io.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;

import com.ai.data.*;

public class DRequestValidator implements IRequestValidator, IInitializable
{

   private String m_requestName = null;
   private String m_fieldValidatorRequestName = null;
   private String m_failureRedirectURL=null;
   private String m_redirectType = null;

   public void initialize(String requestName)
   {
      m_requestName = requestName;

      //mandatory
      m_fieldValidatorRequestName = AppObjects.getValue(requestName + ".fieldValidatorRequestName");

      //mandatory
      m_failureRedirectURL = AppObjects.getValue(requestName + ".failureRedirectURL");

      //redirection type: serverside default
      m_redirectType = AppObjects.getValue(requestName + ".redirectType","serverside");
   }

   private boolean isEmpty(IDataCollection col)
   {
      IIterator itr = col.getIIterator();
      if (itr.isAtTheEnd() == true) return true;
   }

   private void logErrors(IDataCollection errorSet)
   {

   }
   public boolean processValidation(String user,
                              HttpSession session,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
      throws ServletException,IOException
   {
       //validation request available
       IDataCollection errorSet =
             (IDataCollection)
             AppObjects.getObject(this.m_fieldValidatorRequestName,parameters);

       //Nature of the collection: metadata
       //fieldname, error
       if (isEmpty(errorSet))
       {
          //There are no rows
          //Assume the fields are good
          return true;//valid fields
       }
       //There are some errors
       logErrors(errorSet);

       //Find the referer redirect url string
       String preSubstRefererURLString = getRefererURL(requestName);

       String substitutedReferalURL = getSubstitutedReferalURL(preSubstRefererURL, parameters);

       Map errorMap = getFieldErrorMap(errorSet);

       internalRedirect(substitutedReferalURL, errorMap, parameters);
       return false;
   }//end of processvalidation

   String getValidateRequestName(Map parameters)
   {

   }
}
******************************************************************************
*/
}
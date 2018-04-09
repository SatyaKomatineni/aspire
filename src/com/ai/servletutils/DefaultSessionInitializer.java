package com.ai.servletutils;

import com.ai.aspire.authentication.*;
import com.ai.application.interfaces.*;
import javax.servlet.http.*;
import com.ai.application.utils.*;
import com.ai.servlets.*;
import java.util.*;

import com.ai.data.*;

/**
 * How to use DefaultSessionInitializer
 * This class gets executed when a session is established for a user
 *
 * request.aspire.authentication.sessionInitializer.classname=com.ai.servletutils.DefaultSessionInitializer
 * request.aspire.authentication.sessionInitializer.loadVariablesRequestName=YourRequestName
 *
 * request.YourRequestName.classname=com.ai.db.DBRequestExecutor2
 * request.YourRequestName.db=dbname
 * request.YourRequestName.stmt=select * from abc
 *
 * Arguments
 * ***********
 * 1. Input is the profile_user
 * 2. If the user is annonymous then the hashtable is empty
 *
 * Other java classes
 * ********************
 * ISessionInit
 * SessionUtils
 * AspireConstants
 * BaseServlet
 */
public class DefaultSessionInitializer implements ISessionInit, ICreator, IInitializable
{
   public String m_sessionVarRequestName = null;

   public void initialize(String requestName)
   {
      m_sessionVarRequestName = AppObjects.getValue(requestName + ".loadVariablesRequestName",null);
   }
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
       return this;
  }

   public DefaultSessionInitializer()
   {
   }

   /**
    * Load a set of variables into session
    * Look for a request name to execute
    * Thre request is expected to produce an IDataCollection
    * Only one row is expected from this collection
    */
   public boolean initialize(HttpSession session
                           ,HttpServletRequest request
                           ,HttpServletResponse response)
      throws AspireServletException
   {
      return true;
   }
}//eof-class
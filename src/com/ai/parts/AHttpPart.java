package com.ai.parts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.AFactoryPart;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.servlets.AspireConstants;

/*
 * Note this class is a singleton.
 * Don't save state in instance variables.
 * 
 * @see ICreator
 * @see ISingleThreaded
 * @see IInitializable 
 * @see FilterEnabledFactory4
 * 
 */
public abstract class AHttpPart extends AFactoryPart
{

   protected Object executeRequestForPart(String requestName, Map inArgs)
       throws RequestExecutionException
   {
      HttpServletRequest request   = (HttpServletRequest)inArgs.get(AspireConstants.ASPIRE_HTTP_REQUEST_KEY);
      HttpServletResponse response = (HttpServletResponse)inArgs.get(AspireConstants.ASPIRE_HTTP_RESPONSE_KEY);
      HttpSession session          = (HttpSession)inArgs.get(AspireConstants.ASPIRE_HTTP_SESSION_KEY);

      return executeRequestForHttpPart(requestName, request, response, session, inArgs);
   }
   protected abstract Object executeRequestForHttpPart(String requestName
         ,HttpServletRequest request
         ,HttpServletResponse response
         ,HttpSession session
         ,Map inArgs)
         throws RequestExecutionException;
}
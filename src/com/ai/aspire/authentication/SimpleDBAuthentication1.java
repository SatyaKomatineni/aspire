package com.ai.aspire.authentication;

import com.ai.aspire.authentication.*;
import com.ai.application.utils.*;
import com.ai.servlets.AspireConstants;
import java.util.*;
import com.ai.filters.*;
import com.ai.application.interfaces.*;

/**
 * Performs authorization using a reusable request as follows

#
# Specify authentication class
#
request.aspire.authentication.authenticationObject.className=\
   com.ai.aspire.authentication.SimpleDBAuthentication1
#
# Actual authentication request
#

request.aspire.authentication.authenticateUser.className=com.ai.db.DBRequestExecutor2
request.aspire.authentication.authenticateUser.filterName=filters.SingleRowHashtableFilter
request.aspire.authentication.authenticateUser.db=ptrDB
request.aspire.authentication.authenticateUser.stmt= \
      select 'true' as result             \
      from users                     \
      where user_id = '{userid}'     \
      and password = '{password}'

 * Inputs: none
 * multiplicity: Singleton
 */

 public class SimpleDBAuthentication1 extends PublicAccessAuthentication
 {
   public SimpleDBAuthentication1()
   {
   }
   public boolean verifyPassword(final String userid, final String passwd )
      throws AuthorizationException
   {
      try
      {
         Hashtable args = new Hashtable();
         args.put("userid",userid);
         args.put("password",passwd);
         Object reply = AppObjects.getIFactory().getObject(AspireConstants.AUTHENTICATE_USER,args);
         return  FilterUtils.convertToBoolean(reply);
      }
      catch(com.ai.application.interfaces.RequestExecutionException x)
      {
         throw new AuthorizationException(AuthorizationException.CANNOT_ACCESS_AUTHORIZATION_SERVERS,x);
      }
      catch(com.ai.common.UnexpectedTypeException x)
      {
         throw new AuthorizationException("auth: Authorization service returned a wrong object" ,x);
      }
   }
}


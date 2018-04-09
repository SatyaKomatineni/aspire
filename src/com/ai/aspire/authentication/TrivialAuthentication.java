/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.authentication;

import javax.servlet.http.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import com.ai.servlets.AspireConstants;
import java.util.*;
import com.ai.common.*;
import com.ai.servletutils.*;

public class TrivialAuthentication extends PublicAccessAuthentication implements IAuthentication2, ICreator, IInitializable
{
   public boolean verifyPassword(final String userid, final String passwd )
      throws AuthorizationException
   {
      String password =
      AppObjects.getIConfig().getValue(AspireConstants.PASSWORD,null);
      if (password == null) {return true;}
      // password has been mentioned
      return passwd.equals(password);
   }
}
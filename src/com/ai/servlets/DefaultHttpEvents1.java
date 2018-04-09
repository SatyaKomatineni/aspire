package com.ai.servlets;

import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import javax.servlet.http.*;
import java.util.*;

public class DefaultHttpEvents1 extends DefaultHttpEvents
{
   public boolean userLogin(String username,  HttpSession session, HttpServletRequest request, HttpServletResponse response)
         throws AspireServletException
   {
      AppObjects.log("Info:user loggedin event");

      Hashtable args = new Hashtable();
      args.put("aspire_session",session);
      args.put("profile_user",username);
      try
      {
         AppObjects.getObject(AspireConstants.SESSION_SUPPORT_NEW_USER_SESSION_LOADER,args);
         return true;
      }
      catch(RequestExecutionException x)
      {
         throw new AspireServletException("Error:" + x.getRootCause(), x);
      }
   }//eof-function
}//eof-class
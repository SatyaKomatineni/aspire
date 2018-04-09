package com.ai.aspire.authentication;
import com.ai.servlets.AspireConstants;
import javax.servlet.http.*;
import com.ai.servlets.*;

public interface ISessionInit 
{
   static public String NAME=AspireConstants.SESSION_INITIALIZER;
   public boolean initialize(HttpSession session
                           ,HttpServletRequest request
                           ,HttpServletResponse response)
      throws AspireServletException;                           
                           
} 

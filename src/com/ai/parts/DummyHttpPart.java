package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.Map;
import com.ai.servlets.AspireConstants;

public class DummyHttpPart extends AHttpPart
{

   protected Object executeRequestForHttpPart(String requestName
         ,HttpServletRequest request
         ,HttpServletResponse response
         ,HttpSession session
         ,Map inArgs)
         throws RequestExecutionException
   {
      AppObjects.info(this,"Test message");
      return new Boolean(true);
   }
}
package com.ai.servlets;

import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import javax.servlet.http.*;
import java.util.*;

public class HttpRequestCharacterEncodingHandler extends DefaultHttpEvents
{
   public boolean beginRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
      try
      {
         String enc = request.getCharacterEncoding();
         if (enc == null)
         {
            String encoding = AppObjects.getValue(m_requestName + ".encoding", "UTF-8");
            request.setCharacterEncoding(encoding);
            AppObjects.info(this,"setting encoding to %1s",encoding);
         }
         return true;
      }
      catch(java.io.UnsupportedEncodingException x)
      {
         throw new AspireServletException("Error: Encoding error while setting http request char encoding",x);
      }
   }//eof-function

}//eof-class

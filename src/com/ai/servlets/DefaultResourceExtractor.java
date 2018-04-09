/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import javax.servlet.http.*;
import com.ai.application.interfaces.*;

public class DefaultResourceExtractor implements IResourceExtractor, ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
   public String extractResource(HttpServletRequest request)
   {
      String url = request.getParameter("url");
      if (url != null) return url;
      String request_name = request.getParameter("request_name");
      return request_name;
   }
}     

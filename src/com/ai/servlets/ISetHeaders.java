package com.ai.servlets;
import javax.servlet.http.*;

public interface ISetHeaders 
{
   public void setHeaders(String displayURL
         , HttpServletRequest request
         , HttpServletResponse response)
         throws AspireServletException;
} 

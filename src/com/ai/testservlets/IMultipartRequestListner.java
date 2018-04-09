/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.testservlets;
import javax.servlet.http.*;

public interface IMultipartRequestListner 
{
   public boolean beginProcess(HttpServletRequest request, MultipartInputStreamHandler stream);
   public boolean newParameter(String name, String value );
   public boolean newFile(String filename);
   public String suggestAFilename(String filename );
   
} 

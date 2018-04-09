/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import javax.servlet.http.*;

public interface IResourceExtractor 
{
   static public String NAME=AspireConstants.RESOURCE_EXTRACTION_OBJECT;
   public String extractResource(HttpServletRequest request);
}        

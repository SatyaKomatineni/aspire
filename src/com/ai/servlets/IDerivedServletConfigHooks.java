/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import java.io.*;
import javax.servlet.http.*;

/**
 * Provide the base servlet class to configure itself based
 * on the special requirements of the derived classes
 *
 * Nomenclature:
 *
 * methods starting with 'q' - qualifier
 * qh - hook function
 * qhd - a default will be provided
 */
public interface IDerivedServletConfigHooks 
{
   public PrintWriter qhdGetPrintWriter(HttpServletRequest request, HttpServletResponse response) throws IOException;
   public String qhdGetContentType(HttpServletRequest request, HttpServletResponse response);
} 

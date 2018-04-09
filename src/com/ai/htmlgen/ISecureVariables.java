/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.htmlgen;

import java.util.Enumeration;
import com.ai.servlets.AspireConstants;
/**
 * Introduced to distinguish between secure variables and non-secure variables
 * The variables that are being discussed here are session variables
 * Secure variable can be set only from the server side
 * Non-secure variables can be set from both client side and server side
 * This is to protect the secure variables from being changed by url masquerading
 */
public interface ISecureVariables 
{
   public final String NAME= AspireConstants.DEFAULT_OBJECTS_CONTEXT + ".SecureVariables";
   public boolean isASecureVariable(final String variableName);
   public Enumeration list();
} 
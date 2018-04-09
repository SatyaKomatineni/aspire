/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import java.io.*;
import com.ai.common.Tokenizer;


/**
 * Introduced to distinguish between secure variables and non-secure variables
 * The variables that are being discussed here are session variables
 * Secure variable can be set only from the server side
 * Non-secure variables can be set from both client side and server side
 * This is to protect the secure variables from being changed by url masquerading
 *
 * Property file parameters
 * *************************
 *
 * Aspire.secureVariables=<comma separated list of paramenters>
 * request.Aspire.DefaultObjects.SecureVariables.className=com.ai.htmlgen.CSecureVariables
 *
 */
public class CSecureVariables implements ISecureVariables, ICreator
{
   private Hashtable m_variables;
   public CSecureVariables() 
   {
      String variableString =
         AppObjects.getIConfig().getValue("Aspire.secureVariables",null);
      if (variableString != null)
      {
         m_variables = Tokenizer.tokenizeAsAHashtable(variableString.toLowerCase(),",");
         AppObjects.info(this,"info: Secure variables are: %1s",m_variables.toString());
      }
      else
      {  
         AppObjects.warn(this,"No secure variables specified");
         m_variables = new Hashtable();
      }
               
   }
   public Object executeRequest(String requestName, Object args)
          throws RequestExecutionException
   {
      return this;      
   }
   public boolean isASecureVariable(final String variableName)
   {
      Object obj = m_variables.get(variableName.toLowerCase());
      return  (obj == null) ? false:true;
   }
   public Enumeration list()
   {
      if (m_variables == null) return null;
      return m_variables.keys();
   }
}   
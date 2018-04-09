/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import java.util.*;

public class UserProfile 
{
  private Hashtable m_parameters = new Hashtable();
  private String m_userName;
  public UserProfile(String inUserName) 
  {
   m_userName = inUserName;
  }
  public void setParameter(String key, String value)
  {
      m_parameters.put(key,value);
  }
  
  public String getParameter(String key)
  {
      return (String)m_parameters.get(key);
  }
  public Hashtable getParameters()
  {
   return m_parameters;
  }
  
} 
/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servletutils;
import java.util.*;

public class CURL implements IURL 
{
   private String m_url;
   public CURL(final String url) 
   {
      m_url = url;
   }
   public String protocol()
   {
      return "";
   }
   public String host()
   {
      return "";
   }
   public String port()
   {
      return "";
   }
   public String uri()
   {
      return "";
   }
   public String queryString(){ return ""; }
   public Hashtable params(){ return null;}
   public String url(){ return "";}
} 